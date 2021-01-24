package ac.github.umbrella.api

import ac.github.umbrella.UmbrellaAttribute
import ac.github.umbrella.api.event.EntityAttributeUpdateEvent
import ac.github.umbrella.internal.condition.AbstractCondition
import ac.github.umbrella.internal.attribute.base.AbstractLattice
import ac.github.umbrella.internal.attribute.Cabinet
import ac.github.umbrella.internal.attribute.EntityIntegrate
import ac.github.umbrella.internal.attribute.base.AbstractAttributeAdapter
import ac.github.umbrella.internal.attribute.base.ComposeItem
import ac.github.umbrella.internal.attribute.lattice.InventoryLattice
import ac.github.umbrella.internal.item.message
import ac.github.umbrella.internal.language.Language
import cc.kunss.bountyhuntercore.common.container.Param
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

/**
 * 模块化柜子
 */
class EntityOperation {

    companion object {

        private val extra = mutableMapOf<UUID, MutableMap<Class<*>, Cabinet>>()
        private val map = mutableMapOf<UUID, EntityIntegrate>()

        fun updateLivingEntity(
            livingEntity: LivingEntity,
            async: Boolean = false,
            callback: () -> Unit = {}
        ) {
            val entityIntegrate = getEntityIntegrate(livingEntity)
            AbstractLattice.lattices.forEach {
                updateLivingEntityLattice(livingEntity, it, async)
                callback()
                EntityAttributeUpdateEvent(livingEntity, entityIntegrate)
            }
        }

        fun loadSlotItems(player: Player): MutableMap<ComposeItem, Int> {
            val inventory = player.inventory
            val mutableMapOf = mutableMapOf<ComposeItem, Int>()
            UmbrellaAttribute.instance.config.getStringList("Slot-item").forEach {
                val slot: Int
                var keyword: String? = null
                if (it.contains("#")) {
                    val split = it.split("#")
                    slot = split[0].toInt()
                    keyword = it.replace("$slot#", "")
                } else {
                    slot = it.toInt()
                }
                val item = inventory.getItem(slot)
                val composeItem = ComposeItem(ComposeItem.Slot.INVENTORY_SLOT, item, slot)
                if (item != null && item.type != Material.AIR) {
                    if (checkCondition(player, composeItem)) {
                        if (keyword != null) {
                            val lore =
                                if (item.hasItemMeta() && item.itemMeta.hasLore()) item.itemMeta.lore else arrayListOf()
                            if (lore.any { loreString ->
                                    loreString.replace(AbstractAttributeAdapter.colorRegex, "").contains(keyword)
                                }) {
                                mutableMapOf[composeItem] = slot
                            } else {
                                "PLUGIN.ATTRIBUTE.SLOT-KEYWORD-NOT-MATCH"
                                    .message(Param.newInstance().add("slot", keyword))
                                    .sendSender(player)
                            }
                        } else {
                            mutableMapOf[composeItem] = slot
                        }
                    }
                }
            }
            return mutableMapOf
        }



        fun checkCondition(livingEntity: LivingEntity, composeItem: ComposeItem?): Boolean {
            return composeItem == null || AbstractCondition.conditions.all { it.check(livingEntity, composeItem)}
        }

        fun updateLivingEntityInventory(livingEntity: LivingEntity, async: Boolean = false) {
            val abstractLattice = AbstractLattice.getLatticeByClass(InventoryLattice::class.java)!!
            updateLivingEntityLattice(livingEntity, abstractLattice, async)
            EntityAttributeUpdateEvent(livingEntity, getEntityIntegrate(livingEntity)).call()
        }

        fun updatePlayerInventory(
            player: Player, itemStack: ItemStack?, slot: ComposeItem.Slot, rawSlot: Int? = -1,
            async: Boolean = false
        ) {
            fun update() {
                val entityIntegrate = getEntityIntegrate(player)
                entityIntegrate.setInventoryItem(
                    slot,
                    if (checkCondition(player, ComposeItem(slot, itemStack, rawSlot))) itemStack else null,
                    rawSlot
                )
                EntityAttributeUpdateEvent(player, entityIntegrate).call()
            }
            if (async) {
                object : BukkitRunnable() {
                    override fun run() {
                        update()
                    }
                }.runTaskAsynchronously(UmbrellaAttribute.instance)
            } else {
                update()
            }

        }

        fun updateLivingEntityLattice(
            livingEntity: LivingEntity,
            abstractLattice: AbstractLattice,
            async: Boolean = false
        ) {

            fun update() {
                val cabinet = abstractLattice.extract(livingEntity)
                val entityIntegrate = getEntityIntegrate(livingEntity)
                entityIntegrate.cabinet.merger(cabinet)
            }
            if (async) {
                object : BukkitRunnable() {
                    override fun run() {
                        update()
                    }
                }.runTaskAsynchronously(UmbrellaAttribute.instance)
            } else {
                update()
            }
        }

        fun clearEntity(uuid: UUID) {
            this.map.remove(uuid)
        }

        fun clearEntity(livingEntity: LivingEntity) {
            clearEntity(livingEntity.uniqueId)
        }

        fun setEntityCabinet(uuid: UUID, clazz: Class<Any>, cabinet: Cabinet) {
            val extraByUUID = getExtraByUUID(uuid)
            extraByUUID[clazz] = cabinet
            getEntityIntegrate(uuid).let {
                it.cabinet.merger(cabinet)
                EntityAttributeUpdateEvent(Bukkit.getEntity(uuid)!! as LivingEntity, it)
            }
        }

        fun removeEntityCabinet(uuid: UUID, clazz: Class<*>) {
            val extraByUUID = getExtraByUUID(uuid)
            if (extraByUUID.isNotEmpty()) {
                if (extraByUUID.containsKey(clazz)) {
                    val cabinet = extraByUUID[clazz]!!
                    extraByUUID.remove(clazz)
                    getEntityIntegrate(uuid).let {
                        it.cabinet.dismantle(cabinet)
                        EntityAttributeUpdateEvent(Bukkit.getEntity(uuid)!! as LivingEntity, it)
                    }
                }
            }
        }

        fun clearEntityCabinetAll(uuid: UUID) {
            val extraByUUID = getExtraByUUID(uuid)
            if (extraByUUID.isNotEmpty()) {
                val entityIntegrate = getEntityIntegrate(uuid)
                extraByUUID.forEach { entry ->
                    val cabinet = entry.value
                    entityIntegrate.cabinet.dismantle(cabinet)
                }
                EntityAttributeUpdateEvent(Bukkit.getEntity(uuid)!! as LivingEntity, entityIntegrate)
            }
            extraByUUID.clear()
        }

        fun getExtraByUUID(uuid: UUID): MutableMap<Class<*>, Cabinet> {
            return if (extra.containsKey(uuid)) {
                extra[uuid]!!
            } else {
                extra[uuid] = mutableMapOf()
                getExtraByUUID(uuid)
            }
        }

        fun getEntityIntegrate(livingEntity: LivingEntity): EntityIntegrate = getEntityIntegrate(livingEntity.uniqueId)

        fun getEntityIntegrate(uuid: UUID): EntityIntegrate {
            return if (map.containsKey(uuid)) map[uuid]!! else {
                map[uuid] = EntityIntegrate(uuid,Cabinet())
                getEntityIntegrate(uuid)
            }
        }

    }

}