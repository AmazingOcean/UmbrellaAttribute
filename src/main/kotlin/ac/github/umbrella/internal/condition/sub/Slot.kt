package ac.github.umbrella.internal.condition.sub

import ac.github.umbrella.internal.attribute.base.AbstractAttributeAdapter
import ac.github.umbrella.internal.attribute.base.ComposeItem
import ac.github.umbrella.internal.attribute.base.legal
import ac.github.umbrella.internal.condition.AbstractCondition
import ac.github.umbrella.internal.item.message
import cc.kunss.bountyhuntercore.common.container.Param
import org.bukkit.entity.LivingEntity

class Slot : AbstractCondition() {

    override fun check(livingEntity: LivingEntity, composeItem: ComposeItem): Boolean {

        val itemStack = composeItem.item
        if (itemStack != null && itemStack.legal()) {
            val itemMeta = itemStack.itemMeta
            val lore = itemMeta.lore
            if (config().isList(composeItem.slot.toString())) {
                val stringList = config().getStringList(composeItem.slot.toString())

                val result = stringList.contains("none") ||
                        stringList.any {
                            lore.any { loreIt ->
                                loreIt.replace(AbstractAttributeAdapter.colorRegex, "").contains(it)
                            }
                        }

                if (!result) {
                    "PLUGIN.CONDITION.SLOT-NOT-MATCH"
                        .message(
                            Param.newInstance().add("item", itemStack.itemMeta.displayName)
                                .add("name", stringList[0])
                        )
                        .sendSender(livingEntity)
                    return false
                }
            }
        }
        return true
    }

}