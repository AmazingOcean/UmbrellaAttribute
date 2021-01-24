package ac.github.umbrella.api.event

import ac.github.umbrella.internal.attribute.base.AbstractAttributeAdapter
import ac.github.umbrella.internal.event.EventAdapter
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList

class AttributeEnableEvent(
    val abstractAttributeAdapter: AbstractAttributeAdapter
) : EventAdapter(), Cancellable {

    var cancel = false

    override fun isCancelled(): Boolean = cancel

    override fun setCancelled(cancel: Boolean) {
        this.cancel = cancel
    }

}