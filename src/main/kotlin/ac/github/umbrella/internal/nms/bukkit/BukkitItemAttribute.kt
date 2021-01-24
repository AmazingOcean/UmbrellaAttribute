package ac.github.umbrella.internal.nms.bukkit

import cc.kunss.bountyhuntercore.common.container.Param
import org.bukkit.inventory.EquipmentSlot


class BukkitItemAttribute(
    var slot: EquipmentSlot = EquipmentSlot.HAND
) : Param() {

    companion object {

        fun instance(): BukkitItemAttribute {
            return BukkitItemAttribute()
        }

        fun instance(slot: EquipmentSlot): BukkitItemAttribute {
            return BukkitItemAttribute(slot)
        }
    }



    fun setBase(attributeType: BukkitItemAttributeType, value: Any): BukkitItemAttribute {
        attributeType.setBase(this, value)
        return this
    }
}