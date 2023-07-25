package com.example.demoapp.activities

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import coil.load
import com.example.demoapp.R
import com.example.demoapp.databinding.ActivityChooseStructureBinding
import com.example.demoapp.models.Assignment
import com.example.demoapp.models.StructureModel
import com.example.demoapp.network.Resource
import com.example.demoapp.utils.*
import com.example.demoapp.viewmodels.AppViewModel
import com.gg.gapo.treeviewlib.GapoTreeView
import com.gg.gapo.treeviewlib.model.NodeViewData
import java.util.*

class ChooseStructureActivity : AppCompatActivity(), GapoTreeView.Listener<StructureModel> {

    private lateinit var binding: ActivityChooseStructureBinding
    private lateinit var treeView: GapoTreeView<StructureModel>

    private var assignment: Assignment? = null

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseStructureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let { intent ->
            assignment = intent.getParcelableExtra(EXTRA_ASSIGNMENT)
            assignment?.let {
                setData(it)
            }
        }

        setObserver()
        initTreeView()
        setUpClickListener()
    }

    private fun setUpClickListener() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_item_next -> {
                    val node = treeView.getSelectedNodes()
                    if (node.isEmpty()) {
                        Toast.makeText(this, "Please select a structure", Toast.LENGTH_SHORT).show()
                    } else {
                        val intent = Intent(this, UploadActivity::class.java)
                        intent.putExtra(EXTRA_STRUCTURE_NAME, node[0].name)
                        intent.putExtra(EXTRA_CATEGORY_ID, node[0].categoryId)
                        intent.putExtra(EXTRA_ASSIGNMENT, assignment)
                        intent.putExtra(
                            EXTRA_IMAGES,
                            getIntent().getStringArrayListExtra(EXTRA_IMAGES)
                        )
                        startActivity(intent)
                    }
                }
            }
            true
        }

        binding.ivShare.setOnClickListener {
            appViewModel.getShortUrl(
                url = assignment?.image,
                profileId = PROFILE_ID,
                assignmentId = assignment?.assignmentId
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(assignment: Assignment) {
        with(binding) {
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

    private fun initTreeView() {
        treeView = GapoTreeView.Builder.plant<StructureModel>(this)
            .withRecyclerView(binding.treeView)
            .withLayoutRes(R.layout.single_node_view_item)
            .setListener(this)
            .setData(prepareStructureModel().toMutableList())
            .showAllNodes(true)
            .build()
    }

    private fun prepareStructureModel(): List<StructureModel> {

        return assignment?.structure?.entries?.mapIndexed { level0Index, level0Entry ->
            StructureModel(
                nodeViewId = "ID_${level0Index + 1}",
                name = level0Entry.value.title,
                categoryId = level0Entry.key,
                child =
                level0Entry.value.categories?.entries?.mapIndexed { level1Index, level1Entry ->
                    StructureModel(
                        nodeViewId = "ID_${level0Index + 1}_${level1Index + 1}",
                        name = level1Entry.value.title,
                        categoryId = level1Entry.value.categoryId,
                        child = level1Entry.value.categories?.entries?.mapIndexed { level2Index, level2Entry ->
                            StructureModel(
                                nodeViewId = "ID_${level0Index + 1}_${level1Index + 1}_${level2Index + 1}",
                                name = level2Entry.value.title,
                                categoryId = level2Entry.value.categoryId,
                                child = emptyList()
                            )
                        } ?: emptyList()
                    )
                } ?: emptyList()
            )
        } ?: emptyList()
    }

    override fun onBind(
        holder: View,
        position: Int,
        item: NodeViewData<StructureModel>,
        bundle: Bundle?
    ) {
        val ivArrow = holder.findViewById<AppCompatImageView>(R.id.iv_arrow)
        val rbCheck = holder.findViewById<AppCompatRadioButton>(R.id.rb_check)
        val tvNode = holder.findViewById<AppCompatTextView>(R.id.tv_department_name)
        val data = item.getData()

        tvNode.text = data.name

        if (item.isLeaf) {
            ivArrow.visibility = View.INVISIBLE
        } else {
            ivArrow.visibility = View.VISIBLE
        }

        rbCheck.isChecked = item.isSelected

        //select node
        rbCheck.setOnClickListener {
            treeView.selectNode(item.nodeId, !item.isSelected) // will trigger onNodeSelected
        }

        //toggle node
        holder.setOnClickListener {
            if (item.isExpanded) {
                treeView.collapseNode(item.nodeId)
            } else {
                treeView.expandNode(item.nodeId)
            }
        }
    }

    override fun onNodeSelected(
        node: NodeViewData<StructureModel>,
        child: List<NodeViewData<StructureModel>>,
        isSelected: Boolean
    ) {
        if (!isSelected) return // prevent unselect node

        treeView.clearNodesSelected()
        treeView.setSelectedNode(listOf(node), isSelected)
        treeView.requestUpdateTree()
    }

    private fun setObserver() {
        appViewModel.shortUrlResponse.observe(this) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar(binding.progressBar)
                        response.data?.let {
                            it.shortLink?.let { url ->
                                copyToClipboard(url)
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

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(UUID.randomUUID().toString(), text)
        clipboard.setPrimaryClip(clip)
        toast("Web upload link copied to clipboard")
    }
}