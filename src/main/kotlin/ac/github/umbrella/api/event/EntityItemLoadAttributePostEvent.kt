package ac.github.umbrella.api.event

import ac.github.umbrella.internal.attribute.Cabinet
import ac.github.umbrella.internal.attribute.base.ComposeItem
import ac.github.umbrella.internal.event.EventAdapter
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

class EntityItemLoadAttributePostEvent(
    val livingEntity: LivingEntity,composeItem: ComposeItem,cabinet: Cabinet
) : EventAdapter() {


}