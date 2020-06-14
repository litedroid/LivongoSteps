package com.litedroidstudios.livongosteps.util

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType

fun getFitnessOptions(): FitnessOptions {
    return FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .build()
}

fun getAccount(context: Context, fitnessOptions: FitnessOptions): GoogleSignInAccount {
    return GoogleSignIn.getAccountForExtension(context, fitnessOptions)
}