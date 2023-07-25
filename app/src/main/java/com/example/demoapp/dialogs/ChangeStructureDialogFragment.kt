package com.example.demoapp.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.demoapp.R
import com.example.demoapp.databinding.ChangeStructureDialogFragmentBinding
import com.example.demoapp.models.Assignment
import com.example.demoapp.models.StructureModel
import com.example.demoapp.utils.EXTRA_ASSIGNMENT
import com.example.demoapp.utils.EXTRA_FILE_COUNT
import com.example.demoapp.utils.toast
import com.gg.gapo.treeviewlib.GapoTreeView
import com.gg.gapo.treeviewlib.model.NodeViewData

class ChangeStructureDialogFragment : DialogFragment(), GapoTreeView.Listener<StructureModel> {

    private var _binding: ChangeStructureDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var treeView: GapoTreeView<StructureModel>

    private var assignment: Assignment? = null
    private var selectedFileCount: Int = 1

    private var onStructureChangeListener: OnStructureChangeListener? = null

    companion object {
        @JvmStatic
        fun newInstance(
            assignment: Assignment,
            selectedFileCount: Int,
            onStructureChangeListener: OnStructureChangeListener
        ) = ChangeStructureDialogFragment().apply {
            this.onStructureChangeListener = onStructureChangeListener
            arguments =
                bundleOf(
                    EXTRA_ASSIGNMENT to assignment,
                    EXTRA_FILE_COUNT to selectedFileCount
                )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        _binding =
            ChangeStructureDialogFragmentBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.60).toInt()
        dialog!!.window?.setLayout(width, height)
        isCancelable = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpClickListener()

        arguments?.let {
            assignment = it.getParcelable(EXTRA_ASSIGNMENT)
            selectedFileCount = it.getInt(EXTRA_FILE_COUNT)
            assignment?.let {
                initTreeView()
            }
        }
        with(binding) {
            if (selectedFileCount > 1) {
                tvFileCount.text = "$selectedFileCount files"
            } else {
                tvFileCount.text = "$selectedFileCount file"
            }
        }
    }

    private fun setUpClickListener() {

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnChange.setOnClickListener {
            val node = treeView.getSelectedNodes()
            if (node.isEmpty()) {
                toast("Please select a structure")
            } else {
                if (onStructureChangeListener != null) {
                    onStructureChangeListener!!.onChangeStructure(
                        node[0].categoryId!!,
                        node[0].name!!
                    )
                }
                dismiss()
            }
        }
    }

    private fun initTreeView() {
        treeView = GapoTreeView.Builder.plant<StructureModel>(requireContext())
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

}

interface OnStructureChangeListener {
    fun onChangeStructure(id: String, title: String)
}