package ac.github.umbrella.internal.nms.impl

import ac.github.umbrella.internal.attribute.base.ComposeItem
import ac.github.umbrella.internal.nms.obc
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.ItemStack

class NMS_v1_8_R3 : NMSBase() {

    override fun sendTitle(player: Player, title: String, subTitle: String, a: Long, b: Long, c: Long) {
        val obc = player.javaClass.obc()
        obc.invokeMethod(player,"sendTitle", arrayOf(title,subTitle))
    }

    override fun isUnbreakable(item: ItemStack): Boolean {
        val obc = item.javaClass.obc()
        val spigot = obc.fieldValue(item, "spigot")
        return spigot.javaClass.obc().invokeMethod(spigot,"isUnbreakable", emptyArray(),Boolean::class.java)
    }

    override fun setUnbreakable(item: ItemStack, unbreakable: Boolean): ItemStack {
        val obc = item.javaClass.obc()
        val spigot = obc.fieldValue(item, "spigot")
        spigot.javaClass.obc().invokeMethod(spigot,"setUnbreakable", arrayOf(unbreakable))
        return item;
    }

    override fun getInventoryItems(equipment: EntityEquipment): MutableList<ComposeItem> {
        val listOf = arrayListOf<ComposeItem>()
        getInventoryComposeItemBySlot(equipment,ComposeItem.Slot.MAIN)?.let { listOf.add(it) }
        getInventoryComposeItemBySlot(equipment,ComposeItem.Slot.HEAD)?.let { listOf.add(it) }
        getInventoryComposeItemBySlot(equipment,ComposeItem.Slot.CHEST)?.let { listOf.add(it) }
        getInventoryComposeItemBySlot(equipment,ComposeItem.Slot.LEGS)?.let { listOf.add(it) }
        getInventoryComposeItemBySlot(equipment,ComposeItem.Slot.FEET)?.let { listOf.add(it) }
        return listOf
    }

    override fun getInventoryComposeItemBySlot(equipment: EntityEquipment, slot: ComposeItem.Slot): ComposeItem? {
        val inventoryItemBySlot = getInventoryItemBySlot(equipment, slot) ?: return null
        return ComposeItem(slot,inventoryItemBySlot)
    }

    override fun getInventoryItemBySlot(equipment: EntityEquipment, slot: ComposeItem.Slot): ItemStack? {

        return when (slot) {
            ComposeItem.Slot.MAIN -> equipment::class.java.obc().invokeMethod(equipment, "getItemInHand", emptyArray(), ItemStack::class.java)
            ComposeItem.Slot.HEAD -> equipment.helmet
            ComposeItem.Slot.CHEST -> equipment.chestplate
            ComposeItem.Slot.LEGS -> equipment.leggings
            ComposeItem.Slot.FEET -> equipment.boots
            else -> null
        }

    }

    override fun valueOfMaterial(id: String): Material? {
        return if (numberRegex.matches(id)) Material.getMaterial(id.toInt()) else super.valueOfMaterial(id)
    }
}