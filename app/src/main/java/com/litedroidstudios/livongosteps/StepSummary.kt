package com.litedroidstudios.livongosteps

import java.text.SimpleDateFormat
import java.util.*

data class StepSummary(val date: Date, val steps: Int) {
    private val sdf = SimpleDateFormat(DATE_FORMAT)

    fun getDateFormatted() = sdf.format(date)
}