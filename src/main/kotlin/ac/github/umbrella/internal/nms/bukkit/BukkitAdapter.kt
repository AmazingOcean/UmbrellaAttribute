package ac.github.umbrella.internal.nms.bukkit

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

import java.util.HashMap

class BukkitAdapter {

    companion object {

        private val bukkitItemMap = mutableMapOf<String, BukkitItem>()


        fun registry(materialName: String, attribute: BukkitItemAttribute) {
            try {
                val material: Material = Material.valueOf(materialName)
                bukkitItemMap[materialName.toUpperCase()] = BukkitItem.create(material, attribute)
            } catch (e: Exception) {
            }
        }

        fun registry(material: Material, attribute: BukkitItemAttribute) {
            registry(material.toString(), attribute)
        }

        fun hasItem(itemStack: ItemStack): Boolean {
            return hasMaterial(itemStack.getType().name)
        }

        fun hasMaterial(material: String): Boolean {
            return bukkitItemMap.containsKey(material)
        }

        fun getBukkitItem(itemStack: ItemStack): BukkitItem? {
            return getBukkitItem(itemStack.type.name)
        }

        fun getBukkitItem(material: String): BukkitItem? {
            return bukkitItemMap.getOrDefault(material.toUpperCase(), null)
        }

        fun getBukkitItemAttribute(itemStack: ItemStack): BukkitItemAttribute {
            return getBukkitItemAttribute(itemStack.getType().name)
        }

        fun getBukkitItemAttribute(material: String): BukkitItemAttribute {
            return getBukkitItem(material)?.attributes!![0]
        }



        init {
            //SWORD 剑
            registry(
                "IRON_SWORD",
                BukkitItemAttribute.instance(EquipmentSlot.HAND).setBase(BukkitItemAttributeType.ATTACK_SPEED, 1.6)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 6)
            )
            registry(
                "WOOD_SWORD",
                BukkitItemAttribute.instance(EquipmentSlot.HAND).setBase(BukkitItemAttributeType.ATTACK_SPEED, 1.6)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 4)
            )
            registry(
                "STONE_SWORD",
                BukkitItemAttribute.instance(EquipmentSlot.HAND).setBase(BukkitItemAttributeType.ATTACK_SPEED, 1.6)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 5)
            )
            registry(
                "DIAMOND_SWORD",
                BukkitItemAttribute.instance(EquipmentSlot.HAND).setBase(BukkitItemAttributeType.ATTACK_SPEED, 1.6)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 7)
            )
            registry(
                "GOLDEN_SWORD",
                BukkitItemAttribute.instance(EquipmentSlot.HAND).setBase(BukkitItemAttributeType.ATTACK_SPEED, 1.6)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 4)
            )

            //斧子
            registry(
                "IRON_AXE",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 0.9)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 9)
            )
            registry(
                "WOOD_AXE",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 0.8)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 7)
            )
            registry(
                "STONE_AXE",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 0.8)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 9)
            )
            registry(
                "DIAMOND_AXE",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 1)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 9)
            )
            registry(
                "GOLDEN_AXE",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 1)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 7)
            )
            //稿子
            registry(
                "IRON_PICKAXE",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 1.2)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 4)
            )
            registry(
                "WOOD_PICKAXE",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 1.2)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 2)
            )
            registry(
                "STONE_PICKAXE",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 1.2)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 3)
            )
            registry(
                "DIAMOND_PICKAXE",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 1.5)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 5)
            )
            registry(
                "GOLDEN_PICKAXE",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 1.5)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 2)
            )
            //锹
            registry(
                "IRON_SHOVEL",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 1)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 4.5)
            )
            registry(
                "WOOD_SHOVEL",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 1.2)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 2.5)
            )
            registry(
                "STONE_SHOVEL",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 1.2)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 3.5)
            )
            registry(
                "DIAMOND_SHOVEL",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 1.5)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 5.5)
            )
            registry(
                "GOLDEN_SHOVEL",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 1.5)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 2.5)
            )
            //锄
            registry(
                "IRON_HOE",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 1)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 1)
            )
            registry(
                "WOOD_HOE",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 2)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 1)
            )
            registry(
                "STONE_HOE",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 3)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 1)
            )
            registry(
                "DIAMOND_HOE",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 4)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 1)
            )
            registry(
                "GOLDEN_HOE",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 1)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 1)
            )

            //三叉戟
            registry(
                "TRIDENT",
                BukkitItemAttribute.instance().setBase(BukkitItemAttributeType.ATTACK_SPEED, 1.1)
                    .setBase(BukkitItemAttributeType.ATTACK_DAMAGE, 9)
            )

            //头盔
            registry(
                "LEATHER_HELMET",
                BukkitItemAttribute.instance(EquipmentSlot.HEAD).setBase(BukkitItemAttributeType.ARMOR, 1)
            )
            registry(
                "CHAINMAIL_HELMET",
                BukkitItemAttribute.instance(EquipmentSlot.HEAD).setBase(BukkitItemAttributeType.ARMOR, 2)
            )
            registry(
                "IRON_HELMET",
                BukkitItemAttribute.instance(EquipmentSlot.HEAD).setBase(BukkitItemAttributeType.ARMOR, 2)
            )
            registry(
                "DIAMOND_HELMET",
                BukkitItemAttribute.instance(EquipmentSlot.HEAD).setBase(BukkitItemAttributeType.ARMOR, 3)
                    .setBase(BukkitItemAttributeType.ARMOR_TOUGHNESS, 2)
            )
            registry(
                "GOLDEN_HELMET",
                BukkitItemAttribute.instance(EquipmentSlot.HEAD).setBase(BukkitItemAttributeType.ARMOR, 2)
            )
            //防具
            registry(
                "LEATHER_CHESTPLATE",
                BukkitItemAttribute.instance(EquipmentSlot.CHEST).setBase(BukkitItemAttributeType.ARMOR, 3)
            )
            registry(
                "CHAINMAIL_CHESTPLATE",
                BukkitItemAttribute.instance(EquipmentSlot.CHEST).setBase(BukkitItemAttributeType.ARMOR, 5)
            )
            registry(
                "IRON_CHESTPLATE",
                BukkitItemAttribute.instance(EquipmentSlot.CHEST).setBase(BukkitItemAttributeType.ARMOR, 6)
            )
            registry(
                "DIAMOND_CHESTPLATE",
                BukkitItemAttribute.instance(EquipmentSlot.CHEST).setBase(BukkitItemAttributeType.ARMOR, 8)
                    .setBase(BukkitItemAttributeType.ARMOR_TOUGHNESS, 2)
            )
            registry(
                "GOLDEN_CHESTPLATE",
                BukkitItemAttribute.instance(EquipmentSlot.CHEST).setBase(BukkitItemAttributeType.ARMOR, 5)
            )
            //护腿
            registry(
                "LEATHER_LEGGINGS",
                BukkitItemAttribute.instance(EquipmentSlot.LEGS).setBase(BukkitItemAttributeType.ARMOR, 2)
            )
            registry(
                "CHAINMAIL_LEGGINGS",
                BukkitItemAttribute.instance(EquipmentSlot.LEGS).setBase(BukkitItemAttributeType.ARMOR, 4)
            )
            registry(
                "IRON_LEGGINGS",
                BukkitItemAttribute.instance(EquipmentSlot.LEGS).setBase(BukkitItemAttributeType.ARMOR, 5)
            )
            registry(
                "DIAMOND_LEGGINGS",
                BukkitItemAttribute.instance(EquipmentSlot.LEGS).setBase(BukkitItemAttributeType.ARMOR, 6)
                    .setBase(BukkitItemAttributeType.ARMOR_TOUGHNESS, 2)
            )
            registry(
                "GOLDEN_LEGGINGS",
                BukkitItemAttribute.instance(EquipmentSlot.LEGS).setBase(BukkitItemAttributeType.ARMOR, 3)
            )
            //鞋子
            registry(
                "LEATHER_BOOTS",
                BukkitItemAttribute.instance(EquipmentSlot.FEET).setBase(BukkitItemAttributeType.ARMOR, 1)
            )
            registry(
                "CHAINMAIL_BOOTS",
                BukkitItemAttribute.instance(EquipmentSlot.FEET).setBase(BukkitItemAttributeType.ARMOR, 1)
            )
            registry(
                "IRON_BOOTS",
                BukkitItemAttribute.instance(EquipmentSlot.FEET).setBase(BukkitItemAttributeType.ARMOR, 2)
            )
            registry(
                "DIAMOND_BOOTS",
                BukkitItemAttribute.instance(EquipmentSlot.FEET).setBase(BukkitItemAttributeType.ARMOR, 3)
                    .setBase(BukkitItemAttributeType.ARMOR_TOUGHNESS, 2)
            )
            registry(
                "GOLDEN_BOOTS",
                BukkitItemAttribute.instance(EquipmentSlot.FEET).setBase(BukkitItemAttributeType.ARMOR, 3)
            )
        }
    }

}