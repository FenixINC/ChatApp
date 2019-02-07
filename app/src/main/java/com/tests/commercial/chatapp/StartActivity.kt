package com.tests.commercial.chatapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        findViewById<Button>(R.id.btn_login).setOnClickListener {
            startActivity(Intent(this@StartActivity, LoginActivity::class.java))
        }

        findViewById<Button>(R.id.btn_register).setOnClickListener {
            startActivity(Intent(this@StartActivity, RegisterActivity::class.java))
        }
    }
}