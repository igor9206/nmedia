package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.dto.Post

class EditPostResultContract : ActivityResultContract<Post?, String?>() {
    override fun createIntent(context: Context, input: Post?): Intent {
        return Intent(context, EditPostFragment::class.java).apply {
            putExtra(Intent.ACTION_EDIT, input?.content)
        }
    }


    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return if (resultCode == Activity.RESULT_OK) {
            intent?.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            null
        }
    }
}