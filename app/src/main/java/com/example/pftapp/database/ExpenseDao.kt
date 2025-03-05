package com.example.pftapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note :Expense)

    @Delete
    suspend fun delete(note: Expense)


    @Query("Select * from notesTable order by id ASC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Update
    suspend fun update(note: Expense)

    @Query("SELECT SUM(amount) FROM notesTable WHERE category = :category")
    fun getTotalSpent(category: String): Flow<Double?>

}