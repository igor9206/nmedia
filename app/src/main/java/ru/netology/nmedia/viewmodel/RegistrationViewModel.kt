package ru.netology.nmedia.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth

class RegistrationViewModel : ViewModel() {

    val data = AppAuth.getInstance().authState

    fun registration(name: String, login: String, pass: String, context: Context) = viewModelScope.launch {
        AppAuth.registration(name, login, pass, context)
    }
}