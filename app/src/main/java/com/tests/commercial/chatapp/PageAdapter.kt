package com.tests.commercial.chatapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.ArrayList

class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val mFragments = ArrayList<Fragment>()
    private val mTitles = ArrayList<String>()
    private val mTags = ArrayList<String>()

    fun add(title: String, fragment: Fragment): PageAdapter {
        mTitles.add(title)
        mFragments.add(fragment)
        return this
    }

    fun add(title: String, fragment: Fragment, tag: String): PageAdapter {
        add(title, fragment)
        mTags.add(tag)
        return this
    }

    fun getTag(position: Int): String {
        return if (position < count || position >= count) {
            ""
        } else mTags[position]
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitles[position]
    }

    override fun getCount(): Int {
        return mFragments.size
    }
}