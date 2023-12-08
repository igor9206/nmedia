package ru.netology.nmedia.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val appAuth: AppAuth
) : ViewModel() {

    val data: LiveData<AuthState> = appAuth.authState
        .asLiveData(Dispatchers.Default)

    fun registration(name: String, login: String, pass: String, context: Context) =
        viewModelScope.launch {
            appAuth.registration(name, login, pass, context)
        }
}