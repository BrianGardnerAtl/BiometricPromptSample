package com.bignerdranch.android.biometricpromptupdate

import android.content.DialogInterface
import android.content.pm.PackageManager.FEATURE_FINGERPRINT
import android.hardware.biometrics.BiometricPrompt
import android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
import android.hardware.biometrics.BiometricPrompt.AuthenticationResult
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bignerdranch.android.biometricpromptupdate.databinding.ActivityAuthenticationBinding

private const val TAG = "AuthenticationActivity"

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var cancellationSignal: CancellationSignal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)

        cancellationSignal = CancellationSignal()

        // Setup framework BiometricPrompt
        if (hasBiometrics()) {
            binding.authenticateButton.isEnabled = true
            binding.prettyAuthenticateButton.isEnabled = true
        } else {
            binding.authenticateButton.isEnabled = false
            binding.authenticateButton.text = getString(R.string.no_biometrics)
            binding.prettyAuthenticateButton.isEnabled = false
            binding.prettyAuthenticateButton.text = getString(R.string.no_biometrics)
        }
        binding.authenticateButton.setOnClickListener { showAuthenticationDialog() }
        binding.prettyAuthenticateButton.setOnClickListener { showPrettyAuthenticationDialog() }
    }

    override fun onPause() {
        super.onPause()
        cancellationSignal.cancel()
    }

    // Framework BiometricPrompt is API 28+
    private fun hasBiometrics(): Boolean {
        return Build.VERSION.SDK_INT >=  28 && hasFingerprintFeature()
    }

    private fun hasFingerprintFeature(): Boolean {
        return packageManager.hasSystemFeature(FEATURE_FINGERPRINT)
    }

    /**
     * Function to show the framework authentication dialog followed by the authentication callback
     * and the negative button listener
     */
    private fun showAuthenticationDialog() {
        val title = getString(R.string.authentication_title)
        val cancel = getString(R.string.cancel_button)
        val biometricPrompt = BiometricPrompt.Builder(this)
                .setTitle(title)
                .setNegativeButton(cancel, mainExecutor, negativeListener)
                .build()

        biometricPrompt.authenticate(
                cancellationSignal, mainExecutor, authenticationCallback)
    }

    private fun showPrettyAuthenticationDialog() {
        val title = getString(R.string.authentication_title)
        val cancel = getString(R.string.cancel_button)
        val subtitle = getString(R.string.authentication_subtitle)
        val description = getString(R.string.authentication_description)
        val biometricPrompt = BiometricPrompt.Builder(this)
                .setTitle(title)
                .setNegativeButton(cancel, mainExecutor, negativeListener)
                .setSubtitle(subtitle)
                .setDescription(description)
                .build()

        biometricPrompt.authenticate(
                cancellationSignal, mainExecutor, authenticationCallback)
    }

    private val authenticationCallback = object: AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: AuthenticationResult?) {
            Log.d(TAG, "onAuthenticationSucceeded")
        }

        override fun onAuthenticationError(errorCode: Int,
                                           errString: CharSequence?) {
            Log.d(TAG, "onAuthenticationError $errString")
        }

        override fun onAuthenticationFailed() {
            Log.d(TAG, "onAuthenticationFailed")
        }

        override fun onAuthenticationHelp(helpCode: Int,
                                          helpString: CharSequence?) {
            Log.d(TAG, "onAuthenticationHelp $helpString")
        }
    }

    private val negativeListener =
            DialogInterface.OnClickListener { _, _ ->
        Log.d(TAG, "BiometricPrompt cancelled")
    }
}
