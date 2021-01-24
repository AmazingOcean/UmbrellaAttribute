package ac.github.umbrella.internal.item.sub

import ac.github.umbrella.UmbrellaAttribute
import ac.github.umbrella.internal.item.AbstractItemBuilder
import ac.github.umbrella.internal.item.ItemFactory
import ac.github.umbrella.internal.item.message
import ac.github.umbrella.internal.nms.NMSComponent
import ac.github.umbrella.util.CalculatorUtil
import ac.github.umbrella.util.SimpleDateFormatUtils
import cc.kunss.bountyhuntercore.common.container.Param
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import kotlin.collections.ArrayList

class DefaultItemBuilder : AbstractItemBuilder() {

    var localConfigKey = ThreadLocal.withInitial { String() }

    override fun type(): String = "Default"

    override fun build(
        player: Player?, config: ConfigurationSection, lockRandomMap: MutableMap<String, String>
    ): ItemStack {

        var displayName = ItemFactory.instance()
            .processRandomString(config.getString("Name") ?: "", lockRandomMap)!!.replace("%DeleteLore%", "")
        val ids = if (config.isList("ID")) config.getStringList("ID") else listOf(config.getString(".ID"))

        val id = ItemFactory.instance().processRandomString(ids.random(), lockRandomMap)
        val configLore = config.getStringList("Lore")
        var loreList = arrayListOf<String>()

        configLore.forEachIndexed { index, lore ->
            var lore = lore
            lore = ItemFactory.instance().processRandomString(lore, lockRandomMap) ?: ""
            if (!lore.contains("%DeleteLore%")) {
                loreList.addAll(lore.split("/n|\n"))
            }
        }

        if (UmbrellaAttribute.hooks.contains("PlaceholderAPI") && player != null) {
            displayName = PlaceholderAPI.setPlaceholders(player, displayName)
            loreList = PlaceholderAPI.setPlaceholders(player, loreList) as ArrayList<String>
        }

        // 计算器<c:>
        loreList.forEachIndexed { index, string ->
            var string = string
            val stringList = ItemFactory.instance().getStringList("<c:", ">", string)
            stringList.forEach {

                string = string
                    .replaceFirst(
                        ("<c:" + it.replace("*", "\\*")
                            .replace("(", "\\(")
                            .replace(")", "\\)")
                            .replace("+", "\\+") + ">").toRegex(),
                        UmbrellaAttribute.instance.decimalFormat.format(CalculatorUtil.getResult(it).toLong())
                    )
            }
            loreList[index] = string
        }

        val configEnchantList = config.getStringList("EnchantList")
        val enchantList = arrayListOf<String>()
        configEnchantList.forEachIndexed { index, enchant ->
            var enchant = enchant
            enchant = ItemFactory.instance().processRandomString(enchant, lockRandomMap) ?: ""
            if (!enchant.contains("%DeleteLore%")) {
                enchantList.addAll(enchant.split("//n|\n"))
            }
        }

        localConfigKey.remove()
        localConfigKey.set(config.name)
        var item = getItemStack(
            displayName,
            id!!,
            loreList,
            enchantList,
            config.getStringList("ItemFlagList"),
            config.getBoolean("Unbreakable", false),
            config.getColor("Color"),
            config.getString("SkullName")
        )
        if (item == null) {
            return ItemStack(Material.AIR)
        }
        if (lockRandomMap.isNotEmpty()) {
            val list: MutableList<String> = ArrayList()
            for ((key, value) in lockRandomMap) {
                list.add("$key§e§k|§e§r$value")
            }
        }
        if (item.hasItemMeta() && item.itemMeta.hasLore()) {
            if (config.getBoolean("ClearAttribute", false)) {
                item = NMSComponent.impl().clearAttribute(item)
            }
        }
        return item

    }

    private fun getItemStack(
        itemName: String?,
        id: String,
        loreList: MutableList<String>,
        enchantList: List<String> = arrayListOf(),
        itemFlagList: List<String> = arrayListOf(),
        unbreakable: Boolean = false,
        color: Color?,
        skullName: String?
    ): ItemStack? {
        var item: ItemStack
        val valueOfMaterial = try {
            NMSComponent.impl().valueOfMaterial(id)
        } catch (e: Exception) {
            "PLUGIN.ITEM.MATERIAL-NOT-MATCH".message(
                Param.newInstance().add("config", localConfigKey.get()).add("material", id)
            ).sendConsole()
            return null
        }

        if (valueOfMaterial == null) {
            "ID-NOT-MATCH-PLAYER".message().sendConsole()
            return null
        }

        item = ItemStack(valueOfMaterial)
        val idSplit = id.split(":").toTypedArray()
        if (item.type.maxDurability.toInt() != 0 && idSplit.size > 1) {
            item.durability = idSplit[1].toShort()
        }
        val meta = item.itemMeta
        if (itemName != null) {
            meta.displayName = itemName.replace("&", "§")
        }
        loreList.forEachIndexed { index, s ->
            loreList[index] = s.replace("&", "§")
        }
        meta.lore = loreList
        enchantList.forEachIndexed { index, string ->
            val split = string.split("/n")
            split.forEach {
                val enchantSplit = it.split(":")
                val enchantment = Enchantment.getByName(enchantSplit[0])
                val level = enchantSplit[1].toInt()
                if (enchantment != null && level != 0) {
                    meta.addEnchant(enchantment, level, true)
                }
            }
        }
        for (itemFlag in ItemFlag.values()) {
            if (itemFlagList.contains(itemFlag.name)) {
                meta.addItemFlags(itemFlag)
            }
        }
        if (color != null && meta is LeatherArmorMeta) {
            meta.color = color
        }
        if (skullName != null && meta is SkullMeta) {
            meta.owner = skullName
        }
        item.itemMeta = meta
        item = NMSComponent.impl().setUnbreakable(item, unbreakable)
        return item
    }
}

fun Collection<Any>.clone(): ArrayList<Any> {
    val arrayListOf = arrayListOf<Any>()
    arrayListOf.addAll(this)
    return arrayListOf
}