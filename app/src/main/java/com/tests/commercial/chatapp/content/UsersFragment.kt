package com.tests.commercial.chatapp.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.tests.commercial.chatapp.BR
import com.tests.commercial.chatapp.R
import com.tests.commercial.chatapp.databinding.FragmentUsersBinding
import com.tests.commercial.chatapp.model.User
import timber.log.Timber


class UsersFragment : Fragment(), OnUserListener {

    private lateinit var mBinding: FragmentUsersBinding
    private lateinit var mAdapter: ContentAdapter

    private lateinit var mDbReference: DatabaseReference
    private lateinit var mFirebaseUser: FirebaseUser

    private var mList = ArrayList<User>()

    companion object {
        fun newInstance(): UsersFragment {
            return UsersFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentUsersBinding.inflate(inflater, container, false)
        mAdapter = ContentAdapter(this)
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

    }

    private fun loadUsers() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val userList = ArrayList<User>()
            mFirebaseUser = FirebaseAuth.getInstance().currentUser!!
            mDbReference = FirebaseDatabase.getInstance().reference.child("Users")

            mDbReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
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

    private class ContentAdapter(listener: OnUserListener) : RecyclerView.Adapter<ContentAdapter.ViewHolder>() {
        private var mList: ArrayList<User> = ArrayList()
        private val mListener: OnUserListener = listener

        fun setList(list: List<User>) {
            mList.clear()
            mList.addAll(list)
            notifyDataSetChanged()
        }

        fun getItem(position: Int): User {
            return mList[position]
        }

        class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(data: Any, listener: OnUserListener) {
                binding.setVariable(BR.model, data)
                binding.setVariable(BR.clickListener, listener)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentAdapter.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val holder: ViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.item_user, parent, false)
            return ViewHolder(holder)
        }

        override fun onBindViewHolder(holder: ContentAdapter.ViewHolder, position: Int) {
            holder.bind(getItem(position), mListener)
        }

        override fun getItemCount(): Int {
            return mList.size
        }
    }
}