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