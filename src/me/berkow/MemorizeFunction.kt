package me.berkow

import java.util.*

/**
 * Created by konstantinberkov on 6/20/16.
 */
class MemorizeFunction<T, V>(val function: (T) -> V) : (T) -> V {

    val evalutedResults = HashMap<T, V>()
    var evalutionsCount = 0
        private set

    override fun invoke(element: T): V = evalutedResults.getOrPut(element) {
        evalutionsCount++
        function.invoke(element)
    }
}

fun <T, V> memorize(function: (T) -> V): MemorizeFunction<T, V> = MemorizeFunction(function)
