package com.tests.commercial.chatapp.content

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.tests.commercial.chatapp.R
import com.tests.commercial.chatapp.model.User
import timber.log.Timber

class MessageActivity : AppCompatActivity() {

    private lateinit var mFirebaseUser: FirebaseUser
    private lateinit var mDbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val intent = intent
        val userId = intent.getStringExtra("userId") ?: ""

        mFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        mDbReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        mDbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    toolbar.title = user.userName
                }
            }

            override fun onCancelled(dbError: DatabaseError) {
                Timber.e(dbError.message + ", code: " + dbError.code)
            }
        })
    }
}