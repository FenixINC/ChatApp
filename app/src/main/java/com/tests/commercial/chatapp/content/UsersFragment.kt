package com.tests.commercial.chatapp.content

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.tests.commercial.chatapp.databinding.FragmentUsersBinding
import com.tests.commercial.chatapp.model.User
import timber.log.Timber

class UsersFragment : Fragment(), OnUserListener {

    private lateinit var mBinding: FragmentUsersBinding
    private lateinit var mAdapter: UsersAdapter

    private lateinit var mDbReference: DatabaseReference
    private lateinit var mFirebaseUser: FirebaseUser

    companion object {
        fun newInstance(): UsersFragment {
            return UsersFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentUsersBinding.inflate(inflater, container, false)
        mAdapter = UsersAdapter(this, true)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv: RecyclerView = mBinding.recyclerView
        rv.layoutManager = LinearLayoutManager(context)
        rv.setHasFixedSize(true)
        rv.adapter = mAdapter

        loadUsers()

        mBinding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                doSearch(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onUserClick(user: User) {
        val intent = Intent(context, MessageActivity::class.java)
        intent.putExtra("userId", user.id)
        startActivity(intent)
    }

    private fun loadUsers() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val userList = ArrayList<User>()

            mFirebaseUser = FirebaseAuth.getInstance().currentUser!!
            mDbReference = FirebaseDatabase.getInstance().reference.child("Users")
            mDbReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    userList.clear()
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null && user.id != mFirebaseUser.uid) {
                            userList.add(user)
                        }
                    }
                    mAdapter.setList(userList)
                }

                override fun onCancelled(dbError: DatabaseError) {
                    Timber.e(dbError.message + ", code: " + dbError.code)
                }
            })
        }
    }

    private fun doSearch(filter: String) {
        val userList = ArrayList<User>()
        mFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("userName")
            .startAt(filter)
            .endAt(filter + "\uf8ff")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null && user.id != mFirebaseUser.uid) {
                        userList.add(user)
                    }
                }
                mAdapter.setList(userList)
            }

            override fun onCancelled(dbError: DatabaseError) {
                Timber.e(dbError.message + ", code: " + dbError.code)
            }
        })
    }
}