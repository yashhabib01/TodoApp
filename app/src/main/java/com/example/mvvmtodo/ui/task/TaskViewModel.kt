package com.example.mvvmtodo.ui.task

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.mvvmtodo.dao.TaskDao
import com.example.mvvmtodo.data.PreferencesManager
import com.example.mvvmtodo.data.SortOrder
import com.example.mvvmtodo.data.Task
import com.example.mvvmtodo.ui.ADD_TASK_RESULT_OK
import com.example.mvvmtodo.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel  @ViewModelInject
constructor(private val taskDao: TaskDao,
            private val preferenceManager: PreferencesManager,
             @Assisted private val state:SavedStateHandle ) : ViewModel() {

        val searchQuery = state.getLiveData<String>("searchQuery","")

        val username = MutableStateFlow("")

        val preferenceFlow = preferenceManager.preferenceFlow



        private val tasksEventChannel  = Channel<TasksEvent>()

        val tasksEvent = tasksEventChannel.receiveAsFlow()
        private val taskFlow = combine(
                searchQuery.asFlow(),
                preferenceFlow,

        ) {query,preferenceFlow ->
                Pair(query,preferenceFlow)
        }.flatMapLatest {(query,preferenceFlow) ->
                taskDao.getTask(query,preferenceFlow.sortOrder,preferenceFlow.hideCompleted,username = username.value)
        }
        val tasks =taskFlow.asLiveData()

        fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch{
                preferenceManager.updateSortOrder(sortOrder)
        }

        fun onHideCompleted(hideComplete:Boolean) = viewModelScope.launch {
                preferenceManager.updateHideCompleted(hideComplete)
        }

        fun onTaskSelected(task: Task) = viewModelScope.launch{
                tasksEventChannel.send(TasksEvent.NavigationToEditTaskScreen(task))
        }

        fun onTaskCheckedChange(task:Task,isChecked:Boolean) = viewModelScope.launch{
                taskDao.update(task.copy(completed = isChecked))
        }

        fun onTaskSwiped(task: Task) = viewModelScope.launch{
                taskDao.delete(task)
             tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))


        }

        fun onUndoDeleteClick(task:Task) = viewModelScope.launch {
                taskDao.insert(task)
        }

        fun onAddNewTaskClick() = viewModelScope.launch {
                tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen(username = username.value))

        }

        fun onAddEditResult(result:Int){
                when(result){
                        ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
                        EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
                }
        }

        private fun showTaskSavedConfirmationMessage(text:String) = viewModelScope.launch {
                tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(text))
        }

        fun onDeleteAllCompleteClick() = viewModelScope.launch {
                tasksEventChannel.send(TasksEvent.NavigateToDeleteAllCompleteScreen)
        }

        sealed class TasksEvent {
                data class NavigateToAddTaskScreen(val username:String) : TasksEvent()
                data class NavigationToEditTaskScreen (val task:Task): TasksEvent()
                data class ShowUndoDeleteTaskMessage(val task:Task): TasksEvent()
                data class ShowTaskSavedConfirmationMessage(val msg:String) : TasksEvent()
                object NavigateToDeleteAllCompleteScreen:TasksEvent()
         }



}