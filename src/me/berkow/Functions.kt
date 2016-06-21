package me.berkow

val _2PI = 2 * Math.PI

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
    args.dropLast(1).mapIndexed { i, x ->
        val next = args[i + 1]
        val tmp1 = (next - x * x)
        val tmp2 = x - 1
        100 * tmp1 * tmp1 + tmp2 * tmp2
    }.sum()
}

val F6_FUNCTION = { args: List<Double> ->
    args.mapIndexed { i, x -> i * x * x * x * x }.sum()
}

val F7_FUNCTION = { args: List<Double> ->
    args.map { x -> x * Math.sin(Math.sqrt(Math.abs(x))) }.sum()
}

val F8_FUNCTION = { args: List<Double> ->
    args.map { x -> x * x - 10 * Math.cos(_2PI * x) + 10 }.sum()
}

val F9_FUNCTION = { args: List<Double> ->
    val f1Result = F1_FUNCTION(args)
    val tmp1 = 20 * Math.exp(-0.2 * Math.sqrt(f1Result))
    val tmp2 = Math.exp(args.map { Math.cos(_2PI * it) }.sum() / args.size)

    -tmp1 - tmp2 + 20 + Math.E
}

val F10_FUNCTION = { args: List<Double> ->
    val f1Result = F1_FUNCTION(args)
    f1Result / 4000 - args.mapIndexed { i, x -> Math.cos(x / Math.sqrt(i + 1.0)) }.product() + 1
}

val F11_FUNCTION = { args: List<Double> ->
    getF11(args, 0.5, 3.0, 25)
}

fun getF11(args: List<Double>, a: Double, b: Double, kMax: Int): Double {
    val tmp1 = args.map { x -> (0..kMax).map { k -> Math.pow(a, k.toDouble()) * Math.cos(_2PI * (x + 0.5)) }.sum() }.sum()
    val tmp2 = (0..kMax).map { k -> Math.pow(a, k.toDouble()) * Math.cos(Math.PI * Math.pow(b, k.toDouble())) }.sum()

    return tmp1 - args.size * tmp2
}