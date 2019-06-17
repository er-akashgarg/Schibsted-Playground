package com.akashgarg.schibsted.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    fun showToast(msg: String) {
        Toast.makeText(this@BaseActivity, msg, Toast.LENGTH_SHORT).show()
    }
}