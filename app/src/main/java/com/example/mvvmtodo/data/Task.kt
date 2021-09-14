package com.example.mvvmtodo.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(tableName = "task_table")
@Parcelize
data class Task(
    val name :String,
    val username:String,
    val important : Boolean = false,
    val completed: Boolean = false,
    val create:Long  = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id:  Int = 0
): Parcelable {

    val createdDateFormatted: String
    get() = DateFormat.getDateTimeInstance().format(create)
}


@Entity(tableName = "user_table" )
data class User(
    val name:String,
    val password:String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)