package com.hello.helloAdapter_test

import android.graphics.Color
import android.view.View
import com.hello.adapter.HelloHolder
import kotlinx.android.synthetic.main.holder_layout1.view.*

/**
 * @Description:
 * @Author: zhangxiaobai
 * @Date: 2021/1/19 19:06
 */
class LayoutHolder1(itemView: View) : HelloHolder<String>(itemView) {

    override fun bindViewData(data: MutableList<String>, position: Int) {
        super.bindViewData(data, position)

        setItemBackgroudColor(Color.parseColor("#edea23"))
        this.itemView.holder1_item_layout_tv.text = "holder1："+data[position]

    }
}