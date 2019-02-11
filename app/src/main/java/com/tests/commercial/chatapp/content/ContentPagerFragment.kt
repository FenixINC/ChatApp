package com.tests.commercial.chatapp.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.tests.commercial.chatapp.PageAdapter
import com.tests.commercial.chatapp.databinding.FragmentPagerContentBinding

class ContentPagerFragment : Fragment() {

    private lateinit var mBinding: FragmentPagerContentBinding

    companion object {
        fun newInstance(): ContentPagerFragment {
            return ContentPagerFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentPagerContentBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        activity?.title = ""

        val pageAdapter = PageAdapter(childFragmentManager)
        pageAdapter
//            .add("All Users",)
            .add("Requests", RequestsFragment.newInstance())
            .add("All Users", UsersFragment.newInstance())
            .add("Chats", ChatsFragment.newInstance())

        mBinding.viewPager.adapter = pageAdapter
        mBinding.viewPager.offscreenPageLimit = 3
        mBinding.tabLayout.tabMode = TabLayout.MODE_FIXED
        mBinding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager)
    }
}