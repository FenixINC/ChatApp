package com.tests.commercial.chatapp.content

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.tests.commercial.chatapp.R
import com.tests.commercial.chatapp.model.User
import timber.log.Timber

class MessageActivity : AppCompatActivity() {

    private lateinit var mToolbar: Toolbar

    private lateinit var mFirebaseUser: FirebaseUser
    private lateinit var mDbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val editTextMessage = findViewById<EditText>(R.id.message)
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mToolbar.setNavigationOnClickListener {
            finish()
        }

        val intent = intent
        val receiverUserId = intent.getStringExtra("userId") ?: ""

        mFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        mDbReference = FirebaseDatabase.getInstance().getReference("Users").child(receiverUserId)
        mDbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    mToolbar.title = user.userName
                }
            }

            override fun onCancelled(dbError: DatabaseError) {
                Timber.e(dbError.message + ", code: " + dbError.code)
            }
        })

        findViewById<ImageButton>(R.id.message_send).setOnClickListener {
            val message = editTextMessage.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(mFirebaseUser.uid, receiverUserId, message)
            }
            editTextMessage.setText("")
        }
    }

    private fun sendMessage(sender: String, receiver: String, message: String) {
        val messageHashMap = HashMap<String, String>()
        messageHashMap["userSender"] = sender
        messageHashMap["userReceiver"] = receiver
        messageHashMap["userMessage"] = message

        mDbReference = FirebaseDatabase.getInstance().reference
        mDbReference.child("Chats").push().setValue(messageHashMap)
    }
}