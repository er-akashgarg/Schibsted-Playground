package com.akashgarg.schibsted.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import com.akashgarg.schibsted.R
import com.akashgarg.schibsted.app.MyApplication
import com.akashgarg.schibsted.model.ExchangeResponse
import com.akashgarg.schibsted.presenter.ExchangeHistoryPresenter
import com.akashgarg.schibsted.utils.NetworkUtils
import com.akashgarg.schibsted.view.base.BaseView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import javax.inject.Inject


class MainActivity : BaseActivity(), BaseView {

    var TAG = MainActivity::class.java.simpleName

    //injecting retrofit
    @Inject
    lateinit var retrofit: Retrofit

    var response: ExchangeResponse? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.akashgarg.schibsted.R.layout.activity_main)

        chart?.let {
            it.visibility = View.GONE
        }

        (application as MyApplication).netComponent.inject(this@MainActivity)

        if (NetworkUtils.isNetworkConnected(this@MainActivity)) {
            intent?.let {
                val date = intent.getStringExtra("date")
                tvNoInternet.visibility = View.GONE
                rl_view.visibility = View.VISIBLE
                ExchangeHistoryPresenter(retrofit, this@MainActivity, date)
            } ?: run {
                showToast("Date not Selected !")
            }

        } else {
            tvNoInternet.visibility = View.VISIBLE
            rl_view.visibility = View.GONE
            tvNoInternet.text = getString(R.string.no_internet_connection)
            showToast(getString(R.string.no_internet_connection))
        }
    }


    override fun showLoading() {
        progressBar?.let {
            it.visibility = View.VISIBLE
        }
    }

    override fun dismissLoading() {
        progressBar?.let {
            it.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun successResult(data: ExchangeResponse) {
        if (data.success) {
            response = data
            val rate = data.rates
            tvAmount.text = rate!!.uSD.toString()
            tvDate.text = data.date

            rate.let {
                lineChart(response)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        btnShowChart.setOnClickListener {
            chart?.let {
                it.visibility = View.VISIBLE
            }
            response?.let {
                lineChart(response)
            }
        }
    }

    private fun lineChart(resp: ExchangeResponse?) {
        val entries = mutableListOf<Entry>()
        entries.add(Entry(0F, 1F))
        entries.add(Entry(1F, resp!!.rates!!.uSD.toFloat()))
        entries.add(Entry(2F, 2F))

        val dataSet = LineDataSet(entries, "Customized values")
        dataSet.color = ContextCompat.getColor(this, R.color.colorPrimary)
        dataSet.valueTextColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        // Controlling X axis
        val xAxis = chart.xAxis

        // Set the xAxis position to bottom. Default is top
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        //Customizing x axis value
//        val dateStr = arrayOf("Jan", "Feb", "Mar", "Apr")
        val dateStr = arrayOf("", resp.date, "")
        val formatter = IAxisValueFormatter { value, axis -> dateStr[value.toInt()] }
        xAxis.granularity = 1f // minimum axis-step (interval) is 1
        xAxis.valueFormatter = formatter

        //***
        // Controlling right side of y axis
        val yAxisRight = chart.axisRight
        yAxisRight.isEnabled = false

        //***
        // Controlling left side of y axis
        val yAxisLeft = chart.axisLeft
        yAxisLeft.granularity = 1f

        // Setting Data
        val data = LineData(dataSet)
        chart.data = data
        chart.animateX(2500)
        //refresh
        chart.invalidate()
    }

    override fun failure(message: String?) {
        showToast(message!!)
        Log.e(TAG, "------message------: $message")
    }
}
