package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import ru.netology.nmedia.databinding.ActivityEditPostBinding
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard

class EditPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            val text = it.getStringExtra(Intent.ACTION_EDIT)
            binding.edit.setText(text)
        }

        binding.edit.focusAndShowKeyboard()

        binding.ok.setOnClickListener {
            val intent = Intent()
            if (binding.edit.text.isNullOrBlank()) {
                setResult(Activity.RESULT_CANCELED, intent)
            } else {
                val content = binding.edit.text.toString()
                intent.putExtra(Intent.EXTRA_TEXT, content)
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }

        val activity = this
        activity.onBackPressedDispatcher.addCallback(
            activity, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val content = intent.getStringExtra(Intent.ACTION_EDIT)
                    val intent = Intent()
                    intent.putExtra(Intent.EXTRA_TEXT, content)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        )

    }
}