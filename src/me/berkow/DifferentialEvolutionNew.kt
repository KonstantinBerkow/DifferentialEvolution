package me.berkow

import java.util.*

val RANDOM = "random"
val PROBLEM_SIZE = "problem_size"
val LOWER_CONSTRAINTS = "lower_constraints"
val UPPER_CONSTRAINTS = "upper_constraints"
val MAX_GEN = "max_gen"
val POP_SIZE = "pop_size"
val CROSSOVER_PROBABILITY = "cr"
val AMPLIFICATION = "f"
val FUNCTION = "function"
val PRECISION = "eps"
val INITIAL_SOLUTION = "initial_solution"
val LOWER_AMPLIFICATION = "lower_amplification"
val UPPER_AMPLIFICATION = "upper_amplification"
val CROSSOVER_PROBABILITIES = "crossover_probabilities"
val AMPLIFICATIONS = "amplifications"
val T1 = "t1"
val T2 = "t2"

fun main(args: Array<String>) {
    val random1 = Random(233313L)
    val random2 = Random(233313L)
    val random3 = Random(233313L)

    val problemSize = 30
    val lowerConstraints = DoubleArray(problemSize, { -100.0 })
    val upperConstraints = DoubleArray(problemSize, { 100.0 })
    val maxGenerations = 1500

    val populationSize = problemSize * 6
    val amplification = 0.9
    val crossoverProbability = 0.5
    val function = F2_FUNCTION
    val precision = 1e-6

//    solveDE(maxGenerations, populationSize, random1, problemSize, crossoverProbability,
//            amplification, lowerConstraints, upperConstraints, "DE.csv", function)
//
//    val lowerAmplificationFactor = 0.4
//    solveWDE(maxGenerations, populationSize, random2, problemSize, crossoverProbability,
//            lowerAmplificationFactor, lowerConstraints, upperConstraints, "WDE.csv", function)
//

    val initialSolution = Array(populationSize, { createRandomVector(lowerConstraints, upperConstraints, Random()) })

    val deParameters = mapOf(
            LOWER_CONSTRAINTS to lowerConstraints,
            UPPER_CONSTRAINTS to upperConstraints,
            AMPLIFICATION to amplification,
            CROSSOVER_PROBABILITY to crossoverProbability
    )

    differentialEvolution(deParameters, initialSolution, ::de, maxGenerations, precision, memorize(function), random1)

    val wdeParameters = mapOf(
            LOWER_CONSTRAINTS to lowerConstraints,
            UPPER_CONSTRAINTS to upperConstraints,
            AMPLIFICATION to amplification,
            CROSSOVER_PROBABILITY to crossoverProbability
    )

    differentialEvolution(wdeParameters, initialSolution, ::wde, maxGenerations, precision, memorize(function), random2)

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
    differentialEvolution(sadeParameters, initialSolution, ::sade, maxGenerations, precision, memorize(function), random3)
}


fun differentialEvolution(parameters: Map<String, Any>, initialPopulation: Array<List<Double>>,
                          newGenerationBlock: (Map<String, Any>, Array<List<Double>>, Random, (List<Double>) -> Double) -> Array<List<Double>>,
                          maxGenerationsCount: Int, precision: Double, function: MemorizeFunction<Double, Double>,
                          random: Random, stagnationThreshold: Int = initialPopulation.size) {
    var previousVectors = initialPopulation

    var previousBest = previousVectors.map { function.invoke(it) }.average()
    var stagnationCount = 0
    println("initial average = ${previousBest}")

    loop@ for (generation in 1..maxGenerationsCount) {
        val newVectors = newGenerationBlock.invoke(parameters, previousVectors, random, function)

        val newBest = newVectors.map { function.invoke(it) }.average()
//        println("newBest = ${newBest}")

        if (Math.abs(newBest - previousBest) < precision) {
            stagnationCount++
        } else {
            stagnationCount = 0
        }

        previousBest = newBest
        previousVectors = newVectors

        if (stagnationCount >= stagnationThreshold) {
            println("generation = ${generation}")
            break@loop
        }
    }

    println("average cost = ${previousBest}")
    println("evalutions = ${function.evalutionsCount}")
}

fun de(parameters: Map<String, Any>, previousVectors: Array<List<Double>>,
       random: Random, function: (List<Double>) -> Double): Array<List<Double>> {
    val populationSize = previousVectors.size
    val problemSize = previousVectors[0].size

    val lowerConstraints = parameters[LOWER_CONSTRAINTS] as DoubleArray
    val upperConstraints = parameters[UPPER_CONSTRAINTS] as DoubleArray
    val amplification = parameters[AMPLIFICATION] as Double
    val crossoverProbability = parameters[CROSSOVER_PROBABILITY] as Double

    return Array(populationSize, { index ->
        val vector = previousVectors[index]
        val targetedIndexes = getRandomValues(3, 0, populationSize, setOf(index), random).toIntArray()
        val a = targetedIndexes[2]
        val b = targetedIndexes[0]
        val c = targetedIndexes[1]
        val mandatoryIndex = random.nextInt(problemSize)
        val newVector = DoubleArray(problemSize, { j ->
            val newValue = if (mandatoryIndex == j || random.nextDouble() < crossoverProbability) {
                previousVectors[a][j] + amplification * (previousVectors[b][j] - previousVectors[c][j])
            } else {
                vector[j]
            }
            val min = lowerConstraints[j]
            val max = upperConstraints[j]
            if (newValue < min || max < newValue) random.nextDouble(min, max) else newValue
        }).asList()

        if (function.invoke(newVector) < function.invoke(vector)) newVector else vector
    })
}

fun sade(parameters: Map<String, Any>, previousVectors: Array<List<Double>>,
         random: Random, function: (List<Double>) -> Double): Array<List<Double>> {
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
        val targetedIndexes = getRandomValues(3, 0, populationSize, setOf(index), random).toIntArray()
        val a = targetedIndexes[2]
        val b = targetedIndexes[0]
        val c = targetedIndexes[1]
        val mandatoryIndex = random.nextInt(problemSize)
        val newVector = DoubleArray(problemSize, { j ->
            val newValue = if (mandatoryIndex == j || random.nextDouble() < CR) {
                previousVectors[a][j] + F * (previousVectors[b][j] - previousVectors[c][j])
            } else {
                vector[j]
            }
            val min = lowerConstraints[j]
            val max = upperConstraints[j]
            if (newValue < min || max < newValue) random.nextDouble(min, max) else newValue
        }).asList()

        if (function.invoke(newVector) < function.invoke(vector)) newVector else vector
    })
}

fun wde(parameters: Map<String, Any>, previousVectors: Array<List<Double>>,
        random: Random, function: (List<Double>) -> Double): Array<List<Double>> {
    val populationSize = previousVectors.size
    val problemSize = previousVectors[0].size

    val lowerConstraints = parameters[LOWER_CONSTRAINTS] as DoubleArray
    val upperConstraints = parameters[UPPER_CONSTRAINTS] as DoubleArray
    val defaultAmplification = parameters[AMPLIFICATION] as Double
    val crossoverProbability = parameters[CROSSOVER_PROBABILITY] as Double

    val costs = previousVectors.map { function.invoke(it) }
    val maxF = costs.max()!!
    val minF = costs.min()!!
    val otn = Math.abs(maxF / minF)
    val amplification = Math.max(defaultAmplification, if (otn < 1) 1 - otn else 1 - 1 / otn)

    return Array(populationSize, { index ->
        val vector = previousVectors[index]
        val targetedIndexes = getRandomValues(3, 0, populationSize, setOf(index), random).toIntArray()
        val a = targetedIndexes[2]
        val b = targetedIndexes[0]
        val c = targetedIndexes[1]
        val mandatoryIndex = random.nextInt(problemSize)
        val newVector = DoubleArray(problemSize, { j ->
            val newValue = if (mandatoryIndex == j || random.nextDouble() < crossoverProbability) {
                previousVectors[a][j] + amplification * (previousVectors[b][j] - previousVectors[c][j])
            } else {
                vector[j]
            }
            val min = lowerConstraints[j]
            val max = upperConstraints[j]
            if (newValue < min || max < newValue) random.nextDouble(min, max) else newValue
        }).asList()

        if (function.invoke(newVector) < function.invoke(vector)) newVector else vector
    })
}

