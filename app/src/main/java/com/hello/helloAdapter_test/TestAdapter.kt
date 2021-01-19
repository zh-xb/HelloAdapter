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

    private val LAYOUT1 = 10
    private val LAYOUT2 = 20

    override fun getItemViewHelloType(position: Int): Int {
        if (position % 2 == 0) {
            return LAYOUT1
        } else if (position == 3 || position == 5 || position == 7 || position == 9){
            return LAYOUT2
        }
        return super.getItemViewHelloType(position)
    }

    override fun onCreateViewHelloHolder(parent: ViewGroup, viewType: Int): HelloHolder<String> {
        if (viewType == LAYOUT1) {
            val view = LayoutInflater.from(context).inflate(R.layout.holder_layout1, parent, false)
            return LayoutHolder1(view)
        } else if (viewType == LAYOUT2) {
            val view = LayoutInflater.from(context).inflate(R.layout.holder_layout2, parent, false)
            return LayoutHolder2(view)
        }
        return super.onCreateViewHelloHolder(parent, viewType)
    }

    override fun bindViewHolder(holder: HelloHolder<String>, data: MutableList<String>, position: Int) {
        if (holder.itemViewType != LAYOUT1&&holder.itemViewType != LAYOUT2) {
            holder.setItemBackgroudColor(Color.parseColor("#23ee43"))
            holder.itemView.item_layout_tv.text = data[position]
        }
    }

}