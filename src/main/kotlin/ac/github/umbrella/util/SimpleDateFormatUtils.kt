package ac.github.umbrella.util

import ac.github.umbrella.UmbrellaAttribute
import ac.github.umbrella.internal.attribute.base.ifReplaceCharIndex
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SimpleDateFormatUtils(
    var simpleFormat: String
) {

    companion object {
        lateinit var instance: SimpleDateFormatUtils

        fun init() {
            instance = SimpleDateFormatUtils(UmbrellaAttribute.instance.config.getString("SimpleFormat"))
        }
    }

    private val local: ThreadLocal<SimpleDateFormat> = ThreadLocal.withInitial { SimpleDateFormat(simpleFormat) }

    fun parseFormDate(dateStr: String): Date {
        return local.get().parse(dateStr)
    }

    fun parseFormLong(dateStr: String): Long {
        return local.get().parse(dateStr).time
    }

    fun format(date: Long): String {
        return local.get().format(date)
    }

    fun format(date: Date): String {
        return local.get().format(date)
    }

    fun reload() {
        local.remove()
    }

}