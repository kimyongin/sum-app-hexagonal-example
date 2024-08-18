package com.example.engine

import com.example.entity.Event
import com.example.port.inbound.EventFilterPort
import com.example.port.inbound.EventQueryPort
import com.example.port.inbound.EventSavePort
import com.example.port.outbound.EventHistoryQueryPort
import com.example.port.outbound.EventHistorySavePort

class EventProcessor(
    private val eventHistoryQueryPort: EventHistoryQueryPort,
    private val eventHistorySavePort: EventHistorySavePort,
) : EventFilterPort, EventQueryPort, EventSavePort {


    override fun filter(event: Event): Boolean {
        return true
    }

    override fun save(event: Event) {
        eventHistorySavePort.save(event)
    }

    override fun query(id: String): List<Event> {
        return eventHistoryQueryPort.query(id)
    }
}