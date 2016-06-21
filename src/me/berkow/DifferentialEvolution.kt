package me.berkow

import java.util.*

val LOWER_CONSTRAINTS = "lower_constraints"
val UPPER_CONSTRAINTS = "upper_constraints"
val CROSSOVER_PROBABILITY = "cr"
val AMPLIFICATION = "f"
val LOWER_AMPLIFICATION = "lower_amplification"
val UPPER_AMPLIFICATION = "upper_amplification"
val CROSSOVER_PROBABILITIES = "crossover_probabilities"
val AMPLIFICATIONS = "amplifications"
val T1 = "t1"
val T2 = "t2"

fun main(args: Array<String>) {
    val seed  = System.nanoTime()
    val random1 = Random(seed)
    val random2 = Random(seed)
    val random3 = Random(seed)

    val problemSize = 10
    val lowerConstraints = DoubleArray(problemSize, { -100.0 })
    val upperConstraints = DoubleArray(problemSize, { 100.0 })
    val maxGenerations = 1000

    val populationSize = problemSize * 6
    val function = F1_FUNCTION
    val precision = 1e-12

    val probableSolution = DoubleArray(problemSize, { 0.0 }).asList()
    val initialSolution = Array(populationSize, { createRandomVector(lowerConstraints, upperConstraints, Random()) })
    val amplification = 0.9
    val crossoverProbability = 0.5

    val deParameters = mapOf(
            LOWER_CONSTRAINTS to lowerConstraints,
            UPPER_CONSTRAINTS to upperConstraints,
            AMPLIFICATION to amplification,
            CROSSOVER_PROBABILITY to crossoverProbability
    )
    val evaluationsForDE = differentialEvolution(deParameters, initialSolution, ::de, maxGenerations, precision,
            memorize(function), random1, probablySolution = probableSolution)

    val lowerF = 0.1
    val upperF = 0.9
    val t1 = 0.1
    val t2 = 0.1
    val sadeParameters = mapOf(
            LOWER_CONSTRAINTS to lowerConstraints,
            UPPER_CONSTRAINTS to upperConstraints,
            AMPLIFICATIONS to LinkedList<Double>(listOf(upperF)),
            CROSSOVER_PROBABILITIES to LinkedList<Double>(listOf(0.5)),
            T1 to t1,
            T2 to t2,
            LOWER_AMPLIFICATION to lowerF,
            UPPER_AMPLIFICATION to upperF
    )
    val evaluationsForSDE = differentialEvolution(sadeParameters, initialSolution, ::sade, maxGenerations, precision,
            memorize(function), random3, probablySolution = probableSolution)

    val tdeParameters = mapOf(
            LOWER_CONSTRAINTS to lowerConstraints,
            UPPER_CONSTRAINTS to upperConstraints,
            AMPLIFICATION to amplification,
            CROSSOVER_PROBABILITY to crossoverProbability
    )
    val evaluationsForTDE = differentialEvolution(tdeParameters, initialSolution, ::tde, maxGenerations, precision,
            memorize(function), random2, probablySolution = probableSolution)


    println("evaluationsForDE = $evaluationsForDE, evaluationsForSDE = $evaluationsForSDE, evaluationsForTDE = $evaluationsForTDE")
}


fun differentialEvolution(parameters: Map<String, Any>, initialPopulation: Array<List<Double>>,
                          newGenerationBlock: (Map<String, Any>, Array<List<Double>>, Random, MemorizeFunction<List<Double>, Double>) -> Array<List<Double>>,
                          maxGenerationsCount: Int, precision: Double, function: MemorizeFunction<List<Double>, Double>,
                          random: Random, stagnationThreshold: Int = initialPopulation.size,
                          probablySolution: List<Double>?): Int {
    var previousVectors = initialPopulation

    var previousAverageCost = previousVectors.map { function(it) }.average()
    var stagnationCount = 0
    println("initial average = ${previousAverageCost}")

    var stopTrigger = 0
    var stopGeneration = maxGenerationsCount
    loop@ for (generation in 1..maxGenerationsCount) {
        val newVectors = newGenerationBlock(parameters, previousVectors, random, function)

        val currentAverageCost = newVectors.map { function(it) }.average()
//        println("currentAverageCost = ${currentAverageCost}")

        if (Math.abs(currentAverageCost - previousAverageCost) < precision) {
            stagnationCount++
        } else {
            stagnationCount = 0
        }

        previousAverageCost = currentAverageCost
        previousVectors = newVectors

        if (stagnationCount >= stagnationThreshold) {
            stopTrigger = 1
            stopGeneration = generation
            break@loop
        }

        if (probablySolution != null && Math.abs(function(probablySolution) - currentAverageCost) < precision) {
            stopTrigger = 2
            stopGeneration = generation
            break@loop
        }
    }

    when (stopTrigger) {
        0 -> println("Stopped due reaching max generations count!")
        1 -> println("Stopped due stagnation at $stopGeneration!")
        2 -> println("Stopped due to approximating probable solution at $stopGeneration!")
    }

    println("average cost = ${previousAverageCost}")

    return function.evalutionsCount
}

fun de(parameters: Map<String, Any>, previousVectors: Array<List<Double>>,
       random: Random, function: MemorizeFunction<List<Double>, Double>): Array<List<Double>> {
    val populationSize = previousVectors.size
    val problemSize = previousVectors[0].size

    val lowerConstraints = parameters[LOWER_CONSTRAINTS] as DoubleArray
    val upperConstraints = parameters[UPPER_CONSTRAINTS] as DoubleArray
    val amplification = parameters[AMPLIFICATION] as Double
    val crossoverProbability = parameters[CROSSOVER_PROBABILITY] as Double

    return Array(populationSize, { index ->
        val vector = previousVectors[index]
        val r = getRandomValues(3, 0, populationSize, setOf(index), random).toIntArray()
        val mandatoryIndex = random.nextInt(problemSize)
        val newVector = DoubleArray(problemSize, { j ->
            val newValue = if (mandatoryIndex == j || random.nextDouble() < crossoverProbability) {
                previousVectors[r[0]][j] + amplification * (previousVectors[r[1]][j] - previousVectors[r[2]][j])
            } else {
                vector[j]
            }
            val min = lowerConstraints[j]
            val max = upperConstraints[j]
            if (newValue < min || max < newValue) random.nextDouble(min, max) else newValue
        }).asList()

        if (function(newVector) < function(vector)) newVector else vector
    })
}

fun sade(parameters: Map<String, Any>, previousVectors: Array<List<Double>>,
         random: Random, function: MemorizeFunction<List<Double>, Double>): Array<List<Double>> {
    val populationSize = previousVectors.size
    val problemSize = previousVectors[0].size
    val lowerConstraints = parameters[LOWER_CONSTRAINTS] as DoubleArray
    val upperConstraints = parameters[UPPER_CONSTRAINTS] as DoubleArray

    val lowerF = parameters[LOWER_AMPLIFICATION] as Double
    val upperF = parameters[UPPER_AMPLIFICATION] as Double
    val t1 = parameters[T1] as Double
    val t2 = parameters[T2] as Double
    val amplifications = parameters[AMPLIFICATIONS] as LinkedList<Double>
    val crossoverProbabilities = parameters[CROSSOVER_PROBABILITIES] as LinkedList<Double>

    val F = if (random.nextDouble() < t1) lowerF + random.nextDouble() * upperF else amplifications.last()
    val CR = if (random.nextDouble() < t2) random.nextDouble() else crossoverProbabilities.last()
    amplifications.add(F)
    crossoverProbabilities.add(CR)

    return Array(populationSize, { index ->
        val vector = previousVectors[index]
        val r = getRandomValues(3, 0, populationSize, setOf(index), random).toIntArray()
        val mandatoryIndex = random.nextInt(problemSize)
        val newVector = DoubleArray(problemSize, { j ->
            val newValue = if (mandatoryIndex == j || random.nextDouble() < CR) {
                previousVectors[r[0]][j] + F * (previousVectors[r[1]][j] - previousVectors[r[2]][j])
            } else {
                vector[j]
            }
            val min = lowerConstraints[j]
            val max = upperConstraints[j]
            if (newValue < min || max < newValue) random.nextDouble(min, max) else newValue
        }).asList()

        if (function(newVector) < function(vector)) newVector else vector
    })
}

fun tde(parameters: Map<String, Any>, previousVectors: Array<List<Double>>,
        random: Random, function: MemorizeFunction<List<Double>, Double>): Array<List<Double>> {
    val populationSize = previousVectors.size
    val problemSize = previousVectors[0].size

    val lowerConstraints = parameters[LOWER_CONSTRAINTS] as DoubleArray
    val upperConstraints = parameters[UPPER_CONSTRAINTS] as DoubleArray
    val amplification = parameters[AMPLIFICATION] as Double
    val crossoverProbability = parameters[CROSSOVER_PROBABILITY] as Double

    return Array(populationSize, { index ->
        val vector = previousVectors[index]
        val r = getRandomValues(3, 0, populationSize, setOf(index), random).toIntArray()

        val p = r.map { previousVectors[it] }.map { function(it) }.map { Math.abs(it) }.sum()
        val p1 = Math.abs(function(previousVectors[r[0]])) / p
        val p2 = Math.abs(function(previousVectors[r[1]])) / p
        val p3 = Math.abs(function(previousVectors[r[2]])) / p

        val mandatoryIndex = random.nextInt(problemSize)
        val newVector = DoubleArray(problemSize, { j ->
            val newValue = if (mandatoryIndex == j || random.nextDouble() < crossoverProbability) {
                val x1 = previousVectors[r[0]][j]
                val x2 = previousVectors[r[1]][j]
                val x3 = previousVectors[r[2]][j]

                (x1 + x2 + x3) / 3.0 + (p2 - p1) * (x1 - x2) + (p3 - p2) * (x2 - x3) + (p1 - p3) * (x3 - x1)
            } else {
                previousVectors[r[0]][j] + amplification * (previousVectors[r[1]][j] - previousVectors[r[2]][j])
            }
            val min = lowerConstraints[j]
            val max = upperConstraints[j]

            if (newValue < min || max < newValue) random.nextDouble(min, max) else newValue
        }).asList()

        if (function(newVector) < function(vector)) newVector else vector
    })
}