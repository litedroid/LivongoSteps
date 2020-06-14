package com.litedroidstudios.livongosteps

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.litedroidstudios.livongosteps.util.getAccount
import com.litedroidstudios.livongosteps.util.getFitnessOptions
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private var adapter: SummaryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        registerObservers()
        setupUI()
        checkForPermissions()
    }

    private fun setupUI() {
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        steps_list.layoutManager = layoutManager

        adapter = SummaryAdapter()
        steps_list.adapter = adapter

        sort_imageview?.setOnClickListener {
            viewModel.reverseOrder()
        }

        sort_reverse_button?.setOnClickListener {
            viewModel.reverseOrder()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                viewModel.refreshSteps()
            }
        }
    }

    private fun registerObservers() {
        viewModel.getSortedStepsSummaries().observe(this, Observer { summaries ->
            Timber.d("stepSummaries count: ${summaries.size}")
            adapter?.setList(summaries)

            if (summaries.isNotEmpty()) {
                steps_list.visibility = View.VISIBLE
                empty_results_textview?.visibility = View.GONE
            } else {
                steps_list.visibility = View.GONE
                empty_results_textview?.visibility = View.VISIBLE
            }

            if (viewModel.setSortedAscending()) {
                sort_imageview.setImageResource(R.drawable.ic_arrow_up_24)
            } else {
                sort_imageview.setImageResource(R.drawable.ic_arrow_down_24)
            }

            steps_list?.smoothScrollToPosition(0)
        })
    }

    private fun checkForPermissions() {
        val fitnessOptions = getFitnessOptions()
        val account = getAccount(this, fitnessOptions)

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this,
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                account,
                fitnessOptions
            );
        } else {
            viewModel.refreshSteps();
        }
    }
}