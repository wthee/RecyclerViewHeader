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