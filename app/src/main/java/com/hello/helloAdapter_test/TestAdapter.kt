package com.hello.helloAdapter_test

import android.content.Context
import android.graphics.Color
import com.hello.adapter.HelloAdapter
import com.hello.adapter.HelloHolder
import kotlinx.android.synthetic.main.item_layout.view.*

/**
 * @Description:
 * @Author: zhangxiaobai
 * @Date: 2020/8/29 17:22
 */
class TestAdapter(context: Context) : HelloAdapter<String>(context) {
    override fun bindViewHolder(holder: HelloHolder, data: MutableList<String>, position: Int) {
        holder.setItemBackgroudColor(Color.parseColor("#23ee43"))
        holder.itemView.item_layout_tv.text = data[position]
    }

}