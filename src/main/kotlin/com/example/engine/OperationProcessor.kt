package com.example.engine

import com.example.entity.Event
import com.example.entity.Result
import com.example.port.input.EventOperationPortIn

class OperationProcessor(
) : EventOperationPortIn {

    // 중간 집계값을 받아 누적하거나 이벤트 이력으로 집계
    override fun operation(result: Result?, eventHistory: List<Event>, newEvent: Event): Result {
        return if (result != null) {
            // 중간 집계값이 있을 경우 누적
            Result(result.result + newEvent.value)
        } else {
            // 중간 집계값이 없으면 이벤트 이력의 갯수로 전체 집계
            Result(eventHistory.sumOf { it.value })
        }
    }
}