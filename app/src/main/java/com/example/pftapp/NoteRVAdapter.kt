package com.example.pftapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pftapp.database.Expense

class NoteRVAdapter(
    val context: Context,
    val noteClickDeleteInterface: NoteClickDeleteInterface,
    val noteClickInterface: NoteClickInterface
) :
    RecyclerView.Adapter<NoteRVAdapter.ViewHolder>() {


    private val allNotes = ArrayList<Expense>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val noteTV = itemView.findViewById<TextView>(R.id.idTVNote)
        val amountTV = itemView.findViewById<TextView>(R.id.idTVAmount)
        val dateTV = itemView.findViewById<TextView>(R.id.idTVDate)
        val deleteIV = itemView.findViewById<ImageView>(R.id.idIVDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.note_rv_item,
            parent, false
        )
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.noteTV.setText(allNotes.get(position).category)
        holder.amountTV.setText(allNotes.get(position).amount)
        holder.dateTV.setText("Last Updated : " + allNotes.get(position).timeStamp)
        holder.deleteIV.setOnClickListener {

            noteClickDeleteInterface.onDeleteIconClick(allNotes.get(position))
        }

        holder.itemView.setOnClickListener {

            noteClickInterface.onNoteClick(allNotes.get(position))
        }
    }

    override fun getItemCount(): Int {

        return allNotes.size
    }

    fun updateList(newList: List<Expense>) {

        allNotes.clear()

        allNotes.addAll(newList)

        notifyDataSetChanged()
    }
}

interface NoteClickDeleteInterface {

    fun onDeleteIconClick(note: Expense)
}

interface NoteClickInterface {

    fun onNoteClick(note: Expense)
}