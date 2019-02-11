package com.tests.commercial.chatapp.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tests.commercial.chatapp.databinding.FragmentChatsBinding

class ChatsFragment : Fragment() {

    private lateinit var mBinding: FragmentChatsBinding

    companion object {
        fun newInstance(): ChatsFragment {
            return ChatsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentChatsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}