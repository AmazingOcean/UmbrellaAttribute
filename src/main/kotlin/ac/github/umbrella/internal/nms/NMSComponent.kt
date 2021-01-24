package ac.github.umbrella.internal.nms

import ac.github.umbrella.internal.nms.impl.*
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

fun Class<*>.obc(): ObjectClass {

    if (NMSComponent.obcCache.containsKey(this)) {
        return NMSComponent.obcCache[this]!!
    } else {
        val objectClass = ObjectClass(this)
        NMSComponent.obcCache[this] = objectClass
        return objectClass;
    }
}

fun Class<*>.actualTypeArguments() : Array<Type> {
    val type = this.genericSuperclass as ParameterizedType
    return type.actualTypeArguments
}

fun Class<*>.actualTypeArgument(index : Int) : Class<*> {
    return actualTypeArguments()[index] as Class<*>
}

class NMSComponent {

    companion object {
        var services = mutableMapOf<String, Class<*>>()
        var obcCache = mutableMapOf<Class<*>, ObjectClass>()

        var nmsImpls = arrayListOf<Class<out AbstractNMS>>()
        private var nmsImpl: AbstractNMS? = null

        init {
            loadDefaultCraftNMSClass()
            nmsImpls.add(NMS_v1_8_R3::class.java)
            nmsImpls.add(NMS_v1_12_R1::class.java)
            nmsImpls.add(NMS_v1_13_R1::class.java)
            nmsImpls.add(NMS_v1_14_R1::class.java)
            nmsImpls.add(NMS_v1_15_R1::class.java)
            nmsImpls.add(NMS_v1_16_R1::class.java)

        }

        fun impl(): AbstractNMS {
            if (nmsImpl == null) {
                nmsImpls.forEach {
                    if (it.simpleName == "NMS_${version()}") {
                        nmsImpl = it.newInstance()
                        return@forEach
                    }
                }
            }
            return nmsImpl!!
        }

        fun registerServices(array: Array<Class<*>>) {
            array.forEach { registerService(it) }
        }

        fun getService(name: String): Class<*>? {
            return services[name]
        }

        fun registerService(clazz: Class<*>) {
            registerService(clazz.simpleName, clazz)
        }

        fun registerService(name: String, clazz: Class<*>) {

            if (!this.services.containsKey(name)) {
                this.services[name] = clazz
            }
        }

        fun getInstanceService(name: String): ObjectValueClass? {
            return getInstanceServiceByClass(getService(name))
        }

        fun getInstanceServiceByClass(clazz: Class<*>?): ObjectValueClass? {
            if (obcCache.containsKey(clazz)) {
                return obcCache[clazz] as ObjectValueClass
            }
            return null
        }

        fun registerService(instance: Any) {
            this.registerService(instance::class.java)
            obcCache[instance::class.java] = ObjectValueClass(instance)
        }

        fun loadDefaultCraftNMSClass() {
            registerServices(
                arrayOf(
                    getCraftClass("inventory.CraftItemStack"),
                    getNMSClass("ItemStack"),
                    getNMSClass("NBTTagCompound"),
                    getNMSClass("NBTTagString"),
                    getNMSClass("NBTTagDouble"),
                    getNMSClass("NBTTagInt"),
                    getNMSClass("NBTTagList"),
                    getNMSClass("NBTBase"),
                    getNMSClass("IChatBaseComponent"),
                    getNMSClass("ChatComponentText"),
                    getNMSClass("ChatMessageType"),
                    getNMSClass("PacketPlayOutChat"),
                    getNMSClass("Packet"),
                    ItemMeta::class.java,
                    ItemStack::class.java
                )
            )
        }

        fun getCraftClass(name: String): Class<*> {
            return Class.forName(getCraftClassName(name))
        }

        fun getCraftClassName(name: String): String {
            return "${Bukkit.getServer().javaClass.getPackage().name}.${name}"
        }

        fun getNMSClass(name: String): Class<*> {
            return Class.forName(getNMSClassName(name))
        }

        fun getNMSClassName(name: String): String {
            return "net.minecraft.server.${version()}.${name}"
        }

        fun version(): String {
            val packet = Bukkit.getServer().javaClass.getPackage().name
            return packet.substring(packet.lastIndexOf('.') + 1)
        }

    }

}

