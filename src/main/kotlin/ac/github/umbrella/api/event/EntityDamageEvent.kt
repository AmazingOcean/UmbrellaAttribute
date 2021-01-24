package ac.github.umbrella.api.event

import ac.github.umbrella.internal.attribute.base.event.DamageEvent
import ac.github.umbrella.internal.event.EventAdapter

class EntityDamageEvent(
    var damageEvent: DamageEvent
) : EventAdapter()