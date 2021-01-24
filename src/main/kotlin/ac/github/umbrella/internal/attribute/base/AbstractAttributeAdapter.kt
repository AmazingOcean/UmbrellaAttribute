package ac.github.umbrella.internal.attribute.base

import ac.github.umbrella.UmbrellaAttribute
import ac.github.umbrella.internal.`object`.register
import ac.github.umbrella.internal.hook.HolographicDisplaysHook
import ac.github.umbrella.internal.nms.NMSComponent
import cc.kunss.bountyhuntercore.common.container.Param
import cc.kunss.bountyhuntercore.common.yaml.YamlConfig
import cc.kunss.bountyhuntercore.util.Utils
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

abstract class AbstractAttributeAdapter : Comparable<AbstractAttributeAdapter> {
    var priority = 0
    val internal = arrayListOf<AbstractAttributeInternal>()
    lateinit var attributeYaml: AttributeYaml

    companion object {
        val colorRegex = Regex("§[0-9a-zA-Z]")
    }

    fun initialEntity(plugin: JavaPlugin, file: File) {
        this.attributeYaml = AttributeYaml(file, plugin)
    }

    fun queryInternalClass(name: String): Class<out AbstractAttributeInternal>? {
        return (internal.firstOrNull { it.name == name } ?: return null)::class.java
    }

    fun saveYamlConfig() {
        if (!attributeYaml.file.exists()) {
            attributeYaml.onLoad(false)
            attributeYaml.loadConfig()
            val param = Param.newInstance()
            defaultConfigOption(param)
            param.forEach {
                attributeYaml.config.set(it.key, it.value)
            }
            config().save(attributeYaml.file)
        } else {
            attributeYaml.loadConfig()
        }
    }

    open fun defaultConfigOption(param: Param) {
        internal.forEach {
            val internalParam = Param.newInstance()
            it.defaultOption(internalParam)
            if (internalParam.size != 0) {
                param[it.name] = internalParam
            }
        }
    }

    fun config() = attributeYaml.config

    abstract fun onLoad()

    open fun onEnable() {
        internal.forEach { it.enable() }
    }

    abstract fun onDisable()

    abstract fun calculation(): Double

    override fun compareTo(other: AbstractAttributeAdapter): Int {
        return priority.compareTo(other.priority)
    }

    fun verification(string: String): AbstractAttributeInternal? {
        this.internal.forEach { attributeInternal ->
            val replace = string.replace(colorRegex, "")

            if (attributeInternal.discerns.any {
                    replace.contains(it)
                }) {
                return attributeInternal
            }
        }
        return null
    }

    fun reader(string: String, internal: AbstractAttributeInternal): ValueArray {
        return internal.valueArray(string)
    }

    abstract fun processor(): AbstractAttributeEventProcessor?

    class AttributeYaml(
        file: File, plugin: JavaPlugin
    ) : YamlConfig(file, plugin) {

        fun sendBattleMessage(livingEntity: LivingEntity?, path: String, param: Param = Param.newInstance(), target: Location? = null) {

            val abstractPath = "Message.$path"
            if (config.isString(abstractPath)) {
                val string = Utils.replace(config.getString(abstractPath),param.add("prefix",UmbrellaAttribute.instance.config.getString("Prefix","[UmbrellaAttribute]")))
                println("string=$string")
                if (string.startsWith("[ACTIONBAR]") && livingEntity is Player) {
                    NMSComponent.impl().sendActionBar(livingEntity,string.substring(11))
                } else if (string.startsWith("[TITLE]") && livingEntity is Player) {
                    val titleSplit = string.substring(7).split(":")
                    NMSComponent.impl().sendTitle(livingEntity,
                        titleSplit[0],
                        if (titleSplit.size == 2) titleSplit[1] else "",
                        5,
                        10,
                        5)
                } else if (string.startsWith("[HOLO]")) {
                    val substring = string.substring(6)
                    if (UmbrellaAttribute.hooks.contains("HolographicDisplays")) {
                        val hologram = HolographicDisplaysHook.hologramStack.pop()
                        hologram.teleport(target?.add(0.0,2.0,0.0))
                        hologram.appendTextLine(substring)
                        hologram.register()
                    } else {
                        sendBattleMessage(livingEntity, substring)
                    }
                } else {
                    livingEntity!!.sendMessage(string)
                }
            }
        }

    }

}