package com.hello.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.*

/**
 * @Description:
 * @Author: zhangxiaobai
 * @Date: 2020/8/29 16:15
 */
class HelloHolder(itemView: View) : ViewHolder(itemView) {
    private var sa: SparseArray<View> = SparseArray()

    fun  getView(viewId: Int): View? {
        var view = sa[viewId]
        if (null == view) {
            view = itemView.findViewById(viewId)
            sa.put(viewId, view)
        }
        return view
    }

    fun setItemBackgroud(drawable: Drawable): HelloHolder {
        itemView.background = drawable
        return this
    }

    fun setItemBackgroudColor(@ColorInt color: Int): HelloHolder {
        itemView.setBackgroundColor(color)
        return this
    }

    fun setItemBackgroudResource(resid: Int): HelloHolder {
        itemView.setBackgroundResource(resid)
        return this
    }

    fun setText(viewId: Int, text: String): HelloHolder {
        val view = getView(viewId) as TextView
        view.text = text
        return this
    }

    fun setText(viewId: Int, stringId: Int): HelloHolder {
        val view = getView(viewId) as TextView
        view.setText(stringId)
        return this
    }

    fun setTextColor(viewId: Int, colorId: Int): HelloHolder {
        val view = getView(viewId) as TextView
        view.setTextColor(colorId)
        return this
    }

    fun setImgResource(viewId: Int, imgId: Int): HelloHolder {
        val view = getView(viewId) as ImageView
        view.setImageResource(imgId)
        return this
    }

    fun setImgBitmap(viewId: Int, bitmap: Bitmap): HelloHolder {
        val view = getView(viewId) as ImageView
        view.setImageBitmap(bitmap)
        return this
    }

    fun setImgDrawable(viewId: Int, drawable: Drawable): HelloHolder {
        val view = getView(viewId)  as ImageView
        view.setImageDrawable(drawable)
        return this
    }

    fun setViewVisibility(viewId: Int, isVisibility: Boolean): HelloHolder {
        val view = getView(viewId)
        view?.visibility = if (isVisibility) View.VISIBLE else View.GONE
        return this
    }

    fun setVisibility(visibility: Boolean) {
        val param = itemView.layoutParams as RecyclerView.LayoutParams
        if (visibility) {
            param.height = LinearLayout.LayoutParams.MATCH_PARENT
            param.width = LinearLayout.LayoutParams.MATCH_PARENT
        } else {
            param.height = 0
            param.width = 0
        }
        itemView.layoutParams = param
    }

    fun bindData(data: Any) {}

}