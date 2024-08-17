package com.example.engine

import com.example.entity.EventResult
import com.example.port.inbound.EventQueryPort
import com.example.port.inbound.EventSumPort
import com.example.port.outbound.EventHistoryQueryPort
import com.example.port.outbound.InterimResultLoadPort
import com.example.port.outbound.EventHistorySavePort
import com.example.port.outbound.InterimResultSavePort

class EventProcessor(
    private val eventHistoryQueryPort: EventHistoryQueryPort,
    private val eventHistorySavePort: EventHistorySavePort,
    private val interimResultLoadPort: InterimResultLoadPort,
    private val interimResultSavePort: InterimResultSavePort
) : EventQueryPort, EventSumPort {

    // 중간 집계값을 받아 누적하거나 이벤트 이력으로 집계
    private fun processEvent(interimResult: Long?, eventHistory: List<Long>, newEvent: Long): Long {
        return if (interimResult != null) {
            // 중간 집계값이 있을 경우 누적
            interimResult + newEvent
        } else {
            // 중간 집계값이 없으면 이벤트 이력의 갯수로 전체 집계
            eventHistory.sum() + newEvent
        }
    }

    override fun sum(id: String, value: Long): EventResult {
        // 캐시에서 중간 집계값을 불러옴
        val interimResult = interimResultLoadPort.load(id)
        val eventHistory = if (interimResult == null) {
            // 중간 집계값이 없으면 디스크에서 이벤트 이력을 불러옴
            eventHistoryQueryPort.query(id)
        } else {
            // 중간 집계값이 있으면 이벤트 이력을 불러오지 않음
            emptyList()
        }
        // 이벤트를 처리하여 새로운 집계값을 계산
        val newCount = processEvent(interimResult, eventHistory, value)
        // 새로운 집계값을 캐시에 저장
        interimResultSavePort.save(id, newCount)
        // 이벤트를 저장
        eventHistorySavePort.save(id, value)
        // 새로운 집계값과 캐시 히트 여부 반환
        return EventResult(newCount)
    }

    override fun query(id: String): List<Long> {
        return eventHistoryQueryPort.query(id)
    }
}