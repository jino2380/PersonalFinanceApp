package com.example.pftapp.database

import androidx.lifecycle.LiveData

class NoteRepository(private val expenseDao: ExpenseDao) {


    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun insert(note: Expense) {
        expenseDao.insert(note)
    }

    suspend fun delete(note: Expense){
        expenseDao.delete(note)
    }

    suspend fun update(note: Expense){
        expenseDao.update(note)
    }
}