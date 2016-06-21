package me.berkow

val F1_FUNCTION = { args: List<Double> ->
    args.map { arg -> arg * arg }.sum()
}

val F2_FUNCTION = { args: List<Double> ->
    val absoluteArgs = args.map { arg -> Math.abs(arg) }
    absoluteArgs.sum() + absoluteArgs.map { Math.abs(it) }.product()
}

val F3_FUNCTION = { args: List<Double> ->
    args.mapIndexed { index, arg -> args.slice(0..index) }
            .map { slice -> slice.sum() }
            .map { value -> value * value }
            .sum()
}

val F4_FUNCTION = { args: List<Double> ->
    args.map { Math.abs(it) }.max()!!
}

val F5_FUNCTION = { args: List<Double> ->
    args.dropLast(1).mapIndexed { i, x -> 100 * (args[i + 1] - x * x) * (args[i + 1] - x * x) + (x - 1) * (x - 1) }.sum()
}

val F6_FUNCTION = { args: List<Double> ->
    args.mapIndexed { i, x -> i * x * x * x * x }.sum()
}

val F7_FUNCTION = { args: List<Double> ->
    args.map { x -> x * Math.sin(Math.sqrt(Math.abs(x))) }.sum()
}

val F8_FUNCTION = { args: List<Double> ->
    args.map { x -> x * x - 10 * Math.cos(2 * Math.PI * x) + 10 }.sum()
}

val F9_FUNCTION = { args: List<Double> ->
    Math.E + 20 - 20 * Math.exp(-0.2 * Math.sqrt(F1_FUNCTION.invoke(args))) - Math.exp(args.map { Math.cos(2 * Math.PI * it) }.sum() / args.size)
}

val F10_FUNCTION = { args: List<Double> ->
    F1_FUNCTION.invoke(args) / 4000 + 1 - args.mapIndexed { i, x -> Math.cos(x / Math.sqrt(i + 1.0)) }.product()
}

fun main(args: Array<String>) {
    val result = F3_FUNCTION(listOf(1, 2).map { it.toDouble() })
    println("result = ${result}")
}