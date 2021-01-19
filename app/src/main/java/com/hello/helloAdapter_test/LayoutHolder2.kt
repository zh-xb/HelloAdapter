package com.hello.helloAdapter_test

import android.graphics.Color
import android.view.View
import com.hello.adapter.HelloHolder
import kotlinx.android.synthetic.main.footer_layout3.view.*
import kotlinx.android.synthetic.main.holder_layout2.view.*

/**
 * @Description:
 * @Author: zhangxiaobai
 * @Date: 2021/1/19 19:06
 */
class LayoutHolder2(itemView: View) : HelloHolder<String>(itemView) {

    override fun bindViewData(data: MutableList<String>, position: Int) {
        super.bindViewData(data, position)

        this.itemView.holder2_item_layout_tv.text = "holder2："+data[position]
    }
}