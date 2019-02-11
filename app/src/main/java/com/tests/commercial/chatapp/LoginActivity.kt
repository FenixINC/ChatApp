package com.tests.commercial.chatapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tests.commercial.chatapp.dialogs.ProgressDialog

class LoginActivity : AppCompatActivity() {

    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mBtnLogin: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseUser: FirebaseUser

    private val TAG_DIALOG_PROGRESS = "tag_dialog_progress"

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        title = "Login"

        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)
        mBtnLogin = findViewById(R.id.btn_register)
        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            mFirebaseUser = mAuth.currentUser!!
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

        mBtnLogin.setOnClickListener {
            val email = mEmail.text.toString()
            val password = mPassword.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this@LoginActivity, "All fields are required!", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(this@LoginActivity, "Password must be at least 6 characters.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                doLogin(email, password)
            }
        }
    }

    private fun doLogin(email: String, password: String) {
        ProgressDialog().newInstance("Please wait..").show(supportFragmentManager, TAG_DIALOG_PROGRESS)
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Failed authentication!", Toast.LENGTH_SHORT).show()
            }
            hideProgressDialog()
        }
    }

    private fun hideProgressDialog() {
        val fragment = supportFragmentManager.findFragmentByTag(TAG_DIALOG_PROGRESS)
        if (fragment != null && fragment is DialogFragment) {
            fragment.dismissAllowingStateLoss()
        }
    }
}