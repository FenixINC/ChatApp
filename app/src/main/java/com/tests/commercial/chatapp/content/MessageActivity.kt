package com.tests.commercial.chatapp.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.tests.commercial.chatapp.BR
import com.tests.commercial.chatapp.R
import com.tests.commercial.chatapp.databinding.ItemChatLeftBinding
import com.tests.commercial.chatapp.databinding.ItemChatRightBinding
import com.tests.commercial.chatapp.model.Chat
import com.tests.commercial.chatapp.model.User
import timber.log.Timber

class MessageActivity : AppCompatActivity(), OnUserListener {
    private lateinit var mToolbar: Toolbar

    private lateinit var mFirebaseUser: FirebaseUser
    private lateinit var mDbReference: DatabaseReference
    private lateinit var mAdapter: ChatMessageAdapter

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

        mAdapter = ChatMessageAdapter(this)
        val rv: RecyclerView = findViewById(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(this@MessageActivity)
        layoutManager.stackFromEnd = true
        rv.layoutManager = layoutManager
        rv.setHasFixedSize(true)
        rv.adapter = mAdapter

        mFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        mDbReference = FirebaseDatabase.getInstance().getReference("Users").child(receiverUserId)
        mDbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    mToolbar.title = user.userName
                    loadMessages(mFirebaseUser.uid, user.id, user.userPhoto)
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

    private fun loadMessages(myUid: String, userId: String, imageUrl: String) {
        val chatList = ArrayList<Chat>()
        mDbReference = FirebaseDatabase.getInstance().getReference("Chats")
        mDbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatList.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat != null && (chat.userReceiver == myUid && chat.userSender == userId
                                || chat.userReceiver == userId && chat.userSender == myUid)
                    ) {
                        chatList.add(chat)
                    }
                    mAdapter.setList(chatList)
                }
            }

            override fun onCancelled(dbError: DatabaseError) {
                Timber.e(dbError.message + ", code: " + dbError.code)
            }
        })
    }

    override fun onUserClick(user: User) {

    }

    private class ChatMessageAdapter(listener: OnUserListener) : RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>() {
        private val mListener: OnUserListener = listener
        private var mList: ArrayList<Chat> = ArrayList()
        private lateinit var mFirebaseUser: FirebaseUser

        fun setList(list: List<Chat>) {
            mList.clear()
            mList.addAll(list)
            notifyDataSetChanged()
        }

        fun getItem(position: Int): Chat {
            return mList[position]
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageAdapter.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)

            return when (viewType) {
                R.layout.item_chat_left -> ViewHolder(ItemChatLeftBinding.inflate(inflater, parent, false))
                else -> ViewHolder(ItemChatRightBinding.inflate(inflater, parent, false))
            }
//            val holder: ViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.item_user, parent, false)
//            return ViewHolder(holder)
        }

        override fun onBindViewHolder(holder: ChatMessageAdapter.ViewHolder, position: Int) {
            holder.bind(getItem(position), mListener)
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        open inner class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
            open fun bind(data: Any, listener: OnUserListener) {
                binding.setVariable(BR.model, data)
                binding.setVariable(BR.clickListener, listener)
            }
        }

        override fun getItemViewType(position: Int): Int {
            mFirebaseUser = FirebaseAuth.getInstance().currentUser!!
            return when {
                mList[position].userSender == mFirebaseUser.uid -> R.layout.item_chat_right
                else -> R.layout.item_chat_left
            }
        }

//        inner class ChatMessageLeftHolder(private val binding: ItemChatLeftBinding) : ViewHolder(binding) {
//            override fun bind(data: Any, listener: OnUserListener) {
//                super.bind(data, listener)
//            }
//        }
//
//        inner class ChatMessageRightHolder(private val binding: ItemChatRightBinding) : ViewHolder(binding) {
//            override fun bind(data: Any, listener: OnUserListener) {
//                super.bind(data, listener)
//            }
//        }
    }
}