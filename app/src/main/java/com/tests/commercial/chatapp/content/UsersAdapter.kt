package com.tests.commercial.chatapp.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tests.commercial.chatapp.BR
import com.tests.commercial.chatapp.R
import com.tests.commercial.chatapp.model.User

class UsersAdapter(listener: OnUserListener) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder: ViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.item_user, parent, false)
        return ViewHolder(holder)
    }

    override fun onBindViewHolder(holder: UsersAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position), mListener)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}