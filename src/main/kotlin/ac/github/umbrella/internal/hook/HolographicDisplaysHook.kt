package ac.github.umbrella.internal.hook

import ac.github.umbrella.UmbrellaAttribute
import ac.github.umbrella.internal.`object`.HologramStack
import org.bukkit.Bukkit
import org.bukkit.Location

class HolographicDisplaysHook {

    companion object {

        lateinit var hologramStack: HologramStack

        fun initializer() {
            val configurationSection = UmbrellaAttribute.instance.config.getConfigurationSection("Plugin.Hologram")
            val split = configurationSection.getString("SourceLocation").split(",")
            hologramStack = HologramStack(
                configurationSection.getInt("Default"), configurationSection.getInt("AutoCreate"), Location(
                    Bukkit.getWorld(split[0]), split[1].toDouble(), split[2].toDouble(), split[3].toDouble()
                ),configurationSection.getInt("AnimationSpeed")
            )
        }
    }

    init {
        initializer()
    }

}