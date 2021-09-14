package com.example.mvvmtodo.di

import android.app.Application
import androidx.room.Room
import com.example.mvvmtodo.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AddModule {

    @Singleton
    @Provides
    fun provideDatabase(
        app: Application,
        callBack:TaskDatabase.CallBack
    )  =    Room.databaseBuilder(app,TaskDatabase::class.java,"task_database")
            .fallbackToDestructiveMigration()
            .addCallback(callBack)
            .build()

    @Provides
    fun provideTaskDao(db : TaskDatabase) = db.taskDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())


}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

