package ac.github.umbrella.internal.condition.sub

import ac.github.umbrella.internal.attribute.base.AbstractAttributeAdapter
import ac.github.umbrella.internal.attribute.base.ComposeItem
import ac.github.umbrella.internal.attribute.base.ValueArray
import ac.github.umbrella.internal.attribute.base.legal
import ac.github.umbrella.internal.condition.AbstractCondition
import ac.github.umbrella.internal.item.message
import ac.github.umbrella.internal.language.Language
import cc.kunss.bountyhuntercore.common.container.Param
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class Level : AbstractCondition() {

    override fun check(livingEntity: LivingEntity, composeItem: ComposeItem): Boolean {
        val itemStack = composeItem.item
        if (itemStack != null && itemStack.legal() && livingEntity is Player) {
            val lore = itemStack.itemMeta.lore
            val list = config().getStringList("Lores")
            val firstOrNull = lore.firstOrNull {
                list.any { listIt ->
                    it.replace(AbstractAttributeAdapter.colorRegex, "").contains(listIt)
                }
            }
            if (firstOrNull != null) {
                val number = ValueArray.getValue(firstOrNull).toInt()
                if (livingEntity.level < number) {
                    "PLUGIN.CONDITION.LEVEL-NOT-MATCH"
                        .message(Param.newInstance().add("item",itemStack.itemMeta.displayName))
                        .sendSender(livingEntity)

                    return false
                }
            }
        }
        return true
    }
}