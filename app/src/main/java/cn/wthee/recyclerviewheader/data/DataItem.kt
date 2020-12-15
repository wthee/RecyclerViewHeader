package cn.wthee.recyclerviewheader.data

sealed class DataItem {

    abstract val id: Long

    data class Content(val content: MockContent) : DataItem() {
        override val id = content.id
    }

    data class Header(val header: String) : DataItem() {
        override val id = Long.MIN_VALUE
    }
}
