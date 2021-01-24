package ac.github.umbrella.internal.nms.impl

import ac.github.umbrella.internal.nms.NMSComponent
import ac.github.umbrella.internal.nms.bukkit.*
import ac.github.umbrella.internal.nms.obc
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.EquipmentSlot

import java.util.UUID
import java.util.function.Consumer


open class NMS_v1_13_R1 : NMSBase() {


    init {
        NMSComponent.registerService(AttributeModifier::class.java)
        NMSComponent.registerService(Class.forName("org.bukkit.inventory.meta.Damageable"))
    }

    override fun updateAttack(item: ItemStack, speed: Double): ItemStack {


        if (item.type.name !== "AIR") {
            val itemMeta = item.itemMeta!!
            val obc = itemMeta.javaClass.obc()
            obc.invokeMethod(itemMeta,"removeAttributeModifier", arrayOf(Attribute.GENERIC_ATTACK_SPEED))

            val v: Double = if (BukkitAdapter.hasItem(item)) BukkitAdapter.getBukkitItemAttribute(item).getDouble(
                BukkitItemAttributeType.ATTACK_SPEED.name) else 0.0
            val speedModifier = service("AttributeModifier").obc().newInstance(arrayOf(
                UUID.randomUUID(),
                "SX-Attack-Speed",
                speed - v * 2,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HAND
            ))!!

            obc.invokeMethod(itemMeta, "addAttributeModifier", arrayOf(Attribute.GENERIC_ATTACK_SPEED, speedModifier))
            item.itemMeta = itemMeta
        }
        return item;
    }

    override fun clearAttribute(item: ItemStack): ItemStack {

        if (BukkitAdapter.hasItem(item)) {
            val itemMeta = item.itemMeta
            val obc = itemMeta.javaClass.obc()
            val bukkitItem: BukkitItem = BukkitAdapter.getBukkitItem(item)!!
            bukkitItem.attributes.forEach(Consumer { bukkitItemAttribute: BukkitItemAttribute ->
                for ((key, value1) in bukkitItemAttribute) {
                    try {
                        val type = BukkitItemAttributeType.valueOf(key!!)
                        val attribute = type.getAttribute()
                        var modifier: AttributeModifier
                        var value = value1.toString().toDouble()
                        value = if (type == BukkitItemAttributeType.ATTACK_SPEED) {
                            -value
                        } else {
                            0.0
                        }

                        modifier = service("AttributeModifier").obc().newInstance(arrayOf(
                            UUID.randomUUID(),
                            "SXAttribute-Modifier | " + type.name,
                            value,
                            AttributeModifier.Operation.ADD_NUMBER,
                            bukkitItemAttribute.slot
                        )) as AttributeModifier
                        obc.invokeMethod(itemMeta, "addAttributeModifier", arrayOf(Attribute.GENERIC_ATTACK_SPEED, modifier))

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
            item.itemMeta = itemMeta
        }
        return item;
    }

    override fun setUnbreakable(item: ItemStack, unbreakable: Boolean): ItemStack {

        val itemMeta = item.itemMeta
        val obc = itemMeta.javaClass.obc()
        val simpleName = itemMeta.javaClass.simpleName

        if (simpleName == "CraftMetaItem") {

            obc.invokeMethod(itemMeta, "setUnbreakable", arrayOf(unbreakable))
        } else {
            val superclass: Class<*> = itemMeta.javaClass.superclass
            superclass.javaClass.obc().invokeMethod(itemMeta, "setUnbreakable", arrayOf(unbreakable))
        }
        item.itemMeta = itemMeta
        return item;
    }

    override fun setDurability(itemStack: ItemStack, value: Int): ItemStack {
        val itemMeta = itemStack.itemMeta;
        val metaObc = itemMeta.javaClass.obc()
        metaObc.invokeMethod(itemMeta, "setDamage", arrayOf(value))
        itemStack.itemMeta = itemMeta;
        return itemStack;
    }

    override fun getDurability(item: ItemStack): Short {
        val objectClass = service("Damageable").obc()
        return objectClass.fieldValue(item.itemMeta,"getDamage") as Short
    }
}

