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
