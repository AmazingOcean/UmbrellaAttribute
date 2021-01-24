package ac.github.umbrella.internal.attribute.attack

import ac.github.umbrella.UmbrellaAttribute
import ac.github.umbrella.api.event.EntityDamageEvent
import ac.github.umbrella.internal.attribute.base.*
import ac.github.umbrella.internal.attribute.base.annotation.AttributeMeta
import ac.github.umbrella.internal.attribute.base.event.DamageEvent
import ac.github.umbrella.internal.attribute.base.event.EventCentral
import ac.github.umbrella.internal.attribute.base.event.values
import cc.kunss.bountyhuntercore.common.container.Param
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

@AttributeMeta(
    plugin = UmbrellaAttribute::class,
    types = [AttributeType.ATTACK]
)
class Damage : AbstractAttributeAdapter(),Listener {

    override fun onLoad() {

        val damage = object : AbstractAttributeInternal(this, "Damage") {
            override fun configDiscerns(): List<String> = arrayOf("普通攻击", "攻击伤害", "普通伤害").toList()
            override fun combatPowerAlgorithm() = 1.0
        }

        val pvpDamage = object : AbstractAttributeInternal(this, "PVP") {
            override fun configDiscerns(): List<String> = arrayListOf("PVP攻击", "对玩家伤害")
            override fun combatPowerAlgorithm() = 1.0
        }

        object : AbstractAttributeInternal(this, "PVE") {
            override fun configDiscerns(): List<String> = arrayListOf("PVE伤害", "对怪物伤害")
            override fun combatPowerAlgorithm() = 1.0
        }

    }

    override fun onDisable() {

    }

    override fun calculation(): Double {
        return 0.0
    }

    override fun processor(): AbstractAttributeEventProcessor = object : AbstractAttributeEventProcessor() {
        override fun executor(eventCentral: EventCentral) {
            if (eventCentral is DamageEvent) {
                val executor = eventCentral.executor
                val values = executor.values(this@Damage)!!
                val attributeArrayList = values[queryInternalClass("Damage")]!!
                eventCentral.addDamage(attributeArrayList[0].toDouble())
            }
        }
    }

    @EventHandler
    fun damage(e: EntityDamageEvent) {
        print("-------------")
        val damageEvent = e.damageEvent
        attributeYaml.sendBattleMessage(
            damageEvent.executor,
            "Damage",
            Param.newInstance().add("value", damageEvent.damage),
            damageEvent.defender.location
        )

    }


}