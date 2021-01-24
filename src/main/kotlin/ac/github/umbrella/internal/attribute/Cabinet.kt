package ac.github.umbrella.internal.attribute

import ac.github.umbrella.internal.attribute.base.*

class Cabinet {

    var combatPower = 0.0

    val origin =
        mutableMapOf<Class<out AbstractAttributeAdapter>, MutableMap<Class<out AbstractAttributeInternal>, ValueArray>>()

    val spreads = mutableMapOf<Class<out AbstractLattice>, SpreadData>()

    val station =
        mutableMapOf<Class<out AbstractAttributeAdapter>, MutableMap<Class<out AbstractAttributeInternal>, ValueArray.AttributeArrayList<Float>>>()

    fun autoCalculation() {
        this.station.clear()

        this.origin.forEach { adapterEntry ->
            val internalMapOf =
                mutableMapOf<Class<out AbstractAttributeInternal>, ValueArray.AttributeArrayList<Float>>()
            adapterEntry.value.forEach { internalEntry ->
                val attributeArrayListOf = attributeArrayListOf<Float>()
                // TODO 百分比数值注入

                val attributeMetaAnnotation = AttributeFactory.getAttributeMetaAnnotation(adapterEntry.key)

                when (attributeMetaAnnotation.valueType) {
                    ValueEnum.Number -> {
                        internalEntry.value.values[ValueEnum.Number]!!.forEach {
                            var result = it
                            if (internalEntry.value.values.containsKey(ValueEnum.Percent)) {
                                val sum = internalEntry.value.values[ValueEnum.Percent]!!.sum()
                                result += (result * (sum / 100))
                            }
                            attributeArrayListOf.add(result)
                        }
                    }
                    ValueEnum.Percent -> {
                        val arrayList = internalEntry.value.values[ValueEnum.Percent]!!
                        attributeArrayListOf.add(arrayList[0])
                    }
                    else -> {}
                }

                internalMapOf[internalEntry.key] = attributeArrayListOf
            }
            this.station[adapterEntry.key] = internalMapOf
        }
    }

    fun merger(cabinet: Cabinet) {
        cabinet.origin.forEach { (adapter, _) ->
            val baseValues = cabinet.baseValues(adapter)
            baseValues.forEach { (internal, value) ->
                this.addInternalValue(adapter, internal, value)
            }
        }
        autoCalculation()
    }

    fun merger(clazz: Class<out AbstractLattice>, spreadData: SpreadData) {
        dismantle(clazz)
        spreads[clazz] = spreadData
        merger(spreadData.cabinet)
    }

    fun againCalculation() {
        combatPower = 0.0
    }

    fun dismantle(cabinet: Cabinet) {
        cabinet.origin.forEach { (adapter, _) ->
            val baseValues = cabinet.baseValues(adapter)
            baseValues.forEach { (internal, value) ->

                this.takeInternalValue(adapter, internal, value)
            }
        }
        autoCalculation()
    }

    fun dismantle(clazz: Class<out AbstractLattice>) {
        if (spreads.containsKey(clazz)) {
            val spreadData = spreads[clazz]!!
            dismantle(spreadData.cabinet)
        }
    }

    fun clearAll() {
        origin.forEach { (key, _) ->
            clearAttributeAdapter(key)
        }
    }

    fun clearAttributeAdapter(attributeAdapterClass: Class<out AbstractAttributeAdapter>) {
        val adapter = AttributeFactory.attributes.firstOrNull { it::class.java == attributeAdapterClass }
        if (adapter != null) {
            val baseValues = baseValues(attributeAdapterClass)
            baseValues.forEach { entry ->
                adapter.internal.firstOrNull { it::class.java == entry.key }?.correct(entry.value)
            }
        }
    }

    fun addInternalValue(
        attributeAdapterClass: Class<out AbstractAttributeAdapter>,
        internalClass: Class<out AbstractAttributeInternal>,
        valueArray: ValueArray
    ) {
        internalValues(attributeAdapterClass, internalClass).add(valueArray)
    }

    fun takeInternalValue(
        attributeAdapterClass: Class<out AbstractAttributeAdapter>,
        internalClass: Class<out AbstractAttributeInternal>,
        valueArray: ValueArray
    ) {
        internalValues(attributeAdapterClass, internalClass).take(valueArray)
    }

    fun setInternalValue(
        attributeAdapterClass: Class<out AbstractAttributeAdapter>,
        internalClass: Class<out AbstractAttributeInternal>,
        valueArray: ValueArray
    ) {
        this.baseValues(attributeAdapterClass)[internalClass] = valueArray
    }

    fun internalValues(
        attributeAdapterClass: Class<out AbstractAttributeAdapter>,
        internalClass: Class<out AbstractAttributeInternal>
    ): ValueArray {
        return baseValues(attributeAdapterClass)[internalClass]!!
    }

    fun injectStatic() {
        AttributeFactory.attributes.forEach {
            val mapOf = mutableMapOf<Class<out AbstractAttributeInternal>, ValueArray>()
            it.internal.forEach { abstractAttributeInternal ->
                mapOf[abstractAttributeInternal::class.java] = ValueArray.empty()
            }
            origin[it::class.java] = mapOf
        }
    }

    init {
        injectStatic()
    }

    fun baseValues(clazz: Class<out AbstractAttributeAdapter>): MutableMap<Class<out AbstractAttributeInternal>, ValueArray> {
        return origin[clazz]!!
    }

    class SpreadData(val cabinet: Cabinet)

}