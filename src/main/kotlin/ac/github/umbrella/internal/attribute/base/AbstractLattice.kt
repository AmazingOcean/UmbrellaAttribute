package ac.github.umbrella.internal.attribute.base

import ac.github.umbrella.internal.attribute.Cabinet
import ac.github.umbrella.internal.attribute.lattice.InventoryLattice
import org.bukkit.entity.LivingEntity

abstract class AbstractLattice {

    companion object {
        val lattices = arrayListOf<AbstractLattice>()

        fun getLatticeByClass(clazz: Class<out AbstractLattice>): AbstractLattice? {
            return lattices.firstOrNull { it::class.java == clazz }
        }

        init {
            lattices.add(InventoryLattice())
        }
    }

    abstract fun extract(livingEntity: LivingEntity) : Cabinet

}