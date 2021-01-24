package ac.github.umbrella.internal.condition.sub

import ac.github.umbrella.internal.attribute.base.AbstractAttributeAdapter
import ac.github.umbrella.internal.attribute.base.ComposeItem
import ac.github.umbrella.internal.attribute.base.ifReplaceCharIndex
import ac.github.umbrella.internal.attribute.base.legal
import ac.github.umbrella.internal.condition.AbstractCondition
import ac.github.umbrella.internal.item.message
import ac.github.umbrella.util.SimpleDateFormatUtils
import cc.kunss.bountyhuntercore.common.container.Param
import org.bukkit.entity.LivingEntity

class DateTime : AbstractCondition() {
    override fun check(livingEntity: LivingEntity, composeItem: ComposeItem): Boolean {
        val itemStack = composeItem.item
        if (itemStack != null && itemStack.legal()) {
            val lore = itemStack.itemMeta.lore
            val list = config().getStringList("Lores")
            var indexOf = -1
            val firstOrNull = lore.firstOrNull {
                list.indexOfFirst { listIt ->
                    it.replace(AbstractAttributeAdapter.colorRegex, "").contains(listIt)
                }.apply { indexOf = this } != -1
            }
            if (firstOrNull != null) {
                val stringValue = firstOrNull.replace(AbstractAttributeAdapter.colorRegex, "")
                    .replace(list[indexOf], "")
                    .ifReplaceCharIndex(0, 0, ":", "")
                    .trim()
                val parseFormLong = SimpleDateFormatUtils.instance.parseFormLong(stringValue)
                if (System.currentTimeMillis() > parseFormLong) {
                    "PLUGIN.CONDITION.DATETIME-NOT-MATCH"
                        .message(
                            Param.newInstance().add("item", itemStack.itemMeta.displayName).add("time", stringValue)
                        )
                        .sendSender(livingEntity)
                    return false
                }
            }

        }

        return true

    }


}