package com.akashgarg.schibsted.ui.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.akashgarg.schibsted.R
import kotlinx.android.synthetic.main.select_date_activity.*
import java.text.SimpleDateFormat
import java.util.*


class SelectDateActivity : BaseActivity() {
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_date_activity)

        btnDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                this@SelectDateActivity,
                DatePickerDialog.OnDateSetListener { view, year, month, day ->
                    cal.set(year, month, day)
                    val format = SimpleDateFormat("YYYY-MM-dd")
                    val date = format.format(cal.time)
                    Log.e("#######", "------YYYY-MM-DD -----$date")
                    val intent = Intent(this@SelectDateActivity, MainActivity::class.java)
                    intent.putExtra("date", date)
                    startActivity(intent)
                },
                year,
                month,
                day
            )
            dpd.show()
        }
    }
}
