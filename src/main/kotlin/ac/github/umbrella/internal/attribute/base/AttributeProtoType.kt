package ac.github.umbrella.internal.attribute.base

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.lang.StringBuilder

class AttributeProtoType

fun <T> attributeArrayListOf(vararg elements : T) : ValueArray.AttributeArrayList<T> {
    val attributeArrayList = ValueArray.AttributeArrayList<T>()
    attributeArrayList.addAll(elements)
    return attributeArrayList
}
public fun ItemStack.legal() : Boolean {
    return type != Material.AIR && hasItemMeta() && itemMeta.hasLore()
}

fun String.ifReplaceCharIndex(start : Int,end : Int,ifString: String,value : String): String {
    val substring = this.substring(IntRange(start, end))
    if (substring == ifString) {
        val stringBuilder = StringBuilder()
        stringBuilder.append(this.substring(0,start))
        stringBuilder.append(value)
        stringBuilder.append(this.substring(end + 1,this.length))
        return stringBuilder.toString()
    }
    return this
}