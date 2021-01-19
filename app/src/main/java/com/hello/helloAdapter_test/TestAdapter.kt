package com.hello.helloAdapter_test

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hello.adapter.HelloAdapter
import com.hello.adapter.HelloHolder
import kotlinx.android.synthetic.main.item_layout.view.*

/**
 * @Description:
 * @Author: zhangxiaobai
 * @Date: 2020/8/29 17:22
 */
class TestAdapter(context: Context) : HelloAdapter<String>(context) {

    val aa = 10
    val bb = 20

    override fun getItemViewHelloType(position: Int): Int {
        if (position % 2 == 0) {
            return aa
        }
        return super.getItemViewHelloType(position)
    }

    override fun onCreateViewHelloHolder(parent: ViewGroup, viewType: Int): HelloHolder<String> {
        if (viewType == aa) {
            val view = LayoutInflater.from(context).inflate(R.layout.footer_layout3, parent, false)
            return HelloHolder(view)
        }
        return super.onCreateViewHelloHolder(parent, viewType)
    }

    override fun bindViewHolder(holder: HelloHolder<String>, data: MutableList<String>, position: Int) {
        if (holder.itemViewType != aa) {
            holder.setItemBackgroudColor(Color.parseColor("#23ee43"))
            holder.itemView.item_layout_tv.text = data[position]
        }
    }

}