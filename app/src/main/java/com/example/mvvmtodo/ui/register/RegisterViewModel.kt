package com.example.mvvmtodo.ui.register

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.mvvmtodo.dao.TaskDao
import com.example.mvvmtodo.data.User
import com.example.mvvmtodo.ui.login.LoginViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegisterViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
) : ViewModel() {

    sealed class RegisterEvent {
        object InputEmptyError : RegisterEvent()
        object SingUpErrorEvent : RegisterEvent()
        object NavigateToLogin : RegisterEvent()
        data class RegisterSuccessfully(
            val username: String
        ) : RegisterEvent()
    }

    private val registerTaskEventChannel = Channel<RegisterEvent>()
    val registerTaskEvent = registerTaskEventChannel.receiveAsFlow()

    fun registerUser(name: String, password: String) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            viewModelScope.launch {
                registerTaskEventChannel.send(RegisterViewModel.RegisterEvent.InputEmptyError)
            }
        } else {
            viewModelScope.launch {
                val user = taskDao.getUserWithName(username = name).first()

                Log.d("register", user.size.toString())

                var exits = false
                user.forEach {
                    if (it.name.equals(name)) {
                        Log.d("register: ", name)
                        exits = true
                    }
                }
                if (exits == false) {
                    taskDao.insert(User(name.trim(), password.trim()))
                    registerTaskEventChannel.send(RegisterEvent.RegisterSuccessfully(name))
                } else {
                    registerTaskEventChannel.send(RegisterEvent.SingUpErrorEvent)
                }
            }
        }
    }

    fun navigateToLoginScree() = viewModelScope.launch {
        registerTaskEventChannel.send(RegisterEvent.NavigateToLogin)
    }


}

