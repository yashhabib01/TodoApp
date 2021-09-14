package com.example.mvvmtodo.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

enum class SortOrder {
    BY_NAME,BY_DATE
}

data class FilterPreference(val sortOrder: SortOrder , val hideCompleted:Boolean)

@Singleton
class PreferencesManager @Inject constructor( @ApplicationContext  context: Context) {

     private val dataStore = context.createDataStore("user_preference")

     val preferenceFlow = dataStore.data
         .catch { exception ->
             if(exception is IOException){
                 Log.e(TAG,  "Error Reading Preferences" ,exception )
                 emit(emptyPreferences())
             }else{
                 throw  exception
             }

         }

         .map {preference->
                val sortOrder = SortOrder.valueOf(
                    preference[PreferenceKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name
                )
             val hideCompleted = preference[PreferenceKeys.HIDE_COMPLETED] ?:false

            FilterPreference(sortOrder,hideCompleted)
         }

    suspend fun updateSortOrder(sortOrder: SortOrder){
        dataStore.edit {preference->
        preference[PreferenceKeys.SORT_ORDER]    = sortOrder.name

        }
    }
    suspend fun updateHideCompleted(hideCompleted:Boolean){
        dataStore.edit {preference->
        preference[PreferenceKeys.HIDE_COMPLETED]    = hideCompleted

        }
    }

    private object PreferenceKeys{
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}