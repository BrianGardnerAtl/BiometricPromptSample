package com.bignerdranch.android.biometricpromptupdate

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometrics.BiometricPrompt
import androidx.databinding.DataBindingUtil
import com.bignerdranch.android.biometricpromptupdate.databinding.ActivityAuthenticationCompatBinding
import java.util.concurrent.Executor

private const val TAG = "AuthCompatActivity"

class AuthenticationCompatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationCompatBinding
    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication_compat)

        // Configure BiometricPrompt object
        val executor = if (Build.VERSION.SDK_INT >= 28) {
            mainExecutor
        } else {
            Executor {
                fun execute(runnable: Runnable) {
                    runnable.run()
                }
            }
        }
        biometricPrompt = BiometricPrompt(this,
                executor, authenticationCallback)

        // Setup compat BiometricPrompt
        binding.authenticateCompatButton.isEnabled = hasBiometrics()
        binding.authenticateCompatButton.setOnClickListener { showAuthenticateCompatDialog() }
    }

    override fun onPause() {
        super.onPause()
    }

    private fun hasBiometrics(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            packageManager.hasSystemFeature(
                    PackageManager.FEATURE_FINGERPRINT
            )
        } else {
            false
        }
    }

    /**
     * Function to show the compat authentication dialog followed by the authentication callback and
     * the negative button listener
     */
    private fun showAuthenticateCompatDialog() {
        val title = getString(R.string.authentication_title)
        val cancel = getString(R.string.cancel_button)
        // Configure data for the prompt
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setNegativeButtonText(cancel)
                .build()
        biometricPrompt.authenticate(promptInfo)
    }

    private val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            Log.d(TAG, "onAuthenticationSucceeded")
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            Log.d(TAG, "onAuthenticationError $errString")
        }

        override fun onAuthenticationFailed() {
            Log.d(TAG, "onAuthenticationFailed")
        }
    }
}