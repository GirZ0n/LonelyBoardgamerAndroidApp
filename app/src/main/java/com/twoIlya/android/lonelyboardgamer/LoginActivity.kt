package com.twoIlya.android.lonelyboardgamer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.twoIlya.android.lonelyboardgamer.databinding.ActivityLoginBinding
import com.twoIlya.android.lonelyboardgamer.databinding.ActivityLoginBindingImpl

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)

        //Specify this activity as the lifecycleOwner for Data Binding
        binding.lifecycleOwner = this


    }
}