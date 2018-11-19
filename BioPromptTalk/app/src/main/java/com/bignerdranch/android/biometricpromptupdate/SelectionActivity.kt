package com.bignerdranch.android.biometricpromptupdate

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bignerdranch.android.biometricpromptupdate.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_selection)

        binding.authenticateButton.setOnClickListener {
            startAuthenticationActivity()
        }
        if (hasFrameworkBiometrics()) {
            binding.authenticateButton.isEnabled = true
        } else {
            binding.authenticateButton.isEnabled = false
            binding.authenticateButton.text = getString(R.string.no_biometrics)
        }

        binding.authenticateCompatButton.setOnClickListener {
            startAuthenticationCompatActivity()
        }

        binding.firstTimeFlowButton.setOnClickListener {
            startFirstTimeFlowActivity()
        }

        binding.mainFlowButton.setOnClickListener {
            startMainFlowActivity()
        }
    }

    // Framework BiometricPrompt is API 28+
    private fun hasFrameworkBiometrics(): Boolean {
        return if (Build.VERSION.SDK_INT >=  28) {
            packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
        } else {
            false
        }
    }

    private fun startAuthenticationActivity() {
        val intent = Intent(this, AuthenticationActivity::class.java)
        startActivity(intent)
    }

    private fun startAuthenticationCompatActivity() {
        val intent = Intent(this, AuthenticationCompatActivity::class.java)
        startActivity(intent)
    }

    private fun startFirstTimeFlowActivity() {
        val intent = Intent(this, FirstTimeFlowActivity::class.java)
        startActivity(intent)
    }

    private fun startMainFlowActivity() {
        val intent = Intent(this, MainFlowActivity::class.java)
        startActivity(intent)
    }
}