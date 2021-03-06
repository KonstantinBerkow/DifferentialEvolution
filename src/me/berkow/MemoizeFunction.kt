package me.berkow

import java.util.*

/**
 * Created by konstantinberkov on 6/20/16.
 */
class MemoizeFunction<T, V>(val function: (T) -> V) : (T) -> V {

    val evaluatedResults = HashMap<T, V>()
    var evaluationsCount = 0
        private set

    override fun invoke(element: T): V = evaluatedResults.getOrPut(element) {
        evaluationsCount++
        function.invoke(element)
    }
}

fun <T, V> memoize(function: (T) -> V): MemoizeFunction<T, V> = MemoizeFunction(function)
