package ac.github.umbrella.internal.nms.bukkit

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import java.util.stream.Collectors

class BukkitItem(var material: Material) {

    val attributes: MutableList<BukkitItemAttribute> = arrayListOf()

    fun getItemAttributes(slot: EquipmentSlot): List<BukkitItemAttribute> {
        return attributes.stream().filter { attribute -> attribute.slot == slot }.collect(Collectors.toList())
    }

    companion object {
        fun create(material: Material, vararg attributes: BukkitItemAttribute): BukkitItem {
            val bukkitItem = BukkitItem(material)
            bukkitItem.attributes.addAll(attributes)
            return bukkitItem
        }
    }
}