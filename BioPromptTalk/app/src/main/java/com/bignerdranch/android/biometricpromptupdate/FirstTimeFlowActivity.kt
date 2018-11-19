package com.bignerdranch.android.biometricpromptupdate

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometrics.BiometricPrompt
import java.util.concurrent.Executor

private const val ENABLE_BIOMETRIC_DIALOG_TAG = "enable_biometric_dialog"
private const val TAG = "FirstTimeFlowActivity"

class FirstTimeFlowActivity : AppCompatActivity(), EnableBiometricsDialog.Callbacks{

    private lateinit var logInButton: Button
    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        logInButton = findViewById(R.id.login_button)
        logInButton.setOnClickListener {
            if (hasBiometrics()) {
                askUserToUseBiometrics()
            } else {
                navigateToAuthenticatedScreen()
            }
        }
    }

    // Enable biometrics dialog callbacks
    override fun approve() {
        // Show the biometric prompt to confirm
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
        val title = getString(R.string.confirm_biometric_title)
        val cancel = getString(android.R.string.cancel)
        val message = getString(R.string.confirm_biometric_message)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setNegativeButtonText(cancel)
                .setDescription(message)
                .build()
        biometricPrompt.authenticate(promptInfo)
    }

    override fun cancel() {
        // User doesn't want to use biometrics
        navigateToAuthenticatedScreen()
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
            Toast.makeText(this@FirstTimeFlowActivity,
                    R.string.biometric_confirmation_failed, Toast.LENGTH_LONG)
                    .show()
            // failed to confirm biometrics, ask user to try again later and
            // continue to authenticated screen
            navigateToAuthenticatedScreen()
        }

        override fun onAuthenticationFailed() {
            Log.d(TAG, "onAuthenticationFailed")
        }
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

    private fun askUserToUseBiometrics() {
        supportFragmentManager.beginTransaction()
                .add(EnableBiometricsDialog(), ENABLE_BIOMETRIC_DIALOG_TAG)
                .commit()
    }

    private fun navigateToAuthenticatedScreen() {
        val intent = Intent(this, AuthenticatedActivity::class.java)
        startActivity(intent)
    }
}