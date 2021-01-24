package ac.github.umbrella.internal.nms.impl

import ac.github.umbrella.internal.attribute.base.ComposeItem
import ac.github.umbrella.internal.item.message
import ac.github.umbrella.internal.nms.AbstractNMS
import ac.github.umbrella.internal.nms.ObjectClass
import ac.github.umbrella.internal.nms.obc
import net.minecraft.server.v1_12_R1.NBTTagCompound
import net.minecraft.server.v1_12_R1.PacketPlayOutChat
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.ItemStack
import java.lang.reflect.ParameterizedType


open class NMSBase : AbstractNMS() {

    companion object {
        val numberRegex = Regex("[0-9]+")
    }

    override fun updateAttack(item: ItemStack, speed: Double): ItemStack {

        if (item.type != null && item.type != Material.AIR) {
            val speedValue = speed - 4
            val craftItemStackObj = craftItemStackObc()
            val nmsItem = craftItemStackObj.invokeMethod(craftItemStackObj.clazz, "asNMSCopy", arrayOf(item))
            val nbtTagCompound = getTag(nmsItem)
            val nbtTagListObc = service("NBTTagList").obc()
            val modifiers = nbtTagListObc.newInstance(emptyArray())!!
            val nbtTagCompoundInstance = newNBTTagCompoundInstance()
            val nbtTagCompoundObc = nbtTagCompoundInstance!!.javaClass.obc()
            val setMethod = nbtTagCompoundObc.method("set", arrayOf(String::class.java, service("NBTBase")))
            nbtTagCompoundObc.invokeMethod(
                nbtTagCompoundInstance,
                setMethod,
                arrayOf("AttributeName", nbtTagListObc.newInstance(arrayOf("generic.attackSpeed"))!!)
            )
            nbtTagCompoundObc.invokeMethod(
                nbtTagCompoundInstance,
                setMethod,
                arrayOf("Name", nbtTagListObc.newInstance(arrayOf("AttackSpeed"))!!)
            )
            nbtTagCompoundObc.invokeMethod(
                nbtTagCompoundInstance,
                setMethod,
                arrayOf("Amount", nbtTagListObc.newInstance(arrayOf(speedValue - 4))!!)
            )
            nbtTagCompoundObc.invokeMethod(
                nbtTagCompoundInstance,
                setMethod,
                arrayOf("UUIDLeast", nbtTagListObc.newInstance(arrayOf(20000))!!)
            )
            nbtTagCompoundObc.invokeMethod(
                nbtTagCompoundInstance,
                setMethod,
                arrayOf("UUIDMost", nbtTagListObc.newInstance(arrayOf(1000))!!)
            )
            nbtTagCompoundObc.invokeMethod(
                nbtTagCompoundInstance,
                setMethod,
                arrayOf("Slot", nbtTagListObc.newInstance(arrayOf("mainhand"))!!)
            )
            nbtTagCompoundObc.invokeMethod(modifiers, "add", arrayOf(nbtTagCompoundInstance))
            nbtTagCompoundObc.invokeMethod(
                nbtTagCompoundInstance,
                setMethod,
                arrayOf("AttributeModifiers", nbtTagCompound)
            )
            craftItemStackObj.invokeMethod(nmsItem, "setTag", arrayOf(modifiers))
            return craftItemStackObj.invokeMethod(
                craftItemStackObj.clazz,
                "asBukkitCopy",
                arrayOf(nmsItem),
                ItemStack::class.java
            )
        }

        return item
    }

    override fun isUnbreakable(item: ItemStack): Boolean {
        return item.itemMeta.isUnbreakable
    }

    override fun getInventoryItems(equipment: EntityEquipment): MutableList<ComposeItem> {
        val listOf = arrayListOf<ComposeItem>()
        getInventoryComposeItemBySlot(equipment,ComposeItem.Slot.MAIN)?.let { listOf.add(it) }
        getInventoryComposeItemBySlot(equipment,ComposeItem.Slot.OFF_MAIN)?.let { listOf.add(it) }
        getInventoryComposeItemBySlot(equipment,ComposeItem.Slot.HEAD)?.let { listOf.add(it) }
        getInventoryComposeItemBySlot(equipment,ComposeItem.Slot.CHEST)?.let { listOf.add(it) }
        getInventoryComposeItemBySlot(equipment,ComposeItem.Slot.LEGS)?.let { listOf.add(it) }
        getInventoryComposeItemBySlot(equipment,ComposeItem.Slot.FEET)?.let { listOf.add(it) }
        return listOf
    }

    override fun getInventoryComposeItemBySlot(equipment: EntityEquipment, slot: ComposeItem.Slot): ComposeItem? {
        val inventoryItemBySlot = getInventoryItemBySlot(equipment, slot) ?: return null
        return ComposeItem(slot,inventoryItemBySlot)
    }

    override fun getInventoryItemBySlot(equipment: EntityEquipment, slot: ComposeItem.Slot): ItemStack? {

        return when (slot) {
            ComposeItem.Slot.MAIN -> equipment.itemInMainHand
            ComposeItem.Slot.OFF_MAIN -> equipment.itemInOffHand
            ComposeItem.Slot.HEAD -> equipment.helmet
            ComposeItem.Slot.CHEST -> equipment.chestplate
            ComposeItem.Slot.LEGS -> equipment.leggings
            ComposeItem.Slot.FEET -> equipment.boots
            ComposeItem.Slot.INVENTORY_SLOT -> null
        }

    }

    override fun valueOfMaterial(id: String): Material? {
        if (numberRegex.matches(id)) {
            "PLUGIN.ID-NOT-MATCH-SENDER".message().sendConsole()
            return null
        }
        return Material.valueOf(id.toUpperCase())
    }

    fun hasTag(nmsItem: Any): Boolean {
        return service("ItemStack").obc().invokeMethod(nmsItem, "hasTag", emptyArray()).toString().toBoolean()
    }

    fun getTag(nmsItem: Any): Any {
        return if (hasTag(nmsItem))
            service("ItemStack").obc().invokeMethod(nmsItem, "getTag", emptyArray(), service("NBTTagCompound")) else
            newNBTTagCompoundInstance()!!
    }

    fun newNBTTagCompoundInstance(): Any? {
        return service("NBTTagCompound").obc().newInstance(emptyArray())
    }

    override fun clearAttribute(item: ItemStack): ItemStack {
        val nmsItem: Any = asNMSCopy(item)
        val compound = getTag(nmsItem)
        val modifiers = service("NBTTagList").obc().newInstance(emptyArray())!!
        compound.javaClass.obc().invokeMethod(compound, "set", arrayOf("AttributeModifiers", modifiers))
        nmsItem.javaClass.obc().invokeMethod(nmsItem, "setTag", arrayOf(compound))
        item.itemMeta = asBukkitCopy(nmsItem).itemMeta;
        return item
    }

    override fun getAllNBT(item: ItemStack): String {
        val nmsItem = asNMSCopy(item)
        val itemTag: Any = getTag(nmsItem)
        return "§c[" + item.type.name + ":" + item.durability.toString() + "-" + item.hashCode()
            .toString() + "]§7 " + itemTag.toString().replace("§", "&")
    }

    // /ua give KunSs Default-1
    override fun setNBT(item: ItemStack, key: String, value: Any): ItemStack {
        val nmsCopy = asNMSCopy(item)
        val tag = getTag(nmsCopy)
        val covetNBTBase = covetNBTBase(value)
        val tagObc = tag.javaClass.obc()
        tagObc.invokeMethod(
            tag,
            tagObc.method("set", arrayOf(String::class.java, service("NBTBase"))),
            arrayOf(key, covetNBTBase)
        )

        nmsCopy.javaClass.obc().invokeMethod(nmsCopy, "setTag", arrayOf(tag))
        return asBukkitCopy(nmsCopy)
    }

    fun covetNBTBase(value: Any): Any {
        val arrayOf = arrayOf(value)
        return (if (value.javaClass.simpleName.toLowerCase()
                .contains("array") || value.javaClass.simpleName.toLowerCase().contains("list")
        ) {
            val listObc = service("NBTTagList").obc()
            val list = listObc.newInstance(emptyArray())!!
            (value as ArrayList<*>).forEach {
                listObc.invokeMethod(list, "add", arrayOf(covetNBTBase(it!!)))
            }
            list
        } else if (value is Long) {
            service("NBTTagLong").obc().newInstance(arrayOf)
        } else if (value is Int) {
            service("NBTTagInt").obc().newInstance(arrayOf)
        } else if (value is Byte) {
            service("NBTTagByte").obc().newInstance(arrayOf)
        } else if (value.javaClass.simpleName == "NBTTagCompound") {
            newNBTTagCompoundInstance()
        } else if (value is Double) {
            service("NBTTagDouble").obc().newInstance(arrayOf)
        } else if (value is Float) {
            service("NBTTagFloat").obc().newInstance(arrayOf)
        } else if (value is Short) {
            service("NBTTagShort").obc().newInstance(arrayOf)
        } else {
            service("NBTTagString").obc().newInstance(emptyArray())
        })!!
    }

    override fun getNBT(item: ItemStack, key: String): Any? {
        val tag = getTag(asNMSCopy(item))!!
        if (hasNBT(item, key)) {
            return tag.javaClass.obc().invokeMethod(tag, "get", arrayOf(key), service("NBTBase"))
        }
        return null;
    }

    override fun hasNBT(item: ItemStack, key: String): Boolean {
        val nmsCopy = asNMSCopy(item)
        val tag = getTag(nmsCopy)
        return tag.javaClass.obc().invokeMethod(tag, "hasKey", arrayOf(key)).toString().toBoolean()
    }


    fun asNMSCopy(item: ItemStack): Any {
        val craftItemStackObc = craftItemStackObc()
        return craftItemStackObc.invokeMethod(craftItemStackObc, "asNMSCopy", arrayOf(item))
    }

    fun asBukkitCopy(nmsItem: Any): ItemStack {
        val craftItemStackObc = craftItemStackObc()
        return craftItemStackObc.invokeMethod(
            craftItemStackObc,
            "asBukkitCopy",
            arrayOf(nmsItem),
            ItemStack::class.java
        )
    }

    fun craftItemStackObc(): ObjectClass {
        return service("CraftItemStack").obc();
    }


    override fun removeNBT(item: ItemStack, key: String): ItemStack {
        val craftItemStackObc = craftItemStackObc()
        val nmsItem = craftItemStackObc.invokeMethod(craftItemStackObc, "asNMSCopy", arrayOf(item))
        val tag = getTag(nmsItem)
        tag.javaClass.obc().invokeMethod(tag, "remove", arrayOf(key))
        tag.javaClass.obc().invokeMethod(nmsItem, "set", arrayOf(tag))
        item.itemMeta = craftItemStackObc.invokeMethod(
            craftItemStackObc,
            "asBukkitCopy",
            arrayOf(nmsItem),
            ItemStack::class.java
        ).itemMeta
        return item
    }

    override fun setUnbreakable(item: ItemStack, unbreakable: Boolean): ItemStack {
        val itemMeta = item.itemMeta
        val itemMetaObc = service("ItemMeta").obc()
        itemMetaObc.invokeMethod(itemMeta, "setUnbreakable", arrayOf(unbreakable))
        item.itemMeta = itemMeta;
        return item;
    }

    override fun sendTitle(player: Player, title: String, subTitle: String, a: Long, b: Long, c: Long) {
        player.sendTitle(title, subTitle, a.toInt(), b.toInt(), c.toInt())
    }


    override fun sendActionBar(player: Player, title: String) {

        val chatComponentText = service("ChatComponentText").obc().newInstance(arrayOf(title))
        val chatMessageType = service("ChatMessageType").obc().fieldValue("", "GAME_INFO")
        val packetPlayOutChat =
            service("PacketPlayOutChat").obc()
                .constructor(arrayOf(service("IChatBaseComponent"),service("ChatMessageType")))
                .newInstance(chatComponentText.apply { println("test=$this") }, chatMessageType.apply { println("type=$this") })

        val handle = player::class.java.obc().invokeMethod(player, "getHandle", emptyArray())
        val playerConnection = handle.javaClass.obc().fieldValue(handle, "playerConnection")
        val sendPacketMethod = playerConnection.javaClass.obc().method("sendPacket", arrayOf(service("Packet")))
        playerConnection.javaClass.obc().invokeMethod(playerConnection, sendPacketMethod, arrayOf(packetPlayOutChat!!))

    }

    override fun setDurability(itemStack: ItemStack, value: Int): ItemStack {
        itemStack.durability = value.toShort()
        return itemStack;
    }

    override fun getDurability(item: ItemStack): Short {
        return item.durability
    }
}