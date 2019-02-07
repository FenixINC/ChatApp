package com.tests.commercial.chatapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mBtnLogin: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mReference: DatabaseReference

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)

        mBtnLogin = findViewById(R.id.btn_register)

        mAuth = FirebaseAuth.getInstance()

        mBtnLogin.setOnClickListener {
            val email = mEmail.text.toString()
            val password = mPassword.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this@LoginActivity, "All fields are required!", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(this@LoginActivity, "Password must be at least 6 characters.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                register(email, password)
            }
        }
    }

    private fun register(email: String, password: String) {

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = mAuth.currentUser!!
                    val userId = firebaseUser.uid

                    mReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val hashMap = HashMap<String, String>()
                    hashMap["id"] = userId
                    hashMap["imageURL"] = "default"
                    hashMap["status"] = "offline"

                    mReference.setValue(hashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "You can't register with this email or password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}