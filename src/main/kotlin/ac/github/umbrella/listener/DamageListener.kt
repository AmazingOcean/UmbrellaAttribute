package ac.github.umbrella.listener

import ac.github.umbrella.api.EntityOperation
import ac.github.umbrella.api.event.EntityDamageEvent
import ac.github.umbrella.internal.attribute.base.AttributeFactory
import ac.github.umbrella.internal.attribute.base.AttributeType
import ac.github.umbrella.internal.attribute.base.ComposeItem
import ac.github.umbrella.internal.attribute.base.event.DamageEvent
import ac.github.umbrella.internal.condition.sub.Slot
import ac.github.umbrella.internal.condition.sub.takeDurability
import ac.github.umbrella.internal.nms.NMSComponent
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class DamageListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun damageEvent(e: EntityDamageByEntityEvent) {

        if (e.isCancelled) return

        val entity = e.entity

        var executor: LivingEntity? = null
        val defender: LivingEntity? = if (entity is LivingEntity && entity !is ArmorStand) entity else null

        val damager = e.damager
        if (damager is Projectile && damager.shooter is LivingEntity) {
            executor = damager.shooter as LivingEntity
        } else if (damager is LivingEntity) executor = damager

        if (executor == null || defender == null) return


        val damageEvent = DamageEvent(e, executor, defender)
        AttributeFactory.attributes.forEach {
            val attributeMetaAnnotation = AttributeFactory.getAttributeMetaAnnotation(it::class)
            if (attributeMetaAnnotation != null) {
                it.processor()?.executor(damageEvent)

                if (damageEvent.event.isCancelled) {
                    return
                }
            }
        }
        val damage = damageEvent.damage.coerceAtLeast(0.0)
        if (damage < 0) {
            return
        }
        e.damage = damage

        takeLivingEntityDurability(
            executor,
            ComposeItem.Slot.MAIN,
            ComposeItem.Slot.OFF_MAIN,
            ComposeItem.Slot.INVENTORY_SLOT,
            update = true
        )
        takeLivingEntityDurability(
            executor,
            ComposeItem.Slot.HEAD,
            ComposeItem.Slot.CHEST,
            ComposeItem.Slot.LEGS,
            ComposeItem.Slot.FEET,
            ComposeItem.Slot.INVENTORY_SLOT,
            update = true
        )
        EntityDamageEvent(damageEvent).call()
    }

    fun takeLivingEntityDurability(livingEntity: LivingEntity, vararg slots: ComposeItem.Slot, update: Boolean) {
        val executorEquipment = livingEntity.equipment
        slots.forEach {
            if (it == ComposeItem.Slot.INVENTORY_SLOT && livingEntity is Player) {
                EntityOperation.loadSlotItems(livingEntity).forEach { entry ->
                    entry.key.item?.takeDurability(1)
                }
            } else {
                NMSComponent.impl().getInventoryItemBySlot(executorEquipment, it)?.takeDurability(1)
            }
        }
        if (update) {
            EntityOperation.updateLivingEntityInventory(livingEntity, true)
        }
    }

}