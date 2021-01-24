package ac.github.umbrella.internal.attribute.base

class ValueArray(
    string: String?,
) {

    val values = mutableMapOf<ValueEnum, AttributeArrayList<Float>>()
    lateinit var type: ValueEnum

    init {
        if (string != null) {
            type = if (string.endsWith("%")) ValueEnum.Percent else ValueEnum.Number
            when (type) {
                ValueEnum.Percent -> {
                    values[ValueEnum.Percent] = attributeArrayListOf(getValue(string))
                }
                ValueEnum.Number, ValueEnum.All -> {
                    val loreSplit = string.split("-")
                    values[ValueEnum.Number] =
                        attributeArrayListOf(getValue(loreSplit[0]), getValue(loreSplit[if (loreSplit.size > 1) 1 else 0]))
                }
            }
        } else {
            values[ValueEnum.Number] = attributeArrayListOf()
            values[ValueEnum.Percent] = attributeArrayListOf()
        }
    }

    fun take(valueArray: ValueArray): ValueArray {
        return set(valueArray, 1)
    }

    fun set(valueArray: ValueArray, operationStatus: Int): ValueArray {

        synchronized(valueArray.values) {
            valueArray.values.forEach { entry ->
                val floats = if (values.containsKey(entry.key)) {
                    values[entry.key]
                } else {
                    val arrayOf = attributeArrayListOf<Float>()
                    values[entry.key] = arrayOf
                    arrayOf
                }
                entry.value.forEachIndexed { index, value ->
                    val result = floats!!.getOrDefault(index,0F) + (if (operationStatus == 0) value else -value)
                    floats[index] = result
                }
            }
        }

        return this
    }

    fun add(valueArray: ValueArray): ValueArray {
        return set(valueArray, 0)
    }

    companion object {

        fun getValue(string: String): Float {
            val str = string.replace("\u00a7+[a-z0-9]".toRegex(), "").replace("[^-0-9.]".toRegex(), "")
            return if (str.isEmpty() || str.replace("[^.]".toRegex(), "").length > 1) 0F else str.toFloat()
        }


        fun empty(): ValueArray {
            return ValueArray(null)
        }
    }

    class AttributeArrayList<E> : ArrayList<E>() {

        fun getOrDefault(index: Int,default: E): E {
            // 3 3
            if (this.size <= index) {
                val length = index - (this.size - 1)
                for (amount in 0 until length) {
                    this.add(default)
                }
            }
            return this[index]
        }
    }
}