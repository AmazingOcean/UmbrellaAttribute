package ac.github.umbrella.api

import ac.github.umbrella.internal.attribute.Cabinet
import ac.github.umbrella.internal.attribute.base.AbstractAttributeInternal
import ac.github.umbrella.internal.attribute.base.AttributeFactory
import ac.github.umbrella.internal.attribute.base.ValueArray
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

class ItemOperation {

    companion object {

        fun loadItem(item : ItemStack?) : Cabinet {
            if (item != null && item.type != Material.AIR && item.hasItemMeta() && item.itemMeta.hasLore()) {
                val lore = item.itemMeta.lore
                return loadList(lore)
            }
            return Cabinet()
        }

        fun loadList(list: List<String>): Cabinet {
            val cabinet = Cabinet()
            list.forEach { string ->
                AttributeFactory.attributes.forEach {
                    val verification = it.verification(string)
                    if (verification != null) {
                        cabinet.addInternalValue(
                            it::class.java, verification::class.java, it.reader(string, verification)
                        )
                    }
                }
            }
            return cabinet
        }
    }

}