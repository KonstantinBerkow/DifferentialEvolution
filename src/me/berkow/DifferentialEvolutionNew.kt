package me.berkow

import java.util.*

fun main(args: Array<String>) {
    val random1 = Random(233313L)
    val random2 = Random(233313L)
    val random3 = Random(233313L)

    val problemSize = 10
    val lowerConstraints = DoubleArray(problemSize, { -100.0 })
    val upperConstraints = DoubleArray(problemSize, { 100.0 })
    val maxGenerationsCount = 1500

    val populationSize = problemSize * 10
    val crossoverFactor = 0.5
    val amplificationFactor = 0.9
    val function = F3_FUNCTION

    solveDE(maxGenerationsCount, populationSize, random1, problemSize, crossoverFactor,
            amplificationFactor, lowerConstraints, upperConstraints, "DE.csv", function)

    val lowerAmplificationFactor = 0.4
    solveWDE(maxGenerationsCount, populationSize, random2, problemSize, crossoverFactor,
            lowerAmplificationFactor, lowerConstraints, upperConstraints, "WDE.csv", function)

    val lowerF = 0.1
    val upperF = 0.9
    solveSADE(maxGenerationsCount, populationSize, random3, problemSize, lowerF, upperF, 0.1, 0.1, lowerConstraints,
            upperConstraints, "SADE.csv", function)
}

fun solveDE(maxGenerationsCount: Int, populationSize: Int, random: Random, problemSize: Int,
            crossoverFactor: Double, amplificationFactor: Double, lowerConstraints: DoubleArray,
            upperConstraints: DoubleArray, file: String, function: (DoubleArray) -> Double) {
    val initialVectors = Array(populationSize, { createInitialSolution(lowerConstraints, upperConstraints, random) });

    val dataHolder = DataHolder(maxGenerationsCount, populationSize, function)
    dataHolder.put(0, initialVectors)

    differentialEvolution1(maxGenerationsCount, populationSize, random, problemSize, crossoverFactor,
            amplificationFactor, lowerConstraints, upperConstraints, dataHolder)

//    dataHolder.dump(file)
}

fun solveWDE(maxGenerationsCount: Int, populationSize: Int, random: Random, problemSize: Int,
             crossoverFactor: Double, amplificationFactor: Double, lowerConstraints: DoubleArray,
             upperConstraints: DoubleArray, file: String, function: (DoubleArray) -> Double) {
    val initialVectors = Array(populationSize, { createInitialSolution(lowerConstraints, upperConstraints, random) });

    val dataHolder = DataHolder(maxGenerationsCount, populationSize, function)
    dataHolder.put(0, initialVectors)

    weightedDE(maxGenerationsCount, populationSize, random, problemSize, crossoverFactor,
            amplificationFactor, lowerConstraints, upperConstraints, dataHolder)
}

fun solveSADE(maxGenerationsCount: Int, populationSize: Int, random: Random,
              problemSize: Int, lowerF: Double, upperF: Double, t1: Double, t2: Double,
              lowerConstraints: DoubleArray, upperConstraints: DoubleArray, file: String, function: (DoubleArray) -> Double) {
    val initialVectors = Array(populationSize, { createInitialSolution(lowerConstraints, upperConstraints, random) });

    val dataHolder = DataHolder(maxGenerationsCount, populationSize, function)
    dataHolder.put(0, initialVectors)

    selfAdaptableDE(maxGenerationsCount, populationSize, random, problemSize, lowerF, upperF, t1, t2, lowerConstraints, upperConstraints, dataHolder)

//    dataHolder.dump(file)
}

fun differentialEvolution1(maxGenerationsCount: Int, populationSize: Int, random: Random,
                           problemSize: Int, crossoverFactor: Double, amplificationFactor: Double,
                           lowerConstraints: DoubleArray, upperConstraints: DoubleArray, dataHolder: DataHolder) {
    (1..maxGenerationsCount).forEach { generation ->
        val previousVectors = dataHolder.get(generation - 1)
        val newVectors = Array(populationSize, { index ->
            val vector = previousVectors[index]
            val targetedIndexes = getRandomValues(3, 0, populationSize, setOf(index), random).toIntArray()
            val a = targetedIndexes[2]
            val b = targetedIndexes[0]
            val c = targetedIndexes[1]
            val mandatoryIndex = random.nextInt(problemSize)
            val newVector = DoubleArray(problemSize, { j ->
                val newValue = if (mandatoryIndex == j || random.nextDouble() < crossoverFactor) {
                    previousVectors[a][j] + amplificationFactor * (previousVectors[b][j] - previousVectors[c][j])
                } else {
                    vector[j]
                }
                val min = lowerConstraints[j]
                val max = upperConstraints[j]
                if (newValue < min || max < newValue) random.nextDouble(min, max) else newValue
            })
            (if (dataHolder.evalute(newVector) <= dataHolder.evalute(vector)) newVector else vector).copyOf()
        })
        dataHolder.put(generation, newVectors)
    }
    val averageDE = dataHolder.generations.get(maxGenerationsCount)!!.map { dataHolder.evalute(it) }.average()
    println("averageDE = ${averageDE}")
}

fun weightedDE(maxGenerationsCount: Int, populationSize: Int, random: Random,
               problemSize: Int, crossoverFactor: Double, amplificationFactor: Double,
               lowerConstraints: DoubleArray, upperConstraints: DoubleArray, dataHolder: DataHolder) {
    (1..maxGenerationsCount).forEach { generation ->
        val previousVectors = dataHolder.get(generation - 1)
        val costs = previousVectors.map { dataHolder.evalute(it) }
        val maxF = costs.max()!!
        val minF = costs.min()!!
        val otn = Math.abs(maxF / minF)
        val F = Math.max(amplificationFactor, if (otn < 1) 1 - otn else 1 - 1 / otn)

        val newVectors = Array(populationSize, { index ->
            val vector = previousVectors[index]
            val targetedIndexes = getRandomValues(3, 0, populationSize, setOf(index), random).toIntArray()
            val a = targetedIndexes[2]
            val b = targetedIndexes[0]
            val c = targetedIndexes[1]
            val mandatoryIndex = random.nextInt(problemSize)
            val newVector = DoubleArray(problemSize, { j ->
                val newValue = if (mandatoryIndex == j || random.nextDouble() < crossoverFactor) {
                    previousVectors[a][j] + F * (previousVectors[b][j] - previousVectors[c][j])
                } else {
                    vector[j]
                }
                val min = lowerConstraints[j]
                val max = upperConstraints[j]
                if (newValue < min || max < newValue) random.nextDouble(min, max) else newValue
            })
            (if (dataHolder.evalute(newVector) <= dataHolder.evalute(vector)) newVector else vector).copyOf()
        })
        dataHolder.put(generation, newVectors)
    }
    val averageWDE = dataHolder.generations.get(maxGenerationsCount)!!.map { dataHolder.evalute(it) }.average()
    println("averageWDE = ${averageWDE}")
}

fun selfAdaptableDE(maxGenerationsCount: Int, populationSize: Int, random: Random,
                    problemSize: Int, lowerF: Double, upperF: Double, t1: Double, t2: Double,
                    lowerConstraints: DoubleArray, upperConstraints: DoubleArray, dataHolder: DataHolder) {
    val amplifications = LinkedList<Double>()
    val crossoverProbabilities = LinkedList<Double>()
    amplifications.add(lowerF)
    crossoverProbabilities.add(random.nextDouble())
    (1..maxGenerationsCount).forEach { generation ->
        val previousVectors = dataHolder.get(generation - 1)
        val F = if (random.nextDouble() < t1) lowerF + random.nextDouble() * upperF else amplifications.last()
        val CR = if (random.nextDouble() < t2) random.nextDouble() else crossoverProbabilities.last()
        amplifications.add(F)
        crossoverProbabilities.add(CR)

        val newVectors = Array(populationSize, { index ->
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
            })
            (if (dataHolder.evalute(newVector) <= dataHolder.evalute(vector)) newVector else vector).copyOf()
        })
        dataHolder.put(generation, newVectors)
    }
    val averageSADE = dataHolder.generations.get(maxGenerationsCount)!!.map { dataHolder.evalute(it) }.average()
    println("averageSADE = ${averageSADE}")
}

