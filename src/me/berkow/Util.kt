package me.berkow

import java.util.*

fun getRandomValues(valuesCount: Int, min: Int = 0, max: Int, restricted: Set<Int>,
                    random: Random): Set<Int> {
    val result = java.util.HashSet<Int>(valuesCount)

    while (result.size < valuesCount) {
        val randomValue = random.nextInt(min, max)
        if (randomValue !in restricted && randomValue !in result) result.add(randomValue)
    }

    return result
}

fun Collection<Double>.product(): Double = fold(1.0, { accumulated, element -> accumulated * element })

fun Random.nextInt(min: Int, max: Int): Int = nextInt(max - min) + min

fun Random.nextDouble(min: Double, max: Double): Double = nextDouble() * (max - min) + min

fun createRandomVector(lowerConstraints: DoubleArray, upperConstraints: DoubleArray, random: Random): List<Double> {
    val size = lowerConstraints.size
    return DoubleArray(size, { index ->
        val min = lowerConstraints[index]
        val max = upperConstraints[index]
        random.nextDouble(min, max)
    }).asList()
}

fun solveJRC(problemSize: Int): Triple<Int, Int, Int> {
    var j = 0
    var R  = 1
    while (R - 1 < problemSize) {
        j++
        R *= 2
    }
    return Triple(j, R, R -1)
}