package com.akashgarg.schibsted.ui.activity

import android.content.Intent
import android.os.Bundle
import com.akashgarg.schibsted.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        GlobalScope.launch {
            delay(2000)
            startActivity(Intent(this@SplashActivity, SelectDateActivity::class.java))
            finishAffinity()
        }
    }
}