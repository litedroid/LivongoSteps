package com.litedroidstudios.livongosteps

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import timber.log.Timber

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application
    private val stepsRepository = StepsRepository()
    private val stepSummaries = stepsRepository.stepSummaries
    private val sortedSummaries = MediatorLiveData<List<StepSummary>>()
    private var sortAscending = true

    init {
        sortedSummaries.addSource(stepSummaries) { list: List<StepSummary>? ->
            list?.let {
                sortedSummaries.value = sortSummaries(list)
            }
        }
    }

    fun getSortedStepsSummaries(): LiveData<List<StepSummary>> {
        return sortedSummaries
    }

    fun setSortedAscending(): Boolean {
        return sortAscending
    }

    private fun sortSummaries(list: List<StepSummary>): List<StepSummary>? {
        return if (sortAscending) {
            list.sortedBy { it.date }
        } else {
            list.sortedByDescending { it.date }
        }
    }

    fun reverseOrder() {
        sortAscending = !sortAscending
        stepSummaries.value?.let { list ->
            Timber.d("Reversed sort set ascending: $sortAscending")
            sortedSummaries.value = sortSummaries(list)
        }
    }

    fun refreshSteps() {
        stepsRepository.refreshSteps(app, NUM_DAYS)
    }
}