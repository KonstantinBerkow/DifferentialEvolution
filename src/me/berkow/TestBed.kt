package me.berkow

import java.util.*

/**
 * Created by konstantinberkov on 6/21/16.
 */
fun makeTest(problem: FunctionProblem, precision: Double, maxGenerations: Int, populationSize: Int,
             seed: Long, deParameters: Map<String, Any>, sadeParameters: Map<String, Any>,
             tdeParameters: Map<String, Any>) {
    val function = problem.function
    val probableSolution = problem.solution

    val lowerBounds = problem.lowerBounds
    val upperBounds = problem.upperBounds

    val deParameters1 = deParameters.plus(LOWER_CONSTRAINTS to lowerBounds).plus(UPPER_CONSTRAINTS to upperBounds)
    val sadeParameters1 = sadeParameters.plus(AMPLIFICATIONS to LinkedList<Double>(listOf(sadeParameters[AMPLIFICATION] as Double)))
            .plus(CROSSOVER_PROBABILITIES to LinkedList<Double>(listOf(sadeParameters[CROSSOVER_PROBABILITY] as Double)))
    val tdeParameters1 = tdeParameters.plus(LOWER_CONSTRAINTS to lowerBounds).plus(UPPER_CONSTRAINTS to upperBounds)

    val random1 = Random(seed)
    val random2 = Random(seed)
    val random3 = Random(seed)

    val initialSolution = Array(populationSize, { createRandomVector(lowerBounds, upperBounds, Random(seed)) })

    val evaluationsForDE = differentialEvolution(deParameters1, initialSolution, ::de, maxGenerations, precision,
            memoize(function), random1, probablySolution = probableSolution)

    val evaluationsForSDE = differentialEvolution(sadeParameters1, initialSolution, ::sade, maxGenerations, precision,
            memoize(function), random3, probablySolution = probableSolution)

    val evaluationsForTDE = differentialEvolution(tdeParameters1, initialSolution, ::tde, maxGenerations, precision,
            memoize(function), random2, probablySolution = probableSolution)

    println("evaluationsForDE = $evaluationsForDE, evaluationsForSDE = $evaluationsForSDE, evaluationsForTDE = $evaluationsForTDE")
}

fun main(args: Array<String>) {
    makeTest()
}