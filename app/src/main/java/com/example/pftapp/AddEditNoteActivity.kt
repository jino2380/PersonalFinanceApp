package com.example.pftapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.pftapp.database.Budget
import com.example.pftapp.database.Expense
import com.example.pftapp.database.NoteViewModal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class AddEditNoteActivity : AppCompatActivity() {

    lateinit var amountEdt: EditText
    lateinit var noteEdt: EditText
    lateinit var backBtn: ImageView
    lateinit var saveBtn: Button

    lateinit var noteCategory: String
    // on below line we are creating variable for
    // viewmodal and integer for our note id.
    lateinit var viewModal: NoteViewModal
    var noteID = -1;
    val categories = arrayOf("Food", "Transport", "Entertainment", "Bills", "Miscellaneous")
    val budgetAmounts = mapOf(
        "Food" to 3000,
        "Transport" to 1500,
        "Entertainment" to 1000,
        "Bills" to 2500,
        "Miscellaneous" to 5000
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)

        viewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModal::class.java)

        amountEdt= findViewById(R.id.idEdtAmount)
        noteEdt = findViewById(R.id.idEdtNoteDesc)
        saveBtn = findViewById(R.id.idBtn)
        backBtn= findViewById(R.id.backImg)
        var categorySpinner = findViewById<Spinner>(R.id.idEdtNoteName)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                noteCategory = selectedItem

                lifecycleScope.launch {
                    val totalSpent = viewModal.getTotalSpent(noteCategory).first() ?: 0.0
                    val budget = viewModal.getBudgetForCategory(noteCategory).first() ?: 0.0


                    if (totalSpent >= budgetAmounts[selectedItem]!!) {
                        Log.e("1", totalSpent.toString())
                        Log.e("2", budget.toString())

                        Toast.makeText(
                            applicationContext,
                            "Warning: You are close to exceeding your budget!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
//                        Toast.makeText(applicationContext, budgetAmounts[selectedItem]!!.toString(), Toast.LENGTH_LONG)
//                            .show()

                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        val noteType = intent.getStringExtra("noteType")
        if (noteType.equals("Edit")) {

            val noteCategory = intent.getStringExtra("noteCategory")
            val amount = intent.getStringExtra("amount")
            val noteDescription = intent.getStringExtra("noteDescription")
            noteID = intent.getIntExtra("noteId", -1)
            saveBtn.setText("Update Note")
            noteCategory?.let {
                val position = categories.indexOf(it)
                if (position >= 0) {
                    categorySpinner.setSelection(position)
                }
            }
            amountEdt.setText(amount)

            noteEdt.setText(noteDescription)
        } else {
            saveBtn.setText("Save Note")
        }

        backBtn.setOnClickListener {
            this.finish()
        }

        saveBtn.setOnClickListener {

            val noteCategory = noteCategory.toString()
            val amount = amountEdt.text.toString()
            val noteDescription = noteEdt.text.toString()

            if (noteType.equals("Edit")) {
                if (noteCategory.isNotEmpty() && noteDescription.isNotEmpty()) {
                    val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
                    val currentDateAndTime: String = sdf.format(Date())
                    val updatedNote = Expense(noteCategory, amount , noteDescription, currentDateAndTime)
                    updatedNote.id = noteID
                    viewModal.setBudget(Budget(noteCategory, amount.toDouble()))

                    viewModal.updateNote(updatedNote)
                    Toast.makeText(this, "Note Updated..", Toast.LENGTH_LONG).show()
                }
            } else {
                if (noteCategory.isNotEmpty() && noteDescription.isNotEmpty()) {
                    val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
                    val currentDateAndTime: String = sdf.format(Date())
                    viewModal.setBudget(Budget(noteCategory, amount.toDouble()))
                    viewModal.addNote(Expense(noteCategory, amount,noteDescription, currentDateAndTime))
                    Toast.makeText(this, "$noteCategory Added", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this, "Select category and Notes", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
            startActivity(Intent(applicationContext, MainActivity::class.java))
            this.finish()
        }
    }
}