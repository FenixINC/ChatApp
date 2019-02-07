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


class RegisterActivity : AppCompatActivity() {

    private lateinit var mUsername: EditText
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mBtnRegister: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mUsername = findViewById(R.id.username)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)

        mBtnRegister = findViewById(R.id.btn_register)

        mAuth = FirebaseAuth.getInstance()

        mBtnRegister.setOnClickListener {
            val username = mUsername.text.toString()
            val email = mEmail.text.toString()
            val password = mPassword.text.toString()

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this@RegisterActivity, "All fields are required!", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(this@RegisterActivity, "Password must be at least 6 characters.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                register(username, email, password)
            }
        }
    }

    private fun register(username: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = mAuth.currentUser!!
                    val userId = firebaseUser.uid

                    mReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val hashMap = HashMap<String, String>()
                    hashMap["id"] = userId
                    hashMap["username"] = username
                    hashMap["imageURL"] = "default"
                    hashMap["status"] = "offline"
                    hashMap["search"] = username.toLowerCase()

                    mReference.setValue(hashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "You can't register with this email or password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}