package com.example.pftapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pftapp.database.Expense
import com.example.pftapp.database.NoteViewModal
import com.example.pftapp.ui.theme.PFTAppTheme
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

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

        // on below line we are setting layout
        // manager to our recycler view.
        notesRV.layoutManager = LinearLayoutManager(this)

        // on below line we are initializing our adapter class.
        val noteRVAdapter = NoteRVAdapter(this, this, this)

        // on below line we are setting
        // adapter to our recycler view.
        notesRV.adapter = noteRVAdapter

        // on below line we are
        // initializing our view modal.
        viewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModal::class.java)

        // on below line we are calling all notes method
        // from our view modal class to observer the changes on list.
        viewModal.allExpenses.observe(this, Observer { list ->
            list?.let {
                // on below line we are updating our list.
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
            // Get the total spent for the current category
            val totalSpent = viewModal.getTotalSpent(categories[i]).first() ?: 0.0
            println("Total spent for ${categories[i]}: $totalSpent")

            // Add a PieEntry for the category
            pieEntries.add(PieEntry(totalSpent.toFloat(), categories[i]))
            println("rwewresrr: $pieEntries")

        }

        // Check if there are any entries to display
        if (pieEntries.isEmpty()) {
            println("No spending data available for the categories.")
            return@launch // Exit the function if there are no entries
        }

        val pieDataSet = PieDataSet(pieEntries, "Spending Categories")
        pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList() // Set colors for the pie chart

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.description.isEnabled = false // Disable the description
        pieChart.setDrawEntryLabels(true) // Enable entry labels
            pieChart.setHoleColor(Color.DKGRAY) // Set the hole color
            pieChart.holeRadius = 50f
            pieChart.setCenterTextColor(Color.BLACK)
            pieChart.setEntryLabelColor(Color.BLUE)
            pieChart.invalidate() // Refresh the chart
        }
    }


//    private fun setupPieChart(categories: Array<String>, spendingAmounts: DoubleArray) {
//        if (categories.isEmpty()) {
//            println("No categories available.")
//            return // Exit the function if there are no categories
//        }
//        val pieEntries = ArrayList<PieEntry>()
//
////        for (category in categories) {
////            val totalSpent = viewModal.getTotalSpent(category)
////
////            val budget = budgetAmounts[category]
////            println("$category: $budget")
////        }
//lifecycleScope.launch {
//        for (i in categories.indices) {
//            var totalSpent = viewModal.getTotalSpent(categories[i]).first() ?:0.0
//            println("yyyy: ${totalSpent}")
//                pieEntries.add(PieEntry(totalSpent.toFloat(), categories[i]))
//
//
//
//        }
//}
//
////        for (i in categories.indices) {
////            pieEntries.add(PieEntry(spendingAmounts[i].toFloat(), categories[i]))
////        }
//
//        val pieDataSet = PieDataSet(pieEntries, "Spending Categories")
//        pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList() // Set colors for the pie chart
//
//        val pieData = PieData(pieDataSet)
//        pieChart.data = pieData
//        pieChart.description.isEnabled = false // Disable the description
//        pieChart.setDrawEntryLabels(true) // Enable entry labels
//        pieChart.invalidate() // Refresh the chart
//    }

    override fun onNoteClick(note: Expense) {
        // opening a new intent and passing a data to it.
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
        // displaying a toast message
        Toast.makeText(this, "${note.category} Deleted", Toast.LENGTH_LONG).show()
    }



}
