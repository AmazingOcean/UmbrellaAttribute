package ac.github.umbrella.internal.attribute.base

interface InternalDefaultConfig {


    fun configDiscerns() : List<String>

    fun combatPowerAlgorithm() : Double? = null

    fun maximum() : Float? = null

    fun minimum() : Float? = null
}