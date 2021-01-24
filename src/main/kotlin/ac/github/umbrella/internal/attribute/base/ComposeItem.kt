package ac.github.umbrella.internal.attribute.base

import ac.github.umbrella.api.ItemOperation
import ac.github.umbrella.internal.attribute.Cabinet
import net.minecraft.server.v1_12_R1.SoundEffects.id
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.isAccessible

class ComposeItem(
    val slot: Slot,
    val item: ItemStack?,
    val rawSlot: Int? = -1
) {

    var cabinet: Cabinet? = null

    fun initCabinet(): ComposeItem {
        cabinet = ItemOperation.loadItem(item)
        return this
    }

    override fun toString(): String {
        return "ComposeItem(slot=$slot, item=$item, rawSlot=$rawSlot, cabinet=$cabinet)"
    }


    enum class Slot() {
        MAIN,
        OFF_MAIN,
        HEAD,
        FEET,
        LEGS,
        CHEST,
        INVENTORY_SLOT
    }

}