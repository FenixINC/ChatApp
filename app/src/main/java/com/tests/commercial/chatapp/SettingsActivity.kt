package com.tests.commercial.chatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.tests.commercial.chatapp.databinding.ActivitySettingsBinding
import jp.wasabeef.glide.transformations.CropSquareTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class SettingsActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySettingsBinding

    private lateinit var mDbReference: DatabaseReference
    private lateinit var mCurrentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        mCurrentUser = FirebaseAuth.getInstance().currentUser!!
        val currentUid = mCurrentUser.uid
        mDbReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUid)

        mDbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapchot: DataSnapshot) {
                val name = dataSnapchot.child("user_name").value.toString()
                val status = dataSnapchot.child("user_status").value.toString()
                val photo = dataSnapchot.child("user_photo").value.toString()

                mBinding.userName.text = name
                mBinding.userStatus.text = status
//                mBinding.userPhoto = photo
            }

            override fun onCancelled(dbError: DatabaseError) {

            }
        })

        Glide.with(applicationContext)
            .load("")
            .apply(
                RequestOptions()
                    .transforms(
                        CropSquareTransformation(),
                        RoundedCornersTransformation(300, 0)
                    )
            )
            .into(mBinding.userPhoto)


    }
}