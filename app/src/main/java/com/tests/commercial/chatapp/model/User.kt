package com.tests.commercial.chatapp.model

import com.google.gson.annotations.SerializedName

data class User(

    @SerializedName("id")
    var id: String,

    @SerializedName("user_name")
    var userName: String,

    @SerializedName("user_photo")
    var userPhoto: String,

    @SerializedName("user_status")
    var userStatus: String
) {
    constructor() : this(
        "",
        "",
        "",
        ""
    )
}