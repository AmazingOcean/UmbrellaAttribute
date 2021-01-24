package ac.github.umbrella.internal.attribute.base.annotation

import ac.github.umbrella.internal.attribute.base.AttributeType
import ac.github.umbrella.internal.attribute.base.ValueEnum
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AttributeMeta(
    val plugin : KClass<out JavaPlugin>,
    val types : Array<AttributeType>,
    val valueType : ValueEnum = ValueEnum.Number
)
