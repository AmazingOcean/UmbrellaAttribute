package ac.github.umbrella.internal.attribute

import ac.github.umbrella.internal.attribute.base.ComposeItem
import ac.github.umbrella.internal.attribute.lattice.InventoryLattice
import org.bukkit.inventory.ItemStack
import java.util.*

class EntityIntegrate(
    val uuid: UUID,
    val cabinet: Cabinet,
) {
    fun setInventoryItem(slot: ComposeItem.Slot, item: ItemStack?, rawSlot: Int? = -1) {
        val origin = InventoryLattice.cache[uuid]
        origin?.firstOrNull { if (slot == ComposeItem.Slot.INVENTORY_SLOT) it.rawSlot == rawSlot else slot == it.slot }
            ?.let {
                origin.remove(it)
                this.cabinet.dismantle(it.cabinet!!)
            }
        if (item != null) {
            val composeItem = ComposeItem(slot, item, rawSlot)
            origin?.add(composeItem)
            composeItem.initCabinet()
            this.cabinet.merger(composeItem.cabinet!!)
        }
    }

}