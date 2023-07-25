package com.example.demoapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.demoapp.R
import com.example.demoapp.databinding.ListItemAssignmentBinding
import com.example.demoapp.models.Assignment

class AssignmentAdapter(
    private var assignmentList: ArrayList<Assignment>,
    private var handleClick: (Assignment) -> Unit
) : RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>(), Filterable {

    var assignmentFilteredList: ArrayList<Assignment> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val binding =
            ListItemAssignmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        val assignment = assignmentFilteredList[position]
        with(assignment) {
            with(holder) {
                tvTitle.text = if (title.isNullOrEmpty()) "Title" else title
                tvSubTitle.text =
                    "$claimInsuredAddress1, $claimInsuredAddress2, $claimInsuredCity, $claimInsuredState, $claimInsuredZipCode"
                imageView.load(image) {
                    crossfade(true)
                    placeholder(R.drawable.img_placeholder_new)
                    error(R.drawable.img_placeholder_new)
                }
                itemView.setOnClickListener {
                    handleClick(assignment)
                }
            }
        }
    }

    override fun getItemCount(): Int = assignmentFilteredList.size

    inner class AssignmentViewHolder(subjectBinding: ListItemAssignmentBinding) :
        RecyclerView.ViewHolder(subjectBinding.root) {

        val tvTitle = subjectBinding.tvTitle
        val tvSubTitle = subjectBinding.tvSubTitle
        val imageView = subjectBinding.imageView
    }

    fun addAssignments(assignments: List<Assignment>) {
        assignmentList = assignments as ArrayList<Assignment>
        assignmentFilteredList = assignmentList
        notifyItemRangeChanged(0, assignmentList.size)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                assignmentFilteredList = if (charString.isEmpty()) assignmentList else {
                    val filteredList = ArrayList<Assignment>()
                    assignmentList
                        .filter { it.title != null }
                        .filter {
                            (it.title?.lowercase()?.contains(charString)!!)
                        }.forEach { filteredList.add(it) }
                    filteredList
                }
                return FilterResults().apply { values = assignmentFilteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                assignmentFilteredList = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<Assignment>
                notifyDataSetChanged()
            }
        }
    }

    fun reset() {
        assignmentFilteredList = assignmentList
        notifyItemRangeChanged(0, assignmentList.size)
    }
}