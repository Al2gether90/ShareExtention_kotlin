package com.example.demoapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demoapp.adapters.AssignmentAdapter
import com.example.demoapp.databinding.ActivityMainBinding
import com.example.demoapp.dialogs.PropertyInfoDialogFragment
import com.example.demoapp.models.Assignment
import com.example.demoapp.network.Resource
import com.example.demoapp.utils.*
import com.example.demoapp.viewmodels.AppViewModel
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolbar()
        handleIntent(intent)
        initRecyclerView()

        setUpClickListeners()

        setUpObserver()

        appViewModel.getAssignments()

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun setUpObserver() {
        appViewModel.assignmentResponse.observe(this) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Success -> {
                        if (binding.swipeRefresh.isRefreshing)
                            binding.swipeRefresh.isRefreshing = false
                        else
                            hideProgressBar(binding.progressBar)

                        response.data?.let {

                            if (it.isEmpty()) return@observe

                            (binding.rvAssignment.adapter as AssignmentAdapter).addAssignments(it)
                        }
                    }

                    is Resource.Error -> {
                        if (binding.swipeRefresh.isRefreshing)
                            binding.swipeRefresh.isRefreshing = false
                        hideProgressBar(binding.progressBar)
                        response.message?.let { message ->
                            toast(message)
                        }
                    }

                    is Resource.Loading -> {
                        if (!binding.swipeRefresh.isRefreshing) {
                            showProgressBar(binding.progressBar)
                        }
                    }
                }
            }
        }

        appViewModel.createAssignmentResponse.observe(this) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar(binding.progressBar)
                        response.data?.let {
                            // TODO: manage response
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
        with(binding.rvAssignment) {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            clipToPadding = false
            clipChildren = false
            adapter = AssignmentAdapter(arrayListOf()) {
                // Recycler View Item Click
                startActivity(
                    Intent(
                        this@MainActivity,
                        ChooseStructureActivity::class.java
                    ).apply {
                        putExtra(EXTRA_ASSIGNMENT, it)
                        putStringArrayListExtra(EXTRA_IMAGES, appViewModel.selectedImages)
                    }
                )
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setUpClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.fabAdd.setOnClickListener {
            PropertyInfoDialogFragment(object : PropertyInfoDialogFragment.OnDialogCloseListener {
                override fun onClose(success: Boolean, assignment: Assignment?) {
                    startActivity(
                        Intent(
                            this@MainActivity,
                            ChooseStructureActivity::class.java
                        ).apply {
                            assignment?.run {
                                putExtra(EXTRA_ASSIGNMENT, this)
                                putStringArrayListExtra(EXTRA_IMAGES, appViewModel.selectedImages)
                            }
                        }
                    )
                }

            }).show(
                supportFragmentManager,
                "PROPERTY_INFO"
            )
        }
        binding.searchView.setOnQueryTextListener(DebouncingQueryTextListener(
            this.lifecycle
        ) { newText ->
            newText?.let {
                if (it.isEmpty()) {
                    (binding.rvAssignment.adapter as AssignmentAdapter).reset()
                } else {
                    (binding.rvAssignment.adapter as AssignmentAdapter).filter.filter(it)
                }
            }
        })

        binding.searchView.setOnClickListener {
            binding.searchView.onActionViewExpanded()
        }

        binding.swipeRefresh.setOnRefreshListener {
            appViewModel.getAssignments()
        }

    }

    private fun handleIntent(intent: Intent) {
        when {
            intent.action == Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("image/") == true) {
                    handleSendImage(intent) // Handle single image being sent
                }
            }
            intent.action == Intent.ACTION_SEND_MULTIPLE
                    && intent.type?.startsWith("image/") == true -> {
                handleSendMultipleImages(intent) // Handle multiple images being sent
            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }
    }

    private fun handleSendImage(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            // Update UI to reflect image being shared
            Log.d(TAG, it.toString())
            appViewModel.selectedImages.add(it.toString())
        }
    }

    private fun handleSendMultipleImages(intent: Intent) {
        intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)?.let {
            // Update UI to reflect multiple images being shared
            it.forEach { uri ->
                Log.d(TAG, "URI ==> $uri")
            }
            appViewModel.selectedImages.addAll(it.map { uri -> uri.toString() })
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    internal class DebouncingQueryTextListener(
        lifecycle: Lifecycle,
        private val onDebouncingQueryTextChange: (String?) -> Unit
    ) : SearchView.OnQueryTextListener, DefaultLifecycleObserver {
        var debouncePeriod: Long = 500

        private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

        private var searchJob: Job? = null

        init {
            lifecycle.addObserver(this)
        }

        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            searchJob?.cancel()
            searchJob = coroutineScope.launch {
                newText?.let {
                    delay(debouncePeriod)
                    onDebouncingQueryTextChange(newText)
                }
            }
            return false
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            searchJob?.cancel()
        }
    }
}