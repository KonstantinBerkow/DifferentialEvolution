package me.berkow

/**
 * Created by konstantinberkov on 6/21/16.
 */
class FunctionProblem(val function: (List<Double>) -> Double, val lBound: Double, val uBound: Double,
                      val solution: List<Double>) {

    val problemSize = solution.size
    val lowerBounds = DoubleArray(problemSize, { lBound })
    val upperBounds = DoubleArray(problemSize, { uBound })

    object HOLDER {
        val PROBLEM_SIZE = 30

        @JvmStatic
        val F1_PROBLEM = FunctionProblem(F1_FUNCTION, -100.0, 100.0, DoubleArray(PROBLEM_SIZE, { .0 }).asList())

        @JvmStatic
        val F2_PROBLEM = FunctionProblem(F2_FUNCTION, -10.0, 10.0, DoubleArray(PROBLEM_SIZE, { .0 }).asList())

        @JvmStatic
        val F3_PROBLEM = FunctionProblem(F3_FUNCTION, -100.0, 100.0, DoubleArray(PROBLEM_SIZE, { .0 }).asList())

        @JvmStatic
        val F4_PROBLEM = FunctionProblem(F4_FUNCTION, -100.0, 100.0, DoubleArray(PROBLEM_SIZE, { .0 }).asList())

        @JvmStatic
        val F5_PROBLEM = FunctionProblem(F5_FUNCTION, -30.0, 30.0, DoubleArray(PROBLEM_SIZE, { 1.0 }).asList())

        @JvmStatic
        val F6_PROBLEM = FunctionProblem(F6_FUNCTION, -1.28, 1.28, DoubleArray(PROBLEM_SIZE, { .0 }).asList())

        @JvmStatic
        val F7_PROBLEM = FunctionProblem(F7_FUNCTION, -500.0, 500.0, DoubleArray(PROBLEM_SIZE, { 420.96 }).asList())

        @JvmStatic
        val F8_PROBLEM = FunctionProblem(F8_FUNCTION, -5.12, 5.12, DoubleArray(PROBLEM_SIZE, { .0 }).asList())

        @JvmStatic
        val F9_PROBLEM = FunctionProblem(F9_FUNCTION, -32.0, 32.0, DoubleArray(PROBLEM_SIZE, { .0 }).asList())

        @JvmStatic
        val F10_PROBLEM = FunctionProblem(F10_FUNCTION, -600.0, 600.0, DoubleArray(PROBLEM_SIZE, { .0 }).asList())

        @JvmStatic
        val F11_PROBLEM = FunctionProblem(F11_FUNCTION, -50.0, 50.0, DoubleArray(PROBLEM_SIZE, { 1.0 }).asList())

        @JvmStatic
        val F12_PROBLEM = FunctionProblem(F12_FUNCTION, -0.5, 0.5, DoubleArray(PROBLEM_SIZE, { .0 }).asList())

    }
}
