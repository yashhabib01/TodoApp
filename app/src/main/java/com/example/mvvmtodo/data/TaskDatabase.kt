package com.example.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mvvmtodo.dao.TaskDao
import com.example.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class,User::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao():TaskDao

    class CallBack @Inject constructor(
        private val database:Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope:CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()

            applicationScope.launch {

            }
        }
    }
}