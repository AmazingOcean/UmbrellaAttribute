package ac.github.umbrella.internal.item

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class AbstractItemBuilder {


    abstract fun type(): String

    abstract fun build(player: Player?, config: ConfigurationSection,lockRandomMap : MutableMap<String,String>): ItemStack


}