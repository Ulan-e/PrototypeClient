package kg.optimabank.prototype.common.extensions

import android.content.Context

fun Int.formatCount(context: Context, res1: Int, res2: Int, res3: Int): String {
    return this.toLong().formatCount(context, res1, res2, res3)
}

private fun Long.formatCount(context: Context, res1: Int, res2to5: Int, res5: Int): String {
    val res: Int
    val nMod = this % 10

    res = if (this in 5L..20L) {
        res5
    } else if (nMod == 1L) {
        res1
    } else if (nMod == 2L || nMod == 3L || nMod == 4L) {
        res2to5
    } else {
        res5
    }
    return context.getString(res, this)
}