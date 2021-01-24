package ac.github.umbrella.internal.attribute.lattice

import ac.github.umbrella.api.EntityOperation
import ac.github.umbrella.api.event.EntityItemLoadAttributePostEvent
import ac.github.umbrella.internal.attribute.base.AbstractLattice
import ac.github.umbrella.internal.attribute.Cabinet
import ac.github.umbrella.internal.attribute.base.ComposeItem
import ac.github.umbrella.internal.nms.NMSComponent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*

class InventoryLattice : AbstractLattice() {

    companion object {
        val cache = mutableMapOf<UUID, MutableList<ComposeItem>>()
    }

    override fun extract(livingEntity: LivingEntity): Cabinet {
        val cabinet = Cabinet()
        cache[livingEntity.uniqueId]?.let {
            it.forEach { composeItem ->
                if (composeItem.cabinet != null) {

                    cabinet.dismantle(composeItem.cabinet!!)
                }
            }
        }
        val inventoryItems = NMSComponent.impl().getInventoryItems(livingEntity.equipment)
            .filter { EntityOperation.checkCondition(livingEntity, it) }.toMutableList()
        if (livingEntity is Player) {
            EntityOperation.loadSlotItems(livingEntity).forEach {
                inventoryItems.add(it.key)
            }
        }
        cache[livingEntity.uniqueId] = inventoryItems
        inventoryItems.forEach {
            it.initCabinet()
            cabinet.merger(it.cabinet!!)
            EntityItemLoadAttributePostEvent(livingEntity, it, cabinet)
        }
        return cabinet
    }
}