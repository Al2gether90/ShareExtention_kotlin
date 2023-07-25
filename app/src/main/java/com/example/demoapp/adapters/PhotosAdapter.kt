package com.example.demoapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.demoapp.databinding.ListItemPhotosBinding
import com.example.demoapp.models.Image

class PhotosAdapter(
    private val list: ArrayList<Image>,
    private var handleClick: (Image) -> Unit
) : RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>() {

    var enableSelection: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val binding =
            ListItemPhotosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        val photo = list[position]

        with(holder) {
            imageView.load(photo.uri)
            cbSelect.isChecked = photo.isSelected
            itemView.setOnClickListener {
                if (enableSelection) {
                    photo.isSelected = !photo.isSelected
                    handleClick(photo)
                    notifyItemChanged(position)
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    inner class PhotosViewHolder(itemPhotosBinding: ListItemPhotosBinding) :
        RecyclerView.ViewHolder(itemPhotosBinding.root) {

        val imageView = itemPhotosBinding.imageView
        val cbSelect = itemPhotosBinding.cbSelect
    }

    fun addPhotos(images: List<Image>) {
        list.clear()
        list.addAll(images)
        notifyItemRangeChanged(0, images.size)
    }

    fun selectAllPhotos(){
        list.onEach { it.isSelected = true }
        notifyItemRangeChanged(0, list.size)
    }

    fun unselectAllPhotos(){
        list.onEach { it.isSelected = false }
        notifyItemRangeChanged(0, list.size)
    }

    fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }
}