package me.berkow

import java.util.*

/**
 * Created by konstantinberkov on 6/20/16.
 */
class MemorizeFunction<T, V>(val function: (List<T>) -> V) : (List<T>) -> V {

    val evalutedResults = HashMap<List<T>, V>()
    var evalutionsCount = 0
        private set

    override fun invoke(vector: List<T>): V = evalutedResults.getOrPut(vector) {
        evalutionsCount++
        function.invoke(vector)
    }
}

fun <T, V> memorize(function: (List<T>) -> V): MemorizeFunction<T, V> = MemorizeFunction(function)
