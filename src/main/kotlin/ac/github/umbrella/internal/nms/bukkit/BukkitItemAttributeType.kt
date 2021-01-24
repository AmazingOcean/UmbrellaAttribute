package ac.github.umbrella.internal.nms.bukkit

import org.bukkit.attribute.Attribute

enum class BukkitItemAttributeType(description: String) {

    ATTACK_SPEED("攻击速度"),
    ATTACK_DAMAGE("攻击力"),
    ARMOR("盔甲"),
    ARMOR_TOUGHNESS("盔甲韧性");



    fun getAttribute() : Attribute {
        return when (this){
            ATTACK_SPEED -> Attribute.GENERIC_ATTACK_SPEED;
            ATTACK_DAMAGE -> Attribute.GENERIC_ATTACK_DAMAGE;
            ARMOR -> Attribute.GENERIC_ARMOR;
            ARMOR_TOUGHNESS -> Attribute.GENERIC_ARMOR_TOUGHNESS;
        }
    }

    fun setBase(attribute : BukkitItemAttribute ,value : Any){
        attribute.add(this.name,value);
    }

}