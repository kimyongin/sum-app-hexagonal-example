package com.example.engine

import com.example.entity.Result
import com.example.port.input.ResultLoadPortIn
import com.example.port.input.ResultSavePortIn
import com.example.port.output.ResultLoadPortOut
import com.example.port.output.ResultSavePortOut

class ResultProcessor (
    private val resultLoadPortOut: ResultLoadPortOut,
    private val resultSavePortOut: ResultSavePortOut
): ResultLoadPortIn, ResultSavePortIn{
    override fun load(id: String): Result? {
        return resultLoadPortOut.load(id)
    }

    override fun save(id: String, result: Result) {
        resultSavePortOut.save(id, result)
    }
}