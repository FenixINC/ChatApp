package com.tests.commercial.chatapp.content

import com.tests.commercial.chatapp.model.User

interface OnUserListener {
    fun onUserClick(user: User)
}