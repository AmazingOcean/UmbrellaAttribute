package ac.github.umbrella.internal.attribute.base

import ac.github.umbrella.internal.attribute.base.event.EventCentral

abstract class AbstractAttributeEventProcessor {

    abstract fun executor(eventCentral: EventCentral)

}