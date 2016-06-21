package me.berkow

import java.util.*

/**
 * Created by konstantinberkov on 6/21/16.
 */
val DEFAULT_PRECISION = 1e-12
val DEFAULT_MAX_GEN = 60000

fun makeTest(problem: FunctionProblem, precision: Double = DEFAULT_PRECISION, maxGenerations: Int = DEFAULT_MAX_GEN,
             seed: Long, deParameters: Map<String, Any>, sadeParameters: Map<String, Any>,
             tdeParameters: Map<String, Any>) {
    val function = problem.function
    val probableSolution = problem.solution

    val lowerBounds = problem.lowerBounds
    val upperBounds = problem.upperBounds

    val deParameters1 = deParameters.plus(LOWER_CONSTRAINTS to lowerBounds).plus(UPPER_CONSTRAINTS to upperBounds)

    val sadeParameters1 = sadeParameters
            .plus(AMPLIFICATIONS to LinkedList<Double>(listOf(sadeParameters[UPPER_AMPLIFICATION] as Double)))
            .plus(CROSSOVER_PROBABILITIES to LinkedList<Double>(listOf(0.5)))
            .plus(LOWER_CONSTRAINTS to lowerBounds)
            .plus(UPPER_CONSTRAINTS to upperBounds)

    val tdeParameters1 = tdeParameters.plus(LOWER_CONSTRAINTS to lowerBounds).plus(UPPER_CONSTRAINTS to upperBounds)

    val random1 = Random(seed)
    val random2 = Random(seed)
    val random3 = Random(seed)

    val populationSize = 80
    val initialSolution = Array(populationSize, { createRandomVector(lowerBounds, upperBounds, Random()) })

    val evaluationsForDE = differentialEvolution(deParameters1, initialSolution, ::de, maxGenerations, precision,
            memoize(function), random1, probablySolution = probableSolution)

    val evaluationsForSDE = differentialEvolution(sadeParameters1, initialSolution, ::sade, maxGenerations, precision,
            memoize(function), random3, probablySolution = probableSolution)

    val evaluationsForTDE = differentialEvolution(tdeParameters1, initialSolution, ::tde, maxGenerations, precision,
            memoize(function), random2, probablySolution = probableSolution)

    println("evaluationsForDE = $evaluationsForDE, evaluationsForSDE = $evaluationsForSDE, evaluationsForTDE = $evaluationsForTDE")
}

fun main(args: Array<String>) {
    val seed = System.nanoTime()

//    val problemSize = 30
    val maxGenerations = 60000
//
//    val populationSize = problemSize * 6

    val amplification = 0.9
    val crossoverProbability = 0.5
    val deParameters = mapOf(
            AMPLIFICATION to amplification,
            CROSSOVER_PROBABILITY to crossoverProbability
    )
    val lowerF = 0.1
    val upperF = 0.9
    val t1 = 0.1
    val t2 = 0.1
    val sadeParameters = mapOf(
            T1 to t1,
            T2 to t2,
            LOWER_AMPLIFICATION to lowerF,
            UPPER_AMPLIFICATION to upperF
    )

    val tdeParameters = mapOf(
            AMPLIFICATION to amplification,
            CROSSOVER_PROBABILITY to crossoverProbability
    )

    println("Problem 1")
    makeTest(FunctionProblem.HOLDER.F1_PROBLEM, maxGenerations = maxGenerations, seed = seed,
            deParameters = deParameters, sadeParameters = sadeParameters, tdeParameters = tdeParameters)

    println("Problem 2")
    makeTest(FunctionProblem.HOLDER.F2_PROBLEM, maxGenerations = maxGenerations, seed = seed,
            deParameters = deParameters, sadeParameters = sadeParameters, tdeParameters = tdeParameters)

    println("Problem 3")
    makeTest(FunctionProblem.HOLDER.F3_PROBLEM, maxGenerations = maxGenerations, seed = seed,
            deParameters = deParameters, sadeParameters = sadeParameters, tdeParameters = tdeParameters)

    println("Problem 4")
    makeTest(FunctionProblem.HOLDER.F4_PROBLEM, maxGenerations = maxGenerations, seed = seed,
            deParameters = deParameters, sadeParameters = sadeParameters, tdeParameters = tdeParameters)

    println("Problem 5")
    makeTest(FunctionProblem.HOLDER.F5_PROBLEM, maxGenerations = maxGenerations, seed = seed,
            deParameters = deParameters, sadeParameters = sadeParameters, tdeParameters = tdeParameters)

    println("Problem 6")
    makeTest(FunctionProblem.HOLDER.F6_PROBLEM, maxGenerations = maxGenerations, seed = seed,
            deParameters = deParameters, sadeParameters = sadeParameters, tdeParameters = tdeParameters)

    println("Problem 7")
    makeTest(FunctionProblem.HOLDER.F7_PROBLEM, maxGenerations = maxGenerations, seed = seed,
            deParameters = deParameters, sadeParameters = sadeParameters, tdeParameters = tdeParameters)

    println("Problem 8")
    makeTest(FunctionProblem.HOLDER.F8_PROBLEM, maxGenerations = maxGenerations, seed = seed,
            deParameters = deParameters, sadeParameters = sadeParameters, tdeParameters = tdeParameters)

    println("Problem 9")
    makeTest(FunctionProblem.HOLDER.F9_PROBLEM, maxGenerations = maxGenerations, seed = seed,
            deParameters = deParameters, sadeParameters = sadeParameters, tdeParameters = tdeParameters)

    println("Problem 10")
    makeTest(FunctionProblem.HOLDER.F10_PROBLEM, maxGenerations = maxGenerations, seed = seed,
            deParameters = deParameters, sadeParameters = sadeParameters, tdeParameters = tdeParameters)

    println("Problem 11")
    makeTest(FunctionProblem.HOLDER.F11_PROBLEM, maxGenerations = maxGenerations, seed = seed,
            deParameters = deParameters, sadeParameters = sadeParameters, tdeParameters = tdeParameters)

    println("Problem 12")
    makeTest(FunctionProblem.HOLDER.F12_PROBLEM, maxGenerations = maxGenerations, seed = seed,
            deParameters = deParameters, sadeParameters = sadeParameters, tdeParameters = tdeParameters)
}