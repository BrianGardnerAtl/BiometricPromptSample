package com.bignerdranch.android.biometricpromptupdate

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class EnableBiometricsDialog : DialogFragment() {

    interface Callbacks {
        fun approve()
        fun cancel()
    }

    private var callback: Callbacks? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = context as Callbacks
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.enable_biometric_title)
                .setMessage(R.string.enable_biometric_message)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    callback?.approve()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    callback?.cancel()
                }
                .create()
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }
}