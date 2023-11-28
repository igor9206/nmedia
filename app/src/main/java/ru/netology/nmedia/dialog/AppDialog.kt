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
            .setTitle("Перейти на страницу авторизации?")
            .setMessage("Операция доступна только авторизованным пользователям.")
            .setNegativeButton(
                "cancel"
            ) { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton("ok") { _, _ -> findNavController(view).navigate(R.id.authorizationFragment) }
            .create()
            .show()
    }

    fun dialogExit(view: View) {
        AlertDialog.Builder(view.context)
            .setTitle("Выход")
            .setMessage("Вы уверены что хотите выйти?")
            .setNegativeButton(
                "cancel"
            ) { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton("ok") { _, _ ->
                AppAuth.getInstance().removeAuth()
                findNavController(view).navigateUp()
            }
            .create()
            .show()
    }

}