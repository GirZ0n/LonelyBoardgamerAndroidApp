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
import com.twoIlya.android.lonelyboardgamer.dataClasses.EventType
import com.twoIlya.android.lonelyboardgamer.databinding.ActivityLoginBinding
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        // Если мы залогинены, то пропускаем это activity
        if (viewModel.isUserLoggedIn()) {
            // TODO: Уходим на основной экран

            Toast.makeText(this, "Вы уже залогинены", Toast.LENGTH_SHORT).show()

            /*
            val intent = Intent(this, BossActivity::class.java)
            startActivity(intent)
            finish()
            */
        }

        supportActionBar?.hide()

        viewModel.serverToken.observe(this) {
            // TODO: Перейти в профиль
            Toast.makeText(this, it.value, Toast.LENGTH_SHORT).show()
        }

        viewModel.eventLiveData.observe(this) {
            when (it.isHandle) {
                true -> return@observe
                false -> it.isHandle = true
            }

            when (it.type) {
                EventType.Warning -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                EventType.Move -> {
                    if (it.message == "Personalization") {
                        // TODO: Перейти на страницу персонализации
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
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

                viewModel.login(token.accessToken)
            }

            override fun onLoginFailed(errorCode: Int) {
                Toast.makeText(this@LoginActivity, "VK AUTH ERROR: $errorCode", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        if (!VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private companion object {
        private const val TAG = "LoginActivity_TAG"
    }
}
