package com.example.pftapp.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NoteViewModal (application: Application) : AndroidViewModel(application) {

    val allExpenses : LiveData<List<Expense>>
    val repository : NoteRepository
    val budgetDao = NoteDatabase.getDatabase(application).budgetDao()

    private val expenseDao = NoteDatabase.getDatabase(application).getExpenseDao()


    init {
        val dao = NoteDatabase.getDatabase(application).getExpenseDao()
        repository = NoteRepository(dao)
        allExpenses = repository.allExpenses

    }

    fun deleteNote (expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(expense)
    }

    fun updateNote(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(expense)
    }

    fun addNote(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(expense)
    }

    fun setBudget(budget: Budget) {
        viewModelScope.launch { budgetDao.setBudget(budget) }
    }

    fun getBudgetForCategory(category: String): Flow<Double?> {
        return budgetDao.getBudgetForCategory(category)
    }

    fun getTotalSpent(category: String): Flow<Double?> {
        return expenseDao.getTotalSpent(category)
    }

    suspend fun isOverBudget(category: String): Boolean {
        val totalSpent = expenseDao.getTotalSpent(category).first() ?: 0.0
        val budgetLimit = budgetDao.getBudgetForCategory(category).first() ?: 0.0
        return totalSpent >= budgetLimit * 0.9
    }
}