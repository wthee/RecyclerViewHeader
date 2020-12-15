#### 实现效果
![效果](http://wthee.xyz/wp-content/uploads/2020/12/S01215-100007451-193x300.png)
#### 源码地址

- https://github.com/wthee/RecyclerViewHeader

#### 布局文件

- activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/list"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintTop_toTopOf="parent"
        android:orientation="vertical"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
        tools:listitem="@layout/item_content" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

- item_header.xml 列表分组头部

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.textview.MaterialTextView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/header"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:gravity="start"
    android:textAppearance="@style/TextAppearance.MaterialComponents.Headline5"
    app:layout_constraintTop_toTopOf="parent"
    tools:text="header" />
```

- item_content.xml 列表项

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.textview.MaterialTextView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/content"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:gravity="center"
    tools:text="content"
    app:layout_constraintTop_toTopOf="parent" />
```

#### 数据类

- MockData

```kotlin
package cn.wthee.recyclerviewheader.data

import java.util.*
import kotlin.collections.ArrayList

data class MockData(
    val type: String,
    val contents: List<MockContent>
)

data class MockContent(
    val id: Long,
    val data: String
)

// 模拟数据
fun loadData(): ArrayList<MockData> {
    val list = arrayListOf<MockData>()
    for (i in 0..20) {
        val contents = arrayListOf<MockContent>()
        for (j in 0 .. Random(System.currentTimeMillis()).nextInt(20)) {
            contents.add(MockContent(System.currentTimeMillis(), "内容${UUID.randomUUID()}"))
        }
        list.add(MockData("头部$i", contents))
    }
    return list
}
```

#### 列表项密封类

```kotlin
package cn.wthee.recyclerviewheader.data

sealed class ListItem {

    abstract val id: Long

    data class Content(val content: MockContent) : ListItem() {
        override val id = content.id
    }

    data class Header(val header: String) : ListItem() {
        override val id = Long.MIN_VALUE
    }
}

```

#### 列表适配器

```kotlin
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

```

#### 加载数据

```kotlin
package cn.wthee.recyclerviewheader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.wthee.recyclerviewheader.adapter.HeaderListAdapter
import cn.wthee.recyclerviewheader.data.loadData
import cn.wthee.recyclerviewheader.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = HeaderListAdapter()
        binding.list.adapter = adapter
        adapter.addHeaderAndSubmitList(loadData())
    }

}
```

