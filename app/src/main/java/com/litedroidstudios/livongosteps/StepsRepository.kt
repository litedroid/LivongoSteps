package com.litedroidstudios.livongosteps

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.Bucket
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.litedroidstudios.livongosteps.util.getAccount
import com.litedroidstudios.livongosteps.util.getFitnessOptions
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class StepsRepository() {
    val stepSummaries = MutableLiveData<List<StepSummary>>()

    fun refreshSteps(context: Context, numDays: Int) {
        Timber.d("refreshSteps for days: $numDays")

        val fitnessOptions = getFitnessOptions()
        val date = GregorianCalendar()
        date[Calendar.HOUR_OF_DAY] = 0
        date[Calendar.MINUTE] = 0
        date[Calendar.SECOND] = 0
        date[Calendar.MILLISECOND] = 0

        date.add(Calendar.DAY_OF_MONTH, 1)
        val endTime = date.timeInMillis
        date.add(Calendar.DATE, -numDays)
        val startTime = date.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .build()

        val account = getAccount(context, fitnessOptions)
        Fitness.getHistoryClient(context, account)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                Timber.d("getSteps success")
                val summaries = getStepCounts(response.buckets)
                Timber.d("getSteps stepSummaries count: ${summaries.size}")

                stepSummaries.value = summaries
            }
            .addOnFailureListener { e -> Timber.e(e, "getSteps failure -- ${e.message}") }
    }

    private fun getStepCounts(buckets: List<Bucket>): List<StepSummary> {
        val stepSummaries = mutableListOf<StepSummary>()

        for (bucket in buckets) {
            for (dataSet in bucket.dataSets) {
                for (dataPoint in dataSet.dataPoints) {
                    val start = dataPoint.getStartTime(TimeUnit.MILLISECONDS)
                    val date = Date(start)

                    for (field in dataPoint.dataType.fields) {
                        val value = dataPoint.getValue(field).asInt()
                        val stepSummary =
                            StepSummary(
                                date,
                                value
                            )
                        Timber.d("adding stepSummary:$stepSummary")
                        stepSummaries.add(stepSummary)
                    }
                }

            }
        }

        stepSummaries.sortBy { it.date }
        return stepSummaries
    }
}
