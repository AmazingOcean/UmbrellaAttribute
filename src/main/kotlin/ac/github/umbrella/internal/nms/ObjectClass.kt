package ac.github.umbrella.internal.nms

import ac.github.umbrella.util.Reflect
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

open class ObjectClass constructor(val clazz: Class<*>) {


    fun constructor(args: Array<Class<*>>): Constructor<*> = Reflect.getConstructor(clazz, args)


    fun newInstance(args: Array<Any>): Any? {
        return if (args.isEmpty()) clazz.newInstance() else clazz.cast(
            constructor(Reflect.covetClassTypes(args)).newInstance(*args)
        )
    }

    fun method(name: String, args: Array<Class<*>>): Method = Reflect.getMethod(clazz, name, args);

    fun invokeMethod(obj: Any, method: Method, args: Array<Any>): Any {
        method.isAccessible = true
        val returnType = method.returnType
        when (returnType) {
            Void.TYPE -> {
                if (args.isNotEmpty()) {
                    method.invoke(obj, *args)
                } else {
                    method.invoke(obj)
                }
                return obj
            }
            else ->
                return if (args.isNotEmpty()) {
                    method.invoke(obj, *args)
                } else {
                    method.invoke(obj)
                }

        }
    }

    fun invokeMethod(obj: Any, name: String, args: Array<Any>): Any {
        return invokeMethod(obj, method(name, Reflect.covetClassTypes(args)), args)
    }

    fun <T> invokeMethod(obj: Any, name: String, args: Array<Any>, to: Class<T>): T {
        return invokeMethod(obj, method(name, Reflect.covetClassTypes(args)), args, to)
    }

    fun <T> invokeMethod(obj: Any, method: Method, args: Array<Any>, to: Class<T>): T {
        val invokeMethod = invokeMethod(obj, method, args)
        return to.cast(invokeMethod)
    }

    fun field(name: String): Field = Reflect.getField(clazz, name)

    fun fieldValue(obj: Any, name: String): Any = fieldValue(obj, field(name))

    fun fieldValue(obj: Any, field: Field): Any {
        field.isAccessible = true;
        return field.get(obj)
    }

    fun <T> fieldValue(obj: Any, field: Field, clazz: Class<T>): T = clazz.cast(fieldValue(obj, field, clazz))

}