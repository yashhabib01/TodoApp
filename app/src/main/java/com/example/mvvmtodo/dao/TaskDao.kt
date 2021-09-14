package com.example.mvvmtodo.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mvvmtodo.data.SortOrder
import com.example.mvvmtodo.data.Task
import com.example.mvvmtodo.data.User
import com.example.mvvmtodo.ui.task.TaskViewModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    fun getTask(
        query: String,
        sortOrder: SortOrder,
        hideCompleted: Boolean,
        username: String
    ): Flow<List<Task>> =
        when (sortOrder) {
            SortOrder.BY_DATE -> getTaskSortedByDateCreated(query, hideCompleted, username)
            SortOrder.BY_NAME -> getTaskSortedByName(query, hideCompleted, username)
        }



    @Query("SELECT * FROM task_table  WHERE (completed != :hideCompleted OR completed == 0) AND  name LIKE '%' || :searchQuery || '%' AND username = :username  ORDER BY important DESC,name")
    fun getTaskSortedByName(
        searchQuery: String,
        hideCompleted: Boolean,
        username: String
    ): Flow<List<Task>>

    @Query("SELECT * FROM task_table  WHERE (completed != :hideCompleted OR completed == 0) AND  name LIKE '%' || :searchQuery || '%' AND username =:username  ORDER BY important DESC,`create`")
    fun getTaskSortedByDateCreated(
        searchQuery: String,
        hideCompleted: Boolean,
        username: String
    ): Flow<List<Task>>

    @Query("SELECT * FROM user_table")
    fun getUser(): Flow<List<User>>

    @Query("SELECT * FROM user_table WHERE name LIKE :username")
    fun getUserWithName(username:String) : Flow<List<User>>

    @Query("SELECT * FROM user_table WHERE name LIKE :username AND password LIKE :password ")
    fun getUserWithNameAndPassword(username: String,password:String) : Flow<List<User>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM task_table WHERE completed = 1")
    suspend fun deleteCompletedTasks()


    @Insert
    suspend  fun insert(user: User)


}