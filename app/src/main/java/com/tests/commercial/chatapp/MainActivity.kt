package com.tests.commercial.chatapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.tests.commercial.chatapp.content.ContentPagerFragment
import com.tests.commercial.chatapp.model.User
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbReference: DatabaseReference
    private lateinit var mFirebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser != null) {
            mFirebaseUser = mAuth.currentUser!!
            mDbReference = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser.uid)
            mDbReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: User = dataSnapshot.getValue(User::class.java)!!
                    toolbar.title = user.userName
                }

                override fun onCancelled(dbError: DatabaseError) {
                    Timber.e(dbError.message + ", code: " + dbError.code)
                }
            })
        } else {
            startActivity(Intent(this@MainActivity, StartActivity::class.java))
            finish()
        }

        supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.content, ContentPagerFragment.newInstance())
            ?.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        this@MainActivity.menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
//            R.id.action_all_users -> {
//                startActivity(Intent(this@MainActivity, UsersActivity::class.java))
//            }
            R.id.action_settings -> {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, StartActivity::class.java))
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setUserStatus(status: String) {
        val map = HashMap<String, Any>()
        map["userStatus"] = status
        mDbReference = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser.uid)
        mDbReference.updateChildren(map)
    }

    override fun onResume() {
        super.onResume()
        setUserStatus("online")
    }

    override fun onPause() {
        super.onPause()
        setUserStatus("offline")
    }
}