package com.example.mvvmtodo.ui.login

import android.text.TextUtils
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mvvmtodo.dao.TaskDao
import com.example.mvvmtodo.ui.addedittask.AddEditTaskViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class LoginViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    fun LoginUser(username: String, password: String) {

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {

            viewModelScope.launch {
                loginTaskEventChannel.send(LoginEvent.InputEmptyError)
            }
            return
        } else {

            viewModelScope.launch {
                val user = taskDao.getUserWithNameAndPassword(username,password).first()


                var exits  = false
                if (user.size > 0) {
                    loginTaskEventChannel.send(
                        LoginEvent.LoginSuccessfulNavigateToTask(
                            username = username
                        )
                    )
                    exits = true
                }

                if (exits == false) {
                    loginTaskEventChannel.send(LoginEvent.LoginErrorEvent)
                }
            }
        }


    }

    fun navigateToRegisterScree() = viewModelScope.launch {
        loginTaskEventChannel.send(LoginEvent.NavigateToRegisterScreen)
    }

    private val loginTaskEventChannel = Channel<LoginViewModel.LoginEvent>()
    val loginTaskEvent = loginTaskEventChannel.receiveAsFlow()

    sealed class LoginEvent {
        data class LoginSuccessfulNavigateToTask(val username: String) : LoginEvent()
        object InputEmptyError : LoginEvent()
        object LoginErrorEvent : LoginEvent()
        object NavigateToRegisterScreen : LoginEvent()
    }

}