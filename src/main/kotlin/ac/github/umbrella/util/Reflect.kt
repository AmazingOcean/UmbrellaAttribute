package ac.github.umbrella.util

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

class Reflect {

    companion object {

        fun getMethod(clazz: Class<*>, name: String, args: Array<Class<*>>): Method {
            return if (args.isEmpty()) clazz.getDeclaredMethod(name) else clazz.getDeclaredMethod(name, *args)
        }

        fun getField(clazz: Class<*>, name: String): Field = clazz.getDeclaredField(name)

        fun covetClassType(any: Any): Class<*> = any::class.javaPrimitiveType ?: any::class.java

        fun covetClassTypes(array: Array<Any>): Array<Class<*>> {
            val arrayListOf = arrayListOf<Class<*>>()
            array.forEach { arrayListOf.add(covetClassType(it)) }
            return arrayListOf.toTypedArray();
        }

        fun getConstructor(clazz: Class<*>, args: Array<Class<*>>): Constructor<*> = clazz.getDeclaredConstructor(*args)
    }

}