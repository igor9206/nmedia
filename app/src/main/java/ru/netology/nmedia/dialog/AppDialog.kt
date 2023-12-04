package ru.netology.nmedia.dialog

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import androidx.navigation.Navigation.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth

object AppDialog {

    fun dialogAuthorization(view: View) {
        AlertDialog.Builder(view.context)
            .setTitle(R.string.go_to_the_authorization_page)
            .setMessage(R.string.the_operation_is_available_only_to_authorized_users)
            .setNegativeButton(
                R.string.cancel
            ) { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton(R.string.ok) { _, _ -> findNavController(view).navigate(R.id.authorizationFragment) }
            .create()
            .show()
    }

    fun dialogExit(view: View) {
        AlertDialog.Builder(view.context)
            .setTitle(R.string.exit)
            .setMessage(R.string.are_you_sure_you_want_to_get_out)
            .setNegativeButton(
                R.string.cancel
            ) { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton(R.string.ok) { _, _ ->
                AppAuth.getInstance().removeAuth()
                findNavController(view).navigateUp()
            }
            .create()
            .show()
    }

}