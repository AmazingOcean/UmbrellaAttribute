package ac.github.umbrella.internal.attribute.base.event

import ac.github.umbrella.api.EntityOperation
import ac.github.umbrella.internal.attribute.base.AbstractAttributeAdapter
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent

class DamageEvent(
    val event: EntityDamageByEntityEvent,
    val executor: LivingEntity,
    val defender: LivingEntity
) : EventCentral {

    var damage = event.damage
    val labels = mutableMapOf<String, Any>()

    fun takeDamage(value: Double) {
        this.damage = damage - value
    }

    fun setLabel(key: String, value: Any): DamageEvent {
        labels[key] = value
        return this;
    }

    fun hasLabel(key: String) = labels.containsKey(key)

    fun getLabel(key: String, def: Any? = null) = if (hasLabel(key)) labels[key] else def

    fun addDamage(value: Double) {
        this.damage = damage + value
    }

}

fun LivingEntity.integrate() = EntityOperation.getEntityIntegrate(this)

fun LivingEntity.values(abstractAttributeAdapter: AbstractAttributeAdapter) =
    integrate().cabinet.station[abstractAttributeAdapter::class.java]

fun LivingEntity.values(clazz: Class<AbstractAttributeAdapter>) = integrate().cabinet.station[clazz]

fun LivingEntity.originValues(abstractAttributeAdapter: AbstractAttributeAdapter) =
    integrate().cabinet.baseValues(abstractAttributeAdapter::class.java)

fun LivingEntity.originValues(clazz: Class<AbstractAttributeAdapter>) = integrate().cabinet.baseValues(clazz)