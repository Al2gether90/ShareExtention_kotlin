package com.example.demoapp.models

import android.os.Bundle
import com.gg.gapo.treeviewlib.model.NodeData

data class StructureModel(
    override val nodeViewId: String,
    val name: String? = "",
    val child: List<StructureModel> = emptyList(),
    val categoryId: String? = null
) : NodeData<StructureModel> {
    override fun getNodeChild(): List<StructureModel> {
        return child
    }

    override fun areItemsTheSame(item: NodeData<StructureModel>): Boolean {
        return if (item !is StructureModel) false
        else nodeViewId == item.nodeViewId
    }

    override fun areContentsTheSame(item: NodeData<StructureModel>): Boolean {
        return if (item !is StructureModel) false
        else item.name == name && item.child.size == child.size
    }

    override fun getChangePayload(item: NodeData<StructureModel>): Bundle {
        return Bundle()
    }
}