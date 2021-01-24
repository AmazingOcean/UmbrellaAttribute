package ac.github.umbrella.internal.condition.sub

import ac.github.umbrella.internal.attribute.base.*
import ac.github.umbrella.internal.condition.AbstractCondition
import ac.github.umbrella.internal.item.message
import ac.github.umbrella.internal.nms.NMSComponent
import cc.kunss.bountyhuntercore.common.container.Param
import net.minecraft.server.v1_12_R1.SoundEffects.du
import net.minecraft.server.v1_12_R1.SoundEffects.it
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Repairable
import kotlin.math.max

class Durability : AbstractCondition() {

    init {
        lores = config().getStringList("Lores")
        splits = config().getStringList("Splits")
    }

    override fun check(livingEntity: LivingEntity, composeItem: ComposeItem): Boolean {
        val itemStack = composeItem.item
        if (itemStack != null && itemStack.legal()) {
            if (getDurability(itemStack) == 1) {
                "PLUGIN.CONDITION.DURABILITY-EMPTY"
                    .message(Param.newInstance().add("item", itemStack.itemMeta.displayName))
                    .sendSender(livingEntity)
            }
        }
        return true
    }

    companion object {

        private lateinit var lores: List<String>
        private lateinit var splits: List<String>

        // TODO 完成攻击扣除耐久
        fun takeDurability(itemStack: ItemStack?, value: Int): ItemStack? {

            if (itemStack != null && itemStack.legal()) {
                val durability = getDurability(itemStack)
                if (durability != -1) {
                    val itemMeta = itemStack.itemMeta
                    val lore = itemMeta.lore
                    lore.forEachIndexed { index, string ->
                        lores.firstOrNull {
                            string.replace(AbstractAttributeAdapter.colorRegex, "").contains(it)
                        }?.let {
                            lore[index] = string.replaceFirst(durability.toString(), (durability - value).toString())
                            itemMeta.lore = lore
                            itemStack.itemMeta = itemMeta
                            return itemStack
                        }
                    }
                }
                return itemStack
            }
            return null
        }

        fun editDurability(item: ItemStack, takeDurability: Int): Boolean {
            var item = item
            var takeDurability = takeDurability
            if (item.legal() && !NMSComponent.impl().isUnbreakable(item)) {
                val meta = item.itemMeta
                val loreList = meta.lore
                takeDurability = if (item.type.toString().contains("_") && "SPADE|PICKAXE|AXE|HDE".contains(
                        item.type.toString().split("_").toTypedArray()[1]
                    )
                ) 1 else takeDurability

                loreList.forEachIndexed { index, string ->
                    var indexOf = -1
                    lores.indexOfFirst { listIt ->
                        string.replace(AbstractAttributeAdapter.colorRegex, "").contains(listIt)
                    }.apply { indexOf = this } != -1

                    if (indexOf != -1) {
                        val durability = getDurability(string, indexOf)
                        val maxDurability = getMaxDurability(string)
                        val resultDurability = max(durability - takeDurability, 0).coerceAtMost(maxDurability)
                        loreList[index] = string.replaceFirst(durability.toString(), resultDurability.toString())

                        meta.lore = loreList


                        item.itemMeta = meta

                        if (item.type.maxDurability.toInt() != 0) {
                            val defaultDurability = durability.toDouble() / maxDurability * item.type.maxDurability
                            val i1 = (item.type.maxDurability - defaultDurability).toInt()
                            item = NMSComponent.impl().setDurability(item, i1)
                        }
                        return true
                    }
                }
            }
            return false
        }

        fun getMaxDurability(lore: String): Int {
            var split: String? = null
            splits.any {
                lore.contains(it).apply { if (this) split = it }
            }
            if (split != null) {
                return ValueArray.getValue(lore.split(split!!)[1]).toInt()
            } else {
                return -1
            }
        }

        fun getDurability(string: String, indexOf: Int): Int {
            val stringValue = string.replace(AbstractAttributeAdapter.colorRegex, "")
                .replace(lores[indexOf], "").trim()
                .ifReplaceCharIndex(0, 0, ":", "")
            val split = splits.firstOrNull { stringValue.contains(it) }
            var durabilityValue = 0
            if (split != null) {
                val strings = stringValue.split(split)
                strings[0].toInt().also { durabilityValue = it }
            } else {
                durabilityValue = stringValue.toInt()
            }
            return durabilityValue
        }

        fun getDurability(itemStack: ItemStack): Int {
            val lore = itemStack.itemMeta.lore
            var indexOf = -1
            val firstOrNull = lore.firstOrNull {
                lores.indexOfFirst { listIt ->
                    it.replace(AbstractAttributeAdapter.colorRegex, "").contains(listIt)
                }.apply { indexOf = this } != -1
            }
            if (firstOrNull != null) {
                return getDurability(firstOrNull, indexOf)
            }
            return -1
        }
    }

}

fun ItemStack.getDurability(): Int = Durability.getDurability(this)

fun ItemStack.editDurability(value: Int) = Durability.editDurability(this, value)

fun ItemStack.takeDurability(value: Int) = Durability.takeDurability(this, value)