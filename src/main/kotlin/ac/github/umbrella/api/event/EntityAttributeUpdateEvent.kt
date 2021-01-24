package ac.github.umbrella.api.event

import ac.github.umbrella.internal.attribute.EntityIntegrate
import ac.github.umbrella.internal.event.EventAdapter
import org.bukkit.entity.LivingEntity

class EntityAttributeUpdateEvent(val livingEntity: LivingEntity, val entityIntegrate: EntityIntegrate) : EventAdapter()