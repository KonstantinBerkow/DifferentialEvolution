package me.berkow

import java.io.File
import java.util.*

class DataHolder(val maxGenerationsCount: Int, val vectorsPerGeneration: Int, val function: (DoubleArray) -> Double) {

    val generations = HashMap<Int, Array<DoubleArray>>(maxGenerationsCount)
    val costTable = HashMap<DoubleArray, Double>(maxGenerationsCount * vectorsPerGeneration)

    fun put(generation: Int, vectors: Array<DoubleArray>) {
        generations[generation] = vectors
    }

    fun evalute(vector: DoubleArray): Double = costTable.getOrPut(vector, { function.invoke(vector) })

    fun dump(path: String) {
        val infos = generations.entries
                .flatMap { entry ->
                    entry.value.map { vector -> Triple(entry.key, vector, evalute(vector)) }
                }

        File(path).printWriter().use { writer ->
            writer.printf("Generation,Vector,Cost\n")

            infos.forEach { info ->
                writer.printf(Locale.ENGLISH, "%d,%s,%f\n", info.first, Arrays.toString(info.second), info.third)
            }

            writer.flush()
            writer.close()
        }
    }

    fun get(generation: Int): Array<DoubleArray> = generations[generation]!!
}