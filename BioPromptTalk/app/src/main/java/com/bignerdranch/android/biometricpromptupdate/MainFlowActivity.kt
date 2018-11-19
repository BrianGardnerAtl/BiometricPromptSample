package com.bignerdranch.android.biometricpromptupdate

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.biometrics.BiometricPrompt
import java.util.concurrent.Executor

private const val TAG = "MainFlowActivity"

class MainFlowActivity : AppCompatActivity() {

    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        errorTextView = findViewById(R.id.biometric_error)
    }

    override fun onResume() {
        super.onResume()
        // Set this to true just for an example. In a real app this would come from SharedPrefs or
        // something like it
        val hasUsedBiometrics = true
        if (hasUsedBiometrics) {
            promptForBiometrics()
        }
    }

    private fun promptForBiometrics() {
        // Show the biometric prompt to authenticate
        val executor = if (Build.VERSION.SDK_INT >= 28) {
            mainExecutor
        } else {
            Executor {
                fun execute(runnable: Runnable) {
                    runnable.run()
                }
            }
        }
        val biometricPrompt = BiometricPrompt(this,
                executor, authenticationCallback)
        val title = getString(R.string.biometric_authentication_title)
        val cancel = getString(android.R.string.cancel)
        val message = getString(R.string.biometric_authentication_description)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setNegativeButtonText(cancel)
                .setDescription(message)
                .build()
        biometricPrompt.authenticate(promptInfo)
    }

    private val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            Log.d(TAG, "onAuthenticationSucceeded")
            // successfully confirmed biometrics, remember for next log in and
            // continue to authenticated screen
            navigateToAuthenticatedScreen()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            Log.d(TAG, "onAuthenticationError $errString")
            errorTextView.visibility = View.VISIBLE
        }

        override fun onAuthenticationFailed() {
            Log.d(TAG, "onAuthenticationFailed")
        }
    }

    private fun navigateToAuthenticatedScreen() {
        val intent = Intent(this, AuthenticatedActivity::class.java)
        startActivity(intent)
    }
}