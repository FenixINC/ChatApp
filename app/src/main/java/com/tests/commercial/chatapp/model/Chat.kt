package com.tests.commercial.chatapp.model

import com.google.gson.annotations.SerializedName

data class Chat(

    @SerializedName("userSender")
    var userSender: String,

    @SerializedName("userReceiver")
    var userReceiver: String,

    @SerializedName("userMessage")
    var userMessage: String
) {
    constructor() : this(
        "",
        "",
        ""
    )
}