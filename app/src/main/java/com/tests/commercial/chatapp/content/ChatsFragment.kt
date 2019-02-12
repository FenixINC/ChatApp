package com.tests.commercial.chatapp.content

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.tests.commercial.chatapp.databinding.FragmentChatsBinding
import com.tests.commercial.chatapp.model.Chat
import com.tests.commercial.chatapp.model.User
import timber.log.Timber

class ChatsFragment : Fragment(), OnUserListener {

    private lateinit var mBinding: FragmentChatsBinding
    private lateinit var mAdapter: UsersAdapter

    private lateinit var mDbReference: DatabaseReference
    private lateinit var mFirebaseUser: FirebaseUser

    companion object {
        fun newInstance(): ChatsFragment {
            return ChatsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentChatsBinding.inflate(inflater, container, false)
        mAdapter = UsersAdapter(this, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv: RecyclerView = mBinding.recyclerView
        rv.layoutManager = LinearLayoutManager(context)
        rv.setHasFixedSize(true)
        rv.adapter = mAdapter

        loadUsers()
    }

    override fun onUserClick(user: User) {
        val intent = Intent(context, MessageActivity::class.java)
        intent.putExtra("userId", user.id)
        startActivity(intent)
    }

    private fun loadUsers() {
        val userList = ArrayList<String>()
        mFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        mDbReference = FirebaseDatabase.getInstance().getReference("Chats")
        mDbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat != null && chat.userSender == mFirebaseUser.uid) {
                        userList.add(chat.userReceiver)
                    }
                    if (chat != null && chat.userReceiver == mFirebaseUser.uid) {
                        userList.add(chat.userSender)
                    }
                }

                loadChats(userList)
            }

            override fun onCancelled(dbError: DatabaseError) {
                Timber.e(dbError.message + ", code: " + dbError.code)
            }
        })
    }

    private fun loadChats(list: List<String>) {
        val userList = ArrayList<User>()

        mDbReference = FirebaseDatabase.getInstance().getReference("Users")
        mDbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        for (id in list) {
                            if (user.id == id) {
                                if (userList.size != 0) {
                                    for (user1 in userList) {
                                        if (user.id != user1.id) {
                                            userList.add(user)
                                        }
                                    }
                                } else {
                                    userList.add(user)
                                }
                            }
                        }
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