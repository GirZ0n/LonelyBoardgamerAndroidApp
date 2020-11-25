package com.twoIlya.android.lonelyboardgamer.activities.login

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.twoIlya.android.lonelyboardgamer.R
import com.twoIlya.android.lonelyboardgamer.activities.error.ErrorActivity
import com.twoIlya.android.lonelyboardgamer.activities.main.MainActivity
import com.twoIlya.android.lonelyboardgamer.dataClasses.EventType
import com.twoIlya.android.lonelyboardgamer.databinding.ActivityLoginBinding
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        // TODO: DEBUG
        val intentD = MainActivity.newActivity(this, true)
        startActivity(intentD)
        finish()
        // ---------------

        // Если мы залогинены, то пропускаем это activity
        if (viewModel.isUserLoggedIn()) {
            val intent = MainActivity.newActivity(this, false)
            startActivity(intent)
            finish()
        }

        supportActionBar?.hide()

        viewModel.eventLiveData.observe(this) {
            Log.d(TAG, "Event: $it")

            when (it.isHandle) {
                true -> return@observe
                false -> it.isHandle = true
            }

            when (it.type) {
                EventType.Warning -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    binding.loginButton.isEnabled = true
                }
                EventType.Move -> {
                    val intent = when (it.message) {
                        "Registration" -> MainActivity.newActivity(this, true)
                        "MyProfile" -> MainActivity.newActivity(this, false)
                        else -> ErrorActivity.newActivity(this, "Unknown destination")
                    }
                    startActivity(intent)
                    finish()
                }
                EventType.Error -> {
                    val intent = ErrorActivity.newActivity(this, it.message)
                    startActivity(intent)
                    finish()
                }
            }
        }

        binding.loginButton.setOnClickListener {
            VK.login(this, emptyList())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                Log.d(TAG, "onActRes (onLogin): ${token.accessToken}")

                // TODO: удалить
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("token", token.accessToken)
                clipboard.setPrimaryClip(clip)
                // ---------------------------

                binding.loginButton.isEnabled = false
                viewModel.login(token.accessToken)
            }

            override fun onLoginFailed(errorCode: Int) {
                Log.d(TAG, "onActRes (onLoginFailed): $errorCode")
                Toast.makeText(this@LoginActivity, "VK AUTH ERROR: $errorCode", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        if (!VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private const val TAG = "LoginActivity_TAG"


        fun newActivity(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
