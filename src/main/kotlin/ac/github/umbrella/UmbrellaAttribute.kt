package ac.github.umbrella

import ac.github.umbrella.api.event.UmbrellaPluginEnable
import ac.github.umbrella.command.Command
import ac.github.umbrella.internal.attribute.base.AttributeFactory
import ac.github.umbrella.internal.condition.AbstractCondition
import ac.github.umbrella.internal.hook.HolographicDisplaysHook
import ac.github.umbrella.internal.hook.PlaceholderHook
import ac.github.umbrella.internal.item.ItemFactory
import ac.github.umbrella.internal.language.Language
import ac.github.umbrella.internal.language.LanguageFactory
import ac.github.umbrella.listener.PluginListener
import ac.github.umbrella.internal.nms.NMSComponent
import ac.github.umbrella.listener.DamageListener
import ac.github.umbrella.listener.UpdateListener
import ac.github.umbrella.util.SimpleDateFormatUtils
import cc.kunss.kotlinlibrary.KotlinLibrary
import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.text.DecimalFormat
import java.util.*
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class UmbrellaAttribute : JavaPlugin() {


    companion object {
        private val DEPENDS = arrayOf(
            "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-reflect/1.4.30-RC/kotlin-reflect-1.4.30-RC.jar",
            "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.0/gson-2.8.0.jar",
            "https://repo1.maven.org/maven2/org/apache/lucene/lucene-core/8.7.0/lucene-core-8.7.0.jar"
        )
        lateinit var instance: UmbrellaAttribute
        val scriptEngine: ScriptEngine = ScriptEngineManager().getEngineByName("js")
        var hooks = arrayListOf<String>()

        init {
            KotlinLibrary.loadUrls(DEPENDS.toList())
        }

    }

    var runType: PluginType = PluginType.LOAD
    lateinit var languageFactory: LanguageFactory
    lateinit var decimalFormat: DecimalFormat
    var random: Random = Random()
    val gson = Gson()

    override fun onLoad() {
        instance = this.apply { this.registerService() }
        programInjectLoad()
    }

    fun programInjectLoad() {
        saveDefaultConfig()
        languageFactory = LanguageFactory().apply { Language.instance = this.initLanguage() }
        SimpleDateFormatUtils.init()
        decimalFormat = DecimalFormat(config.getString("DecimalFormat"))
        AttributeFactory.registerDefault()
        AbstractCondition.registerDefault()
        ItemFactory.registerDefault()
    }

    override fun onEnable() {
        runType = PluginType.ENABLE
        Bukkit.getPluginManager().registerEvents(PluginListener(), this)
        programInjectEnable()

        Bukkit.getPluginManager().registerEvents(DamageListener(), this)
        Bukkit.getPluginManager().registerEvents(UpdateListener(), this)
        UmbrellaPluginEnable().call()
        getCommand("ua").setExecutor(Command()?.apply { Command.instance = this })
        registerHooks()
    }

    fun registerHooks() {
        hooks.clear()
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PlaceholderHook().let { hooks.add("PlaceholderAPI") }
        }
        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            HolographicDisplaysHook().let { hooks.add("HolographicDisplays") }
        }
    }

    fun programInjectEnable() {
        AttributeFactory.enableAttributes()
        ItemFactory.init()
    }

    override fun onDisable() {
        runType = PluginType.DISABLE
    }


    fun sendPluginMessage(string: String) {
        Language.LanguageMessage(string).sendConsole()
    }


    enum class PluginType {
        LOAD, ENABLE, DISABLE
    }
}

fun Any.registerService(): Any {
    NMSComponent.registerService(this)
    return this
}