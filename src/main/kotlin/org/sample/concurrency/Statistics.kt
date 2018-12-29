package org.sample.concurrency

import arrow.core.Option

class Statistics private constructor(val count: Pair<String, Int>, val pairs: List<Pair<String, String>>) {

    companion object {
        fun of(count: Int, vararg pairs: Pair<String, String>): Statistics {
            return of(count, pairs.asList())
        }

        fun of(count: Int, pairs: List<Pair<String, String>>): Statistics {
            return Statistics("count" to count, pairs)
        }
    }

    fun count(): Int {
        return count.second
    }

    fun get(key: String): Option<String> {
        val possiblePair = pairs.find { it -> it.first == key }
        return Option.fromNullable(possiblePair)
                .map { it.second }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Statistics

        if (count != other.count) return false
        if (pairs != other.pairs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = count.hashCode()
        result = 31 * result + pairs.hashCode()
        return result
    }

    override fun toString(): String {
        return "Statistics(count=$count, pairs=$pairs)"
    }


}
