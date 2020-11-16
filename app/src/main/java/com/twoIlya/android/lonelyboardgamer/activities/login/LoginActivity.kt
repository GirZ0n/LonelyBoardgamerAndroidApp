package com.twoIlya.android.lonelyboardgamer.activities.login

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.twoIlya.android.lonelyboardgamer.R
import com.twoIlya.android.lonelyboardgamer.databinding.ActivityLoginBinding
import com.twoIlya.android.lonelyboardgamer.sharedPref.Cache
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        // Если мы залогинены, то пропускаем это activity
        if (Cache.isLoggedIn(this)) {
            // TODO: Уходим на основной экран

            Toast.makeText(this, "Вы уже залогинены", Toast.LENGTH_SHORT).show()

            /*
            val intent = Intent(this, BossActivity::class.java)
            startActivity(intent)
            finish()
            */
        }

        supportActionBar?.hide()

        viewModel.loginServerResponse.observe(this) {
            // TODO: или обработать ошибку, или отправить пользователя куда-то
        }

        binding.loginButton.setOnClickListener {
            if (!it.isEnabled) {
                Toast.makeText(this, "Запрос обрабатывается", Toast.LENGTH_SHORT).show()
            }

            VK.login(this, emptyList())
            it.isEnabled = false
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

                viewModel.login(token.accessToken)
            }

            override fun onLoginFailed(errorCode: Int) {
                Toast.makeText(this@LoginActivity, "VK AUTH ERROR: $errorCode", Toast.LENGTH_SHORT)
                    .show()
                binding.loginButton.isEnabled = true
            }
        }

        if (!VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private companion object {
        private const val TAG = "LOGIN_ACTIVITY"
    }
}