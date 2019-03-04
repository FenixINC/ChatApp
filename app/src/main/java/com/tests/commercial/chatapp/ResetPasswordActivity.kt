package com.tests.commercial.chatapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        title = "Reset Password"

        mEmail = findViewById(R.id.email)
        mAuth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.btn_reset_password).setOnClickListener {
            val email = mEmail.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@ResetPasswordActivity, "Enter email!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.d("Successful password reset.")
                        startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Timber.e(task.exception)
                    }
                }
            }
        }
    }
}