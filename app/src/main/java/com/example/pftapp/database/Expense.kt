package com.example.pftapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notesTable")

class Expense (@ColumnInfo(name = "category")val category :String,@ColumnInfo(name = "amount")val amount :String,@ColumnInfo(name = "notes")val noteDescription :String,@ColumnInfo(name = "timestamp")val timeStamp :String) {

    @PrimaryKey(autoGenerate = true) var id = 0
}