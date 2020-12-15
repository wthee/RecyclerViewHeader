package cn.wthee.recyclerviewheader.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import cn.wthee.recyclerviewheader.data.ListItem
import cn.wthee.recyclerviewheader.data.MockContent
import cn.wthee.recyclerviewheader.data.MockData
import cn.wthee.recyclerviewheader.databinding.ItemContentBinding
import cn.wthee.recyclerviewheader.databinding.ItemHeaderBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HeaderListAdapter : ListAdapter<ListItem, HeaderListAdapter.ViewHolder>(DiffCallback()) {

    private val ITEM_VIEW_TYPE_HEADER = 0
    private val ITEM_VIEW_TYPE_ITEM = 1
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list: List<MockData>?) {
        adapterScope.launch {
            val items = when {
                //无数据时
                list == null || list.isEmpty() -> listOf(ListItem.Header("头部为空")) + listOf(
                    ListItem.Content(
                        MockContent(-1, "暂无")
                    )
                )
                //处理数据
                else -> {
                    val datas = arrayListOf<ListItem>()
                    list.forEach {
                        //添加头部
                        datas.add(ListItem.Header(it.type))
                        //遍历内容，并添加
                        it.contents.forEach { content ->
                            datas.add(ListItem.Content(content))
                        }
                    }
                    datas
                }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> ViewHolder(
                ItemHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            ITEM_VIEW_TYPE_ITEM -> ViewHolder(
                ItemContentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem) {
            when (item) {
                //内容
                is ListItem.Content -> {
                    (binding as ItemContentBinding).apply {
                        content.text = item.content.data
                    }
                }
                //头部
                is ListItem.Header -> {
                    (binding as ItemHeaderBinding).apply {
                        header.text =item.header
                    }
                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.Header -> ITEM_VIEW_TYPE_HEADER
            is ListItem.Content -> ITEM_VIEW_TYPE_ITEM
        }
    }
}

private class DiffCallback : DiffUtil.ItemCallback<ListItem>() {

    override fun areItemsTheSame(
        oldItem: ListItem,
        newItem: ListItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ListItem,
        newItem: ListItem
    ): Boolean {
        return oldItem == newItem
    }
}
