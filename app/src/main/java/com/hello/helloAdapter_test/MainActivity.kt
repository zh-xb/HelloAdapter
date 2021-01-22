package com.hello.helloAdapter_test

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hello.adapter.HelloAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener,
    HelloAdapter.OnItemClickForDataListener<String>, HelloAdapter.OnHeadAndFootClickListener {

    var TAG = "MainActivity❤❤❤❤"
    var data: MutableList<String> = ArrayList()
    var headers: MutableList<View> = ArrayList()
    var footers: MutableList<View> = ArrayList()
    private var adapter: HelloAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for (i in 0..14) {
            data.add("原始数据$i")
        }

        adapter = TestAdapter(this)
            .setLayoutId(R.layout.item_layout)
            .setData(data)
            .setEmptyLayoutId(R.layout.item_empty_layout_a)
            .showEmptyLayout(true)
//        val header: View = adapter?.addHeaderView(R.layout.header_layout3)!!
        var layoutIds: MutableList<Int> = arrayListOf()
        layoutIds.add(R.id.head_bt1)
        layoutIds.add(R.id.head_bt2)
        layoutIds.add(R.id.head_bt3)
        val header: View = adapter?.addHeaderAndClickListener(R.layout.header_layout3, layoutIds)!!
        headers.add(header)
        headerType++

        val llm =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        test_rv.layoutManager = llm
        test_rv.adapter = adapter

        other_bt.setOnClickListener(this)
        add_header.setOnClickListener(this)
        add_footer.setOnClickListener(this)
        remove_header.setOnClickListener(this)
        remove_footer.setOnClickListener(this)
        add_data.setOnClickListener(this)
        adapter?.setOnItemClickForDataListener(this)
        adapter?.setOnHeadAndFootClickListener(this)

        var views: Sequence<View>? = adapter?.getAllHeaderView();
        Log.i(TAG, "getAllFooterView：" + views?.count())
    }

    var headerType = 0
    var footerType = 0
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.add_header -> {
                val header: View = (if (headerType % 2 == 0) {
                    adapter?.addHeaderView(R.layout.header_layout2)
                } else {
                    var layoutIds: MutableList<Int> = arrayListOf()
                    layoutIds.add(R.id.head_bt1)
                    layoutIds.add(R.id.head_bt2)
                    layoutIds.add(R.id.head_bt3)
                    adapter?.addHeaderAndClickListener(R.layout.header_layout3, layoutIds)!!
                })!!
                headers.add(header)
                headerType++

                var views: Sequence<View>? = adapter?.getAllHeaderView();
                Log.i(TAG, "getAllHeaderView：" + views?.count())
                Log.i(TAG, "getHeaderViewCount：" + adapter?.getHeaderViewCount())
            }
            R.id.remove_header -> {
                if (headerType > 0) {
                    headerType--
                    adapter?.removeHeaderView(headers[headerType])
                    headers.removeAt(headerType);
                }
                var views: Sequence<View>? = adapter?.getAllHeaderView();
                Log.i(TAG, "getAllHeaderView：" + views?.count())
                Log.i(TAG, "getHeaderViewCount：" + adapter?.getHeaderViewCount())
            }
            R.id.add_footer -> {
                val footer: View = (if (footerType % 2 == 0) {
                    adapter?.addFooterView(R.layout.footer_layout2)
                } else {
                    adapter?.addFooterView(R.layout.footer_layout3)
                })!!
                footers.add(footer)
                footerType++
                var views: Sequence<View>? = adapter?.getAllFooterView();
                Log.i(TAG, "getAllFooterView：" + views?.count())
                Log.i(TAG, "getFooterViewCount：" + adapter?.getFooterViewCount())
            }
            R.id.remove_footer -> {
                if (footerType > 0) {
                    footerType--
                    adapter?.removeFooterView(footers[footerType])
                    footers.removeAt(footerType);
                }
                var views: Sequence<View>? = adapter?.getAllFooterView();
                Log.i(TAG, "getAllFooterView：" + views?.count())
                Log.i(TAG, "getFooterViewCount：" + adapter?.getFooterViewCount())
            }
            R.id.add_data -> {
                data.add("新增数据")
                adapter?.setData(data)
            }
            R.id.other_bt -> {
                data.clear()
                adapter?.setData(data)
            }
            else -> {
            }
        }
    }

    override fun itemClickForData(view: View?, position: Int, data: String) {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show()
    }

    override fun headAndFootClick(view: View?) {
        when (view?.id) {
            R.id.head_bt1 -> {
                Toast.makeText(this, "我是button1", Toast.LENGTH_SHORT).show()
            }
            R.id.head_bt2 -> {
                Toast.makeText(this, "我是button2", Toast.LENGTH_SHORT).show()
            }
            R.id.head_bt3 -> {
                Toast.makeText(this, "我是button3", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
