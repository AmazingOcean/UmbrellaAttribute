package ac.github.umbrella.internal.event

import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

abstract class EventAdapter : Event(),Trigger {

    companion object {
        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }

    override fun getHandlers(): HandlerList {
        return EventAdapter.handlers;
    }

    override fun call() {
        Bukkit.getServer().pluginManager.callEvent(this)
    }

}