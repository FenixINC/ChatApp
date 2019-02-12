package com.tests.commercial.chatapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.tests.commercial.chatapp.databinding.ActivitySettingsBinding
import com.tests.commercial.chatapp.model.User
import jp.wasabeef.glide.transformations.CropSquareTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import timber.log.Timber
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySettingsBinding

    private lateinit var mDbReference: DatabaseReference
    private lateinit var mFirebaseUser: FirebaseUser

    private lateinit var mStorageReference: StorageReference
    private lateinit var imageUri: Uri

    private lateinit var mStorageTask: StorageTask<UploadTask.TaskSnapshot>

    private val IMAGE_REQUEST = 1
    private val TAG_DIALOG_PROGRESS = "tag_dialog_progress"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        mStorageReference = FirebaseStorage.getInstance().getReference("uploads")
        mFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        mDbReference = FirebaseDatabase.getInstance().reference.child("Users").child(mFirebaseUser.uid)
        mDbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapchot: DataSnapshot) {
                val user = dataSnapchot.getValue(User::class.java)
                if (user != null) {
                    mBinding.model = user

                    Glide.with(applicationContext)
                        .load("http://pics.wikireality.ru/upload/thumb/a/a1/Calm_down.jpg/300px-Calm_down.jpg")
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

            override fun onCancelled(dbError: DatabaseError) {
                Timber.e(dbError.message + ", code: " + dbError.code)
            }
        })

        mBinding.btnChangePhoto.setOnClickListener {
            openImage()
        }
    }

    private fun openImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_REQUEST)
    }

    private fun getFileExtension(uri: Uri): String {
        val contentResolver = this@SettingsActivity.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))!!
    }

//    private fun uploadImage() {
//        ProgressDialog().newInstance("Uploading..").show(supportFragmentManager, TAG_DIALOG_PROGRESS)
//        if (imageUri != null) {
//            val fileReference = mStorageReference.child(getFileExtension(imageUri))
//            mStorageTask = fileReference.getFile(imageUri)
////            uploadTask.continueW
//        } else {
//            Toast.makeText(this@SettingsActivity, "No image selected!", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun uploadImage() {
        if (imageUri != null) {
            val ref: StorageReference = mStorageReference.child("images/" + getFileExtension(imageUri))
            ref.putFile(imageUri)
                .addOnProgressListener {

                }
            mStorageTask = ref.putFile(imageUri)
            mStorageTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uri = task.result
                    val strUri = uri.toString()

                    mDbReference = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser.uid)
                    val map = HashMap<String, Any>()
                    map["userPhoto"] = strUri
                    mDbReference.updateChildren(map)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!

//            if (mStorageTask != null && mStorageTask.isInProgress) {
//                Toast.makeText(this@SettingsActivity, "Upload in process", Toast.LENGTH_SHORT).show()
//            } else {
            uploadImage()
//            }
        }
    }

    private fun hideProgressDialog() {
        val fragment = supportFragmentManager.findFragmentByTag(TAG_DIALOG_PROGRESS)
        if (fragment != null && fragment is DialogFragment) {
            fragment.dismissAllowingStateLoss()
        }
    }
}