package com.example.demoapp.activities

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.demoapp.R
import com.example.demoapp.adapters.CategoryAdapter
import com.example.demoapp.databinding.ActivityUploadBinding
import com.example.demoapp.dialogs.ChangeStructureDialogFragment
import com.example.demoapp.dialogs.OnStructureChangeListener
import com.example.demoapp.dialogs.PhotoUploadSuccessDialogFragment
import com.example.demoapp.models.Assignment
import com.example.demoapp.models.Category
import com.example.demoapp.models.Image
import com.example.demoapp.network.Resource
import com.example.demoapp.utils.*
import com.example.demoapp.viewmodels.AppViewModel

class UploadActivity : AppCompatActivity(), OnStructureChangeListener {

    private lateinit var binding: ActivityUploadBinding

    private var assignment: Assignment? = null

    private val appViewModel: AppViewModel by viewModels()

    private val list: ArrayList<Category> = arrayListOf()

    private val photoUploadSuccessDialogFragment = PhotoUploadSuccessDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()
        setUpClickListener()
        setUploadObserver()

        intent?.let { intent ->
            val name = intent.getStringExtra(EXTRA_STRUCTURE_NAME)
            val categoryId = intent.getStringExtra(EXTRA_CATEGORY_ID)
            assignment = intent.getParcelableExtra(EXTRA_ASSIGNMENT)
            val selectedImages = intent.getStringArrayListExtra(EXTRA_IMAGES)
            list.addAll(
                arrayListOf(
                    Category(
                        title = name,
                        categoryId = categoryId,
                        photos = selectedImages?.map {
                            Image(
                                uri = Uri.parse(it),
                                categoryId = categoryId
                            )
                        } as ArrayList<Image>
                    )
                )
            )
            (binding.rvCategories.adapter as CategoryAdapter).addCategories(list)
            assignment?.let {
                setData(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(assignment: Assignment) {
        with(binding) {
            ivChange.hide()
            ivDelete.hide()
            with(assignment) {
                imageView.load(image) {
                    crossfade(true)
                    placeholder(R.drawable.img_placeholder_new)
                    error(R.drawable.img_placeholder_new)
                }
                tvTitle.text = if (title.isNullOrEmpty()) "Title" else title
                tvSubTitle.text =
                    "$claimInsuredAddress1, $claimInsuredAddress2, $claimInsuredCity, $claimInsuredState, $claimInsuredZipCode"
            }
        }
    }

    private fun setUpClickListener() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_item_upload -> {
                    if (list.isNotEmpty()) {
                        list.flatMap { category -> category.photos!! }.forEach { selectedPhoto ->
                            appViewModel.upload(
                                PROFILE_ID,
                                assignment?.assignmentId,
                                selectedPhoto.categoryId,
                                FileUtil.getTempFile(this, selectedPhoto.uri!!)
                            )
                        }
                    } else {
                        toast("There is no photo to upload")
                    }
                }
            }
            true
        }

        binding.ivDelete.setOnClickListener {

            if (list.isEmpty()) return@setOnClickListener

            alert {
                setTitle("Delete Images")
                setMessage("Are you sure?")
                positiveButton("Yes") {
                    // remove selected photos
                    list.forEach {
                        it.photos?.removeIf { image -> image.isSelected }
                    }
                    // remove category if there is no photo under it
                    list.removeIf { it.photos.isNullOrEmpty() }
                    (binding.rvCategories.adapter as CategoryAdapter).addCategories(list)
                    // hide buttons
                    binding.ivChange.hide()
                    binding.ivDelete.hide()
                }
                negativeButton("No") {
                    it.dismiss()
                }
            }
        }

        binding.ivChange.setOnClickListener {
            assignment?.let { it1 ->
                ChangeStructureDialogFragment.newInstance(
                    it1,
                    list.flatMap { it.photos!! }.count { it.isSelected }, this
                ).show(supportFragmentManager, TAG_DIALOG_FRAGMENT_CHANGE_STRUCTURE)
            }
        }
    }

    private fun setUploadObserver() {

        appViewModel.uploadResponse.observe(this) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar(binding.progressBar)
                        response.data?.let {
                            if (it.isSuccessful) {

                                if (photoUploadSuccessDialogFragment.isVisible)
                                    return@observe

                                photoUploadSuccessDialogFragment.show(
                                    supportFragmentManager,
                                    "PHOTO_UPLOADED"
                                )
                            } else {
                                it.message?.let { msg -> toast(msg) }
                            }
                        }
                    }

                    is Resource.Error -> {
                        hideProgressBar(binding.progressBar)
                        response.message?.let { message ->
                            toast(message)
                        }
                    }

                    is Resource.Loading -> {
                        showProgressBar(binding.progressBar)
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        with(binding.rvCategories) {
            layoutManager = LinearLayoutManager(this@UploadActivity)
            clipToPadding = false
            clipChildren = false
            adapter = CategoryAdapter(arrayListOf()) { category, image, i ->
                if (list.flatMap { it.photos!! }.any { photo -> photo.isSelected }) {
                    binding.ivChange.show()
                    binding.ivDelete.show()
                } else {
                    binding.ivChange.hide()
                    binding.ivDelete.hide()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onChangeStructure(id: String, title: String) {
        val category = list.find { it.categoryId == id }
        if (category != null) { // we have category exist in list
            val tempList = list.flatMap { it.photos!! }.filter { it.isSelected }
            list.forEach {
                it.photos?.removeIf { image -> image.isSelected }
            }
            category.photos?.addAll(tempList)
        } else { // new category
            val tempList = list.flatMap { it.photos!! }.filter { it.isSelected }
            list.forEach {
                it.photos?.removeIf { image -> image.isSelected }
            }
            list.add(
                Category(
                    categoryId = id,
                    title = title,
                    photos = tempList as ArrayList<Image>
                )
            )
        }
        // reset photo selection
        list.forEach {
            it.photos?.onEach { it.isSelected = false }
        }
        // remove category if there is no photo under it
        list.removeIf { it.photos.isNullOrEmpty() }
        // hide buttons
        binding.ivChange.hide()
        binding.ivDelete.hide()
        // notify changes
        (binding.rvCategories.adapter as CategoryAdapter).addCategories(list)
    }
}