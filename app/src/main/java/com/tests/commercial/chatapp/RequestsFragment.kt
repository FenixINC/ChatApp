package com.tests.commercial.chatapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tests.commercial.chatapp.databinding.FragmentRequestsBinding

class RequestsFragment : Fragment() {

    private lateinit var mBinding: FragmentRequestsBinding

    companion object {
        fun newInstance(): RequestsFragment {
            return RequestsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentRequestsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}