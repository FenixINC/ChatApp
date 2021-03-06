package com.tests.commercial.chatapp.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.tests.commercial.chatapp.databinding.DialogProgressBinding
import timber.log.Timber

class ProgressDialog : DialogFragment() {
    private val KEY_MESSAGE: String = "message"

    fun newInstance(message: String): ProgressDialog {
        val bundle = bundleOf(KEY_MESSAGE to message)
        this@ProgressDialog.isCancelable = false
        return ProgressDialog().apply { arguments = bundle }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments
        var message: String? = null
        if (bundle != null) {
            message = bundle.getString(KEY_MESSAGE, null)
        }
        if (message == null) {
            Timber.w("Message not set")
            message = "Please wait."
        }
        val binding = DialogProgressBinding.inflate(LayoutInflater.from(context), null, false)
        binding.message = message
        return AlertDialog.Builder(context!!)
            .setView(binding.root)
            .setCancelable(false)
            .create()
    }
}