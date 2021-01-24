package ac.github.umbrella.internal.attribute.base

import ac.github.umbrella.UmbrellaAttribute
import cc.kunss.bountyhuntercore.common.container.Param
import org.bukkit.configuration.ConfigurationSection

abstract class AbstractAttributeInternal(
    val attribute: AbstractAttributeAdapter,
    val name: String,
) : InternalDefaultConfig {

    lateinit var discerns: List<String>
    lateinit var combatPowerAlgorithm: String
    var maximum = 0.0
    var minimum = 0.0
    var algorithm = 1.0

    fun defaultOption(config: Param) {
        config["discerns"] = configDiscerns()

        Param.newInstance().add("algorithm", combatPowerAlgorithm())?.let {
            config["combatPower"] = it
        }

        maximum()?.let {
            config["maximum"] = it
        }

        minimum()?.let {
            config["minimum"]
        }
    }

    abstract override fun combatPowerAlgorithm(): Double

    fun enable() {
        discerns = section().getStringList("discerns")
        combatPowerAlgorithm = section().getString(
            "combatPower.algorithm",
            UmbrellaAttribute.instance.config.getString("plugin.defaultCombatPowerAlgorithm", "value * 1")
        )
        maximum = section().getDouble("maximum", Double.NaN)
        minimum = section().getDouble("minimum", Double.NaN)
        algorithm = section().getDouble("combatPower.algorithm")
    }

    fun combatPower(): Double {
        return 0.0
    }

    fun section(): ConfigurationSection {
        if (attribute.config().isConfigurationSection(name)) {
            return attribute.config().getConfigurationSection(name)
        } else {
            return attribute.config().createSection(name)
        }
    }

    fun inject() {
        attribute.internal.add(this)
    }

    fun correct(valueArray: ValueArray) {

        valueArray.values[ValueEnum.Number]?.let {
            it.forEachIndexed { index, _ -> it[index] = 0F }
        }
        valueArray.values[ValueEnum.Percent]?.let {
            it.forEachIndexed { index, _ -> it[index] = 0F }
        }

    }


    fun valueArray(string: String) = ValueArray(string)

    init {
        inject()
    }
}

enum class ValueEnum {
    Number, Percent, All
}