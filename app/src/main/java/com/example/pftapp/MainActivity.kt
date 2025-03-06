package com.example.pftapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pftapp.database.Expense
import com.example.pftapp.database.NoteViewModal
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NoteClickInterface, NoteClickDeleteInterface {
    lateinit var viewModal: NoteViewModal
    lateinit var notesRV: RecyclerView
    lateinit var addFAB: FloatingActionButton
    private lateinit var pieChart: PieChart
    val categories = arrayOf("Food", "Transport", "Entertainment", "Bills", "Misc")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notesRV = findViewById(R.id.notesRV)
        addFAB = findViewById(R.id.idFAB)
        pieChart = findViewById(R.id.pieChart)
        notesRV.layoutManager = LinearLayoutManager(this)

        val noteRVAdapter = NoteRVAdapter(this, this, this)
        notesRV.adapter = noteRVAdapter

        viewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModal::class.java)

        viewModal.allExpenses.observe(this, Observer { list ->
            list?.let {
                noteRVAdapter.updateList(it)
            }
        })
        addFAB.setOnClickListener {
             val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
            startActivity(intent)
        }

        setupPieChart(categories)

    }

    private fun setupPieChart(categories: Array<String>) {
        lifecycleScope.launch {
        if (categories.isEmpty()) {
            println("No categories available.")
            return@launch
        }

        val pieEntries = ArrayList<PieEntry>()
            if (!pieEntries.isEmpty()) {
                pieChart.visibility = android.view.View.GONE
            }

        for (i in categories.indices) {
            val totalSpent = viewModal.getTotalSpent(categories[i]).first() ?: 0.0
            println("Total spent for ${categories[i]}: $totalSpent")
            pieEntries.add(PieEntry(totalSpent.toFloat(), categories[i]))
            println("rwewresrr: $pieEntries")

        }
            if (pieEntries.isEmpty()) {
            println("No spending data available for the categories.")
            return@launch
        }

        val pieDataSet = PieDataSet(pieEntries, "Spending Categories")
        pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.setDrawEntryLabels(true)
            pieChart.setHoleColor(Color.DKGRAY)
            pieChart.holeRadius = 50f
            pieChart.setCenterTextColor(Color.BLACK)
            pieChart.setEntryLabelColor(Color.BLUE)
            pieChart.invalidate()
        }
    }

    override fun onNoteClick(note: Expense) {
        val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
        intent.putExtra("noteType", "Edit")
        intent.putExtra("noteCategory", note.category)
        intent.putExtra("amount", note.amount)
        intent.putExtra("noteDescription", note.noteDescription)
        intent.putExtra("noteId", note.id)
        startActivity(intent)
        this.finish()
    }

    override fun onDeleteIconClick(note: Expense) {

        viewModal.deleteNote(note)
        Toast.makeText(this, "${note.category} Deleted", Toast.LENGTH_LONG).show()
    }
}
