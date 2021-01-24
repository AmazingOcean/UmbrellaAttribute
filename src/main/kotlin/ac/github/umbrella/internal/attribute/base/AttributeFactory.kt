package ac.github.umbrella.internal.attribute.base

import ac.github.umbrella.UmbrellaAttribute
import ac.github.umbrella.api.event.AttributeEnableEvent
import ac.github.umbrella.internal.attribute.attack.Damage
import ac.github.umbrella.internal.attribute.base.annotation.AttributeMeta
import ac.github.umbrella.internal.item.message
import ac.github.umbrella.internal.nms.NMSComponent
import ac.github.umbrella.internal.nms.obc
import cc.kunss.bountyhuntercore.common.container.Param
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.reflect.KClass

class AttributeFactory {

    companion object {

        val registryManager = arrayListOf<KClass<out AbstractAttributeAdapter>>()
        var attributes = arrayListOf<AbstractAttributeAdapter>()

        fun distribute(kClass: KClass<out AbstractAttributeAdapter>): Int {
            val stringList = UmbrellaAttribute.instance.config.getStringList("AttributeQueue")
            val indexOfFirst = stringList.indexOfFirst { it.equals(kClass.simpleName, ignoreCase = true) }
            return if (indexOfFirst != -1) indexOfFirst * 10 else -1;
        }

        fun enableAttributes() {
            registryManager.forEach {
                enableAttribute(it)
            }
        }

        fun loads() {
            attributes.forEach { it.onLoad() }
        }

        fun enables() {
            attributes.forEach { it.onEnable() }
        }

        fun disables() {
            attributes.forEach { it.onDisable() }
        }

        fun getAttributeMetaAnnotation(kClass: KClass<out AbstractAttributeAdapter>): AttributeMeta? {
            return getAttributeMetaAnnotation(kClass.java)
        }

        fun getAttributeMetaAnnotation(clazz: Class<out AbstractAttributeAdapter>): AttributeMeta {
            return clazz.getAnnotation(AttributeMeta::class.java)
        }

        fun enableAttribute(kClass: KClass<out AbstractAttributeAdapter>): Boolean {
            val injectPriority = injectPriority(kClass)
            if (injectPriority != -1) {
                val annotation = getAttributeMetaAnnotation(kClass)
                if (annotation != null) {
                    val attributeMeta = annotation as AttributeMeta
                    val plugin = attributeMeta.plugin
                    val queryPlugin =
                        NMSComponent.getInstanceServiceByClass(plugin.java)
                    if (queryPlugin != null) {
                        queryPlugin.value as JavaPlugin
                        val instance = kClass.java.obc().newInstance(emptyArray()) as AbstractAttributeAdapter
                        instance.initialEntity(queryPlugin.value, generatorAttributeFile(queryPlugin.value, kClass))
                        attributes.add(instance)
                        val event = AttributeEnableEvent(instance)
                        event.call()
                        if (!event.isCancelled) {
                            "PLUGIN.ATTRIBUTE.ENABLE-ATTRIBUTE-SUCCESSFUL"
                                .message(
                                    Param.newInstance().add("name", kClass.simpleName)
                                        .add("index", injectPriority)
                                )
                                .sendConsole()

                            if (instance is Listener)
                                Bukkit.getPluginManager().registerEvents(instance,UmbrellaAttribute.instance)

                            instance.onLoad()
                            instance.saveYamlConfig()
                            instance.onEnable()
                        }
                        return !event.isCancelled
                    } else {
                        // PLUGIN-INVALID
                        "PLUGIN.ATTRIBUTE.PLUGIN-INVALID"
                            .message(
                                Param.newInstance().add("name", kClass.simpleName)
                                    .add("plugin", plugin.simpleName)
                            )
                            .sendConsole()
                    }
                } else {
                    // INVALID
                    "PLUGIN.ATTRIBUTE.INVALID"
                        .message(
                            Param.newInstance().add("name", kClass.simpleName)
                                .add("index", injectPriority)
                        )
                        .sendConsole()
                }
            } else {
                "PLUGIN.ATTRIBUTE.ENABLE-ATTRIBUTE-ERROR-BY-PRIORITY"
                    .message(Param.newInstance().add("name", kClass.simpleName))
                    .sendConsole()
            }
            return false
        }

        fun generatorAttributeFileName(plugin: JavaPlugin, kClass: KClass<out AbstractAttributeAdapter>): String {
            return UmbrellaAttribute.instance.dataFolder.path + "/attribute/${plugin.description.name}/${kClass.simpleName}.yml"
        }

        fun generatorAttributeFile(plugin: JavaPlugin, kClass: KClass<out AbstractAttributeAdapter>): File {
            return File(generatorAttributeFileName(plugin, kClass))
        }

        fun injectPriority(kClass: KClass<out AbstractAttributeAdapter>) = distribute(kClass)

        fun registerAttribute(kClass: KClass<out AbstractAttributeAdapter>) {
            this.registryManager.add(kClass)
        }

        fun registerDefault() {
            registerAttribute(Damage::class)
        }


    }

}