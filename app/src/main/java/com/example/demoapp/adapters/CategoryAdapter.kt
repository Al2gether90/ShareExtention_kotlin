package com.example.demoapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.databinding.ListItemCategoryBinding
import com.example.demoapp.models.Category
import com.example.demoapp.models.Image
import com.example.demoapp.utils.hide
import com.example.demoapp.utils.show

class CategoryAdapter(
    private val list: ArrayList<Category>,
    private var handleClick: (Category, Image, Int) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ListItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = list[position]

        with(holder) {

            with(rvImages) {
                layoutManager = GridLayoutManager(context, 3)
                clipToPadding = false
                adapter = PhotosAdapter(category.photos!!) {
                    handleClick(category, it, holder.absoluteAdapterPosition)
                }
            }

            if (!(rvImages.adapter as PhotosAdapter).enableSelection) {
                tvCancel.hide()
                tvSelect.show()
            } else {
                tvCancel.show()
                tvSelect.hide()
            }

            tvStructureName.text = category.title

            tvSelect.setOnClickListener {
                if ((rvImages.adapter as PhotosAdapter).enableSelection) {
                    (rvImages.adapter as PhotosAdapter).selectAllPhotos()
                }
                tvSelect.hide()
                (rvImages.adapter as PhotosAdapter).enableSelection = true
                tvCancel.show()
            }

            tvCancel.setOnClickListener {
                (rvImages.adapter as PhotosAdapter).enableSelection = false
                (rvImages.adapter as PhotosAdapter).unselectAllPhotos()
                tvCancel.hide()
                tvSelect.show()
            }
        }
    }

    override fun getItemCount(): Int = list.size

    inner class CategoryViewHolder(itemCategoryBinding: ListItemCategoryBinding) :
        RecyclerView.ViewHolder(itemCategoryBinding.root) {

        val tvStructureName = itemCategoryBinding.tvStructureName
        val rvImages = itemCategoryBinding.rvImages
        val tvSelect = itemCategoryBinding.tvSelect
        val tvCancel = itemCategoryBinding.tvCancel
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addCategories(categories: List<Category>) {
        list.clear()
        list.addAll(categories)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }
}