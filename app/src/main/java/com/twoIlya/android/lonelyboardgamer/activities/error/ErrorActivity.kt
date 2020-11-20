package com.twoIlya.android.lonelyboardgamer.activities.error

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.twoIlya.android.lonelyboardgamer.R

class ErrorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)

        val errorTextField = findViewById<TextView>(R.id.error_message)
        val errorMessage = intent.getStringExtra(EXTRA_ERROR_MESSAGE)
        errorTextField.text = errorMessage
    }

    companion object {
        private const val EXTRA_ERROR_MESSAGE =
            "com.twoIlya.android.lonelyboardgamer.activities.error.error_message"

        fun newActivity(context: Context, errorMessage: String): Intent {
            return Intent(context, ErrorActivity::class.java).apply {
                putExtra(EXTRA_ERROR_MESSAGE, errorMessage)
            }
        }
    }
}
