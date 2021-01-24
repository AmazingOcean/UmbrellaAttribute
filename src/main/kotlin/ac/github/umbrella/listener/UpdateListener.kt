package ac.github.umbrella.listener

import ac.github.umbrella.UmbrellaAttribute
import ac.github.umbrella.api.EntityOperation
import ac.github.umbrella.api.event.EntityAttributeUpdateEvent
import ac.github.umbrella.api.event.UmbrellaPluginEnable
import ac.github.umbrella.internal.attribute.base.ComposeItem
import ac.github.umbrella.internal.nms.NMSComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.Inventory

class UpdateListener : Listener {

    val updateInventoryNames = arrayOf("container", "Repair", "Crafting")

    @EventHandler
    fun handleUpdate(e : EntityAttributeUpdateEvent) {
        if (e.livingEntity is Player) {

        }
    }

    @EventHandler
    fun join(e: PlayerJoinEvent) {
        EntityOperation.updateLivingEntity(e.player, true)
    }

    @EventHandler
    fun updateInventory(e: InventoryCloseEvent) {
        val player = e.player as Player
        val title = e.view.title
        if (updateInventoryNames.any { title.contains(it) }) {
            EntityOperation.updateLivingEntityInventory(player, true)
        }
    }

    @EventHandler
    fun dropItem(e: PlayerDropItemEvent) {
        EntityOperation.updateLivingEntityInventory(e.player, true)
    }

    @EventHandler
    fun pickUpItem(e: EntityPickupItemEvent) {
        EntityOperation.updateLivingEntityInventory(e.entity, true)
    }

    @EventHandler
    fun updateHeld(e: PlayerItemHeldEvent) {
        val inv: Inventory = e.player.inventory
        val newItem = inv.getItem(e.newSlot)
        EntityOperation.updatePlayerInventory(e.player as Player, newItem, ComposeItem.Slot.MAIN, async = true)
    }

    @EventHandler
    fun loadOnlinePlayers(e: UmbrellaPluginEnable) {
        Bukkit.getOnlinePlayers().forEach {
            EntityOperation.updateLivingEntity(it, true)
        }
    }

    @EventHandler
    fun quitPlayer(e: PlayerQuitEvent) {
        EntityOperation.clearEntity(e.player)
    }

    @EventHandler
    fun spawnEntity(e: CreatureSpawnEvent) {
        val livingEntity = e.entity
        livingEntity.isInvulnerable = true
        EntityOperation.updateLivingEntity(livingEntity, true) {
            livingEntity.isInvulnerable = false
        }
    }

    @EventHandler
    fun deathEntity(e: EntityDeathEvent) {
        EntityOperation.clearEntity(e.entity)
    }

    init {
        if (NMSComponent.version().split("_")[1].toInt() > 8) {
            object : Listener {
                @EventHandler
                fun swapHeldItem(e: PlayerSwapHandItemsEvent) {
                    EntityOperation.updatePlayerInventory(e.player, e.mainHandItem, ComposeItem.Slot.MAIN, async = true)
                    EntityOperation.updatePlayerInventory(e.player, e.offHandItem, ComposeItem.Slot.OFF_MAIN, async = true)
                }
            }.let { Bukkit.getPluginManager().registerEvents(it,UmbrellaAttribute.instance) }
        }
    }


}