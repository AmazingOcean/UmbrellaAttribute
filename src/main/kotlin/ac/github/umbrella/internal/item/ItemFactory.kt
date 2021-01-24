package ac.github.umbrella.internal.item

import ac.github.umbrella.UmbrellaAttribute
import ac.github.umbrella.internal.item.sub.DefaultItemBuilder
import ac.github.umbrella.internal.language.Language
import ac.github.umbrella.internal.nms.NMSComponent
import ac.github.umbrella.util.SimpleDateFormatUtils
import ac.github.umbrella.util.Utils
import cc.kunss.bountyhuntercore.common.container.Param
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.lang.StringBuilder
import java.util.ArrayList
import kotlin.math.log

class ItemFactory {

    companion object {
        var instance: ItemFactory? = null
        val builders = arrayListOf<AbstractItemBuilder>()

        fun register(abstractItemBuilder: AbstractItemBuilder) {
            builders.add(abstractItemBuilder)
            "PLUGIN.LOAD-ITEM-BUILDER"
                .message(Param.newInstance().add("name", abstractItemBuilder::class.simpleName))
                .sendConsole()
        }

        fun instance() = instance!!

        fun init() {
            if (instance == null) {
                instance = ItemFactory()
            }
            instance!!.init()
        }

        fun registerDefault() {
            register(DefaultItemBuilder())
        }
    }

    var dataFolder = File(UmbrellaAttribute.instance.dataFolder, "item")
    var itemConfigSections = mutableMapOf<String, MutableList<ConfigurationSection>>()
    var randomConfigSections = mutableMapOf<String, List<String>>()

    fun hasItem(key: String): Boolean {
        return getItemConfigSection(key) != null
    }

    fun getItemConfigSection(key: String): ConfigurationSection? {
        itemConfigSections.forEach { entry ->
            entry.value.forEach {
                if (it.name == key) return it
            }
        }
        return null
    }

    fun getItem(player: Player?, key: String, lockMap: MutableMap<String, String> = mutableMapOf()): ItemStack? {
        val itemConfigSection = getItemConfigSection(key)!!
        var item = getItem(player, itemConfigSection,lockMap)
        if (item != null && item.type != Material.AIR) {
            val simpleName = UmbrellaAttribute::class.java.simpleName
            item = NMSComponent.impl().setNBT(item, "$simpleName-Key", key)
            item =
                NMSComponent.impl().setNBT(item, "$simpleName-Treaty", itemConfigSection.getString("Treaty", "final"))
            return item
        }
        return null
    }

    fun getItem(
        player: Player?,
        configurationSection: ConfigurationSection,
        lockMap: MutableMap<String, String> = mutableMapOf()
    ): ItemStack? {
        builders.firstOrNull {
            it::class == DefaultItemBuilder::class
        }?.let {
            return it.build(player, configurationSection, lockMap)
        }
        return null
    }

    fun init() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
            UmbrellaAttribute.instance.saveResource("item/DefaultItem.yml", false);
            UmbrellaAttribute.instance.saveResource("item/DefaultRandom.yml", false);
        }

        itemConfigSections.clear()
        randomConfigSections.clear()
        val listFiles = Utils.listFiles(dataFolder)
        if (listFiles.isNotEmpty()) {
            listFiles.forEach {
                val configuration = YamlConfiguration.loadConfiguration(it)
                configuration.getKeys(false).forEach { sectionKey ->

                    // TODO key修正
                    if (configuration.get(sectionKey) is String) {
                        randomConfigSections[sectionKey] = listOf(configuration.getString(sectionKey))
                    } else if (configuration.get(sectionKey) is List<*>) {
                        val list: MutableList<String> = ArrayList()
                        for (obj in configuration.getList(sectionKey)) {
                            if (obj is List<*>) {
                                val objList = obj as List<String>
                                val str = StringBuilder(if (objList.size > 0) objList[0] else "")
                                for (i in 1 until objList.size) {
                                    str.append("/n").append(objList[i])
                                }
                                list.add(str.toString())
                            } else {
                                list.add(obj.toString())
                            }
                        }
                        randomConfigSections[sectionKey] = list
                    } else if (configuration.isConfigurationSection(sectionKey)) {
                        val list = if (itemConfigSections.containsKey(it.path)) {
                            itemConfigSections[it.path]!!
                        } else {
                            val arrayListOf = arrayListOf<ConfigurationSection>()
                            itemConfigSections[it.path] = arrayListOf
                            arrayListOf
                        }
                        list.add(configuration.getConfigurationSection(sectionKey))
                    }

                }
            }
        }
        "PLUGIN.LOAD-ITEM-SECTION".message(
            Param.newInstance().add("amount", itemConfigSections.values.sumBy { it.size })
        ).sendConsole()
        "PLUGIN.LOAD-RANDOM-SECTION".message(Param.newInstance().add("amount", randomConfigSections.size)).sendConsole()
    }

    fun processRandomString(string: String?, lockMap: MutableMap<String, String>): String? {
        var string = string
        if (string != null) {
            // 固定随机
            val replaceLockStringList: List<String> = getStringList("<l:", ">", string)
            for (str in replaceLockStringList) {
                var randomStr = lockMap[str]
                if (randomStr == null) {
                    lockMap[str] = getRandomString(str, lockMap).also { randomStr = it }
                }
                string = string!!.replace("<l:$str>", randomStr!!)
            }
            // 普通随机
            val replaceStringList: List<String> = getStringList("<s:", ">", string!!)
            for (str in replaceStringList) {
                string = string!!.replaceFirst("<s:" + str + ">".toRegex(), getRandomString(str, lockMap))
            }
            // 数字随机
            val replaceIntList: List<String> = getStringList("<r:", ">", string!!)
            for (str in replaceIntList) {
                val strSplit = str.split("_").toTypedArray()
                if (strSplit.size > 1) {
                    val i1 = strSplit[0].toInt()
                    val i2 = strSplit[1].toInt() + 1
                    string = string!!.replaceFirst("<r:" + str + ">".toRegex(), (i1..i2).random().toString())
                }
            }
            // 小数随机
            val replaceDoubleList: List<String> = getStringList("<d:", ">", string!!)
            for (str in replaceDoubleList) {
                val strSplit = str.split("_").toTypedArray()
                if (strSplit.size > 1) {
                    val d1 = strSplit[0].toDouble()
                    val d2 = strSplit[1].toDouble()
                    string = string!!.replaceFirst(
                        "<d:" + str + ">".toRegex(),
                        UmbrellaAttribute.instance.decimalFormat.format(UmbrellaAttribute.instance.random.nextDouble() * (d2 - d1) + d1)
                    )
                }
            }
            // 日期随机
            val replaceTimeList: List<String> = getStringList("<t:", ">", string!!)
            if (replaceTimeList.isNotEmpty()) {
                for (str in replaceTimeList) {
                    val addTime = str + "000"
                    val time = System.currentTimeMillis() + java.lang.Long.valueOf(addTime)
                    string =
                        string!!.replaceFirst("<t:" + str + ">".toRegex(), SimpleDateFormatUtils.instance.format(time))
                }
            }
        }
        return string
    }


    fun getStringList(prefix: String?, suffix: String?, string: String): List<String> {
        val stringList: MutableList<String> = ArrayList()
        if (string.contains(prefix!!)) {
            val args = string.split(prefix).toTypedArray()
            if (args.size > 1 && args[1].contains(suffix!!)) {
                var i = 1
                while (i < args.size && args[i].contains(suffix)) {
                    stringList.add(args[i].split(suffix).toTypedArray()[0])
                    i++
                }
            }
        }
        return stringList
    }

    private fun getRandomString(string: String, lockMap: MutableMap<String, String>): String {
        var string = string
        val randomList = randomConfigSections[string]
        if (randomList != null) {
            string = randomList.random()
            for (str in getStringList("<l:", ">", string)) {
                var randomStr = lockMap[str]
                if (randomStr == null) {
                    lockMap[str] = getRandomString(str, lockMap).also { randomStr = it }
                }
                string = string.replace("<l:$str>", randomStr!!)
            }
            for (str2 in getStringList("<s:", ">", string)) {
                string = string.replaceFirst("<s:" + str2 + ">".toRegex(), getRandomString(str2, lockMap))
            }
            return string
        }
        return "%DeleteLore%"
    }

    enum class ConfigType {
        ITEM, RANDOM
    }
}

fun String.message(param: Param? = null): Language.LanguageMessage {
    return Language.instance.getString(this, param)
}