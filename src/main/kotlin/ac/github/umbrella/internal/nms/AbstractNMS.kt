package ac.github.umbrella.internal.nms

import ac.github.umbrella.internal.attribute.base.ComposeItem
import ac.github.umbrella.util.Reflect
import org.bukkit.Material
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.ItemStack
import sun.instrument.InstrumentationImpl
import java.lang.instrument.Instrumentation

abstract class AbstractNMS : MinecraftCross {

    abstract fun updateAttack(item: ItemStack, speed: Double): ItemStack

    abstract fun clearAttribute(item: ItemStack): ItemStack

    abstract fun getAllNBT(item: ItemStack): String

    abstract fun setNBT(item: ItemStack, key: String, value: Any): ItemStack

    abstract fun getNBT(item: ItemStack, key: String): Any?

    abstract fun hasNBT(item: ItemStack, key: String): Boolean

    abstract fun removeNBT(item: ItemStack, key: String): ItemStack

    abstract fun setUnbreakable(item: ItemStack, unbreakable: Boolean): ItemStack

    abstract fun setDurability(itemStack: ItemStack, value: Int): ItemStack

    abstract fun getDurability(item: ItemStack): Short

    abstract fun isUnbreakable(item: ItemStack): Boolean

    abstract fun getInventoryItems(equipment: EntityEquipment): MutableList<ComposeItem>

    abstract fun getInventoryItemBySlot(equipment: EntityEquipment, slot: ComposeItem.Slot): ItemStack?

    abstract fun getInventoryComposeItemBySlot(equipment: EntityEquipment, slot: ComposeItem.Slot): ComposeItem?

    abstract fun valueOfMaterial(id: String): Material?

    fun service(name: String): Class<*> = NMSComponent.getService(name)!!

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val chatComponentText = Class.forName("net.minecraft.server.v1_12_R1.ChatComponentText")
            val chatMessageType = Class.forName("net.minecraft.server.v1_12_R1.ChatMessageType")
            val constructor = Reflect.getConstructor(
                Class.forName("net.minecraft.server.v1_12_R1.PacketPlayOutChat"), arrayOf(
                    Class.forName("net.minecraft.server.v1_12_R1.IChatBaseComponent").apply { print("base=$this") },
                    chatMessageType
                )
            )
            val newInstance = constructor.newInstance(
                chatComponentText.getConstructor(String::class.java).newInstance("aaa"),
                Reflect.getField(chatMessageType, "GAME_INFO").get("")
            )
            val instrumentationImpl = InstrumentationImpl()
            print(newInstance)

        }
    }
}