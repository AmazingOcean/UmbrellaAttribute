package ac.github.umbrella.internal.language

import ac.github.umbrella.UmbrellaAttribute
import cc.kunss.bountyhuntercore.common.command.CommandAdapter
import cc.kunss.bountyhuntercore.common.container.Param
import cc.kunss.bountyhuntercore.util.Utils
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.EntityType

class Language(
    var configuration: FileConfiguration
) : CommandAdapter.CommandAdapterMessage(configuration) {

    val entityLocalKeys = mutableMapOf<String, String>()

    init {
        this.NOT_PERMISSION_EXECUTE = "PLUGIN.COMMAND.NOT-PERMISSION"
        this.NOT_FOUND_SUBCOMMAND = "PLUGIN.COMMAND.SUB-COMMAND-INVALID"
        entityLocalKeys.clear()
        if (this.configuration.isConfigurationSection("EntityLocalNameKeys")) {
            val configurationSection = configuration.getConfigurationSection("EntityLocalNameKeys")
            configurationSection.getKeys(false).forEach {
                entityLocalKeys[it.toUpperCase()] = configurationSection.getString(it)
            }
        }
    }

    fun getString(path: String): LanguageMessage {
        return getString(path, null)
    }

    fun getEntityLocalKeyName(entityType: EntityType) =
        entityLocalKeys.getOrDefault(entityType.name.toUpperCase(), entityType.name)

    fun getString(path: String, param: Param?): LanguageMessage {
        val string = configuration.getString(path, "error message")
        if (string == "error message") {
            print("Debug $path")
        }
        return LanguageMessage(Utils.replace(string, param ?: Param.newInstance()))
    }

    class LanguageMessage(var string: String) {

        init {
            string = Utils.replace(
                string,
                Param.newInstance().add("prefix", UmbrellaAttribute.instance.config.getString("Prefix"))
            )
        }

        fun sendConsole() {
            Bukkit.getConsoleSender().sendMessage("[UmbrellaAttribute] | $string")
        }

        fun sendSender(commandSender: CommandSender) {
            commandSender.sendMessage(string)
        }
    }

    companion object {

        lateinit var instance: Language

    }

}