package ac.github.umbrella.internal.condition

import ac.github.umbrella.UmbrellaAttribute
import ac.github.umbrella.internal.attribute.base.ComposeItem
import ac.github.umbrella.internal.condition.sub.*
import ac.github.umbrella.internal.item.message
import ac.github.umbrella.internal.language.Language
import cc.kunss.bountyhuntercore.common.container.Param
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.LivingEntity

abstract class AbstractCondition {

    companion object {
        val conditions = arrayListOf<AbstractCondition>()

        fun register(abstractCondition: AbstractCondition) {
            if (UmbrellaAttribute.instance.config.getStringList("Conditions")
                    .contains(abstractCondition::class.simpleName)
            ) {
                conditions.add(abstractCondition)
                "PLUGIN.CONDITION.ENABLE-CONDITION"
                    .message(
                        Param.newInstance().add(
                            "name",
                            abstractCondition::class.simpleName
                        )
                    ).sendConsole()
            } else {
                "PLUGIN.CONDITION.FAIL-CONDITION"
                    .message(
                        Param.newInstance().add(
                            "name",
                            abstractCondition::class.simpleName
                        )
                    )
                    .sendConsole()
            }
        }


        fun registerDefault() {

            register(Level())
            register(Permission())
            register(DateTime())
            register(Durability())
            register(Slot())
        }
    }

    fun config(): ConfigurationSection {
        return UmbrellaAttribute.instance.config.getConfigurationSection("Condition.${this::class.simpleName}")
    }

    abstract fun check(livingEntity: LivingEntity, composeItem: ComposeItem): Boolean

}