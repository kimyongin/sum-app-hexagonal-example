package com.example.engine

import com.example.entity.Event
import com.example.port.inbound.EventFilterPortIn
import com.example.port.inbound.EventQueryPortIn
import com.example.port.inbound.EventSavePortIn
import com.example.port.outbound.EventQueryPortOut
import com.example.port.outbound.EventSavePortOut
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class EventProcessor(
    private val eventQueryPortOut: EventQueryPortOut,
    private val eventSavePortOut: EventSavePortOut,
) : EventFilterPortIn, EventQueryPortIn, EventSavePortIn {

    companion object {
        const val BUFFER_SIZE = 3
        const val BUFFER_FLUSH_DELAY_MS = 5000L // 5초
    }

    private val buffer = mutableListOf<Event>()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val deferredList = mutableListOf<CompletableDeferred<Unit>>()
    private var timerJob: Job? = null
    private val mutex = Mutex()

    override fun filter(event: Event): Boolean {
        return true
    }

    override suspend fun save(event: Event): Deferred<Unit> {
        val deferred = CompletableDeferred<Unit>()
        scope.launch {
            var shouldFlush = false
            mutex.withLock {
                buffer.add(event)
                deferredList.add(deferred)
                if (buffer.size >= BUFFER_SIZE) {
                    shouldFlush = true
                } else if (timerJob == null || !timerJob!!.isActive) {
                    startTimer()
                }
            }
            if (shouldFlush) {
                flushBuffer()
            }
        }
        return deferred
    }

    private suspend fun flushBuffer() {
        val eventsToSave: List<Event>
        val deferredsToComplete: List<CompletableDeferred<Unit>>

        // 잠금 안에서 데이터 복사 및 클리어
        mutex.withLock {
            eventsToSave = buffer.toList()
            deferredsToComplete = deferredList.toList()
            buffer.clear()
            deferredList.clear()
            timerJob?.cancel()
            timerJob = null
        }

        // 잠금 해제 후 비동기 작업 실행
        eventSavePortOut.saveBatch(eventsToSave)

        // 비동기 작업 완료 후 결과 처리
        deferredsToComplete.forEach { it.complete(Unit) }
    }

    private fun startTimer() {
        timerJob = scope.launch {
            delay(BUFFER_FLUSH_DELAY_MS)
            flushBuffer()
        }
    }

    override suspend fun query(id: String): List<Event> = coroutineScope {
        eventQueryPortOut.query(id)
    }
}
