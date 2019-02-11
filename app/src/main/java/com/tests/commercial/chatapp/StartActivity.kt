package com.tests.commercial.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.btn_login).setOnClickListener {
            startActivity(Intent(this@StartActivity, LoginActivity::class.java))
        }

        findViewById<Button>(R.id.btn_register).setOnClickListener {
            startActivity(Intent(this@StartActivity, RegisterActivity::class.java))
        }
    }
}