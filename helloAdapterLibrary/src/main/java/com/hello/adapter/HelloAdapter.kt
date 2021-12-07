package com.hello.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.contains
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * @Description:
 * @Author: zhangxiaobai
 * @Date: 2020/8/29 16:14
 */
abstract class HelloAdapter<T>(var context: Context) : RecyclerView.Adapter<HelloHolder<T>>() {
    // 显示数据的布局id
    private var layoutId = 0

    // 空数据要传的布局id
    private var emptyLayoutId = 0

    // 要监听的item上的viewid集合
    private var listenerViewsIds: List<Int>? = null

    // 自定义的一个String类型的数据
    private var customData: String? = null

    // 是否显示尾部布局
    var isShowFooterLayout = false

    // 是否显示头部布局
    var isShowHeadLayout = false

    // 无数据时是否显示暂无数据提示页面
    var isShowEmptyLayout = true

    // 头布局view
    private val HEADER_VIEW = 0x01

    // 尾布局view
    private val FOOTER_VIEW = 0x02

    // 数据填充的view
    private val DATA_VIEW = 0x03

    // 空数据显示的view
    private val EMPTY_VIEW = 0x04

    // 空数据的viewHolder
    private var emptyHolder: HelloHolder<T>? = null

    // 头部view的父布局
    private var headerViewParentLayout: LinearLayout? = null

    // 尾部view的父布局
    private var footerViewParentLayout: LinearLayout? = null

    private var baseData: MutableList<T> = ArrayList()
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemClickForDataListener: OnItemClickForDataListener<T>? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null
    private var onItemLongClickForDataListener: OnItemLongClickForDataListener<T>? = null
    private var onHeadAndFootClickListener: OnHeadAndFootClickListener? = null


    override fun getItemCount(): Int {

        var count = baseData.size

        if (isShowHeadLayout) {
            count++
        }
        if (isShowFooterLayout) {
            count++
        }
        if (baseData.size == 0) {
            if (isShowEmptyLayout) {
                count++
            }
        }
        return count
    }

    override fun getItemViewType(position: Int): Int {

        if (isShowHeadLayout && 0 == position) {
            return HEADER_VIEW
        }
        if (isShowEmptyLayout && baseData.size == 0) {
            if (isShowHeadLayout) {
                if (1 == position) {
                    return EMPTY_VIEW
                }
            } else {
                if (0 == position) {
                    return EMPTY_VIEW
                }
            }
        }

        return if (isShowFooterLayout) {
            if (position < itemCount - 1) {
                getItemViewHelloType(position)
            } else {
                FOOTER_VIEW
            }
        } else {
            getItemViewHelloType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelloHolder<T> {
        when (viewType) {
            HEADER_VIEW -> {
                if (null == headerViewParentLayout) {
                    initHeaderView()
                } else {
                    val headerViewParent = headerViewParentLayout!!.parent
                    if (headerViewParent is ViewGroup) {
                        headerViewParent.removeView(headerViewParentLayout)
                    }
                }
                return HelloHolder(headerViewParentLayout!!)
            }
            EMPTY_VIEW -> {
                val emptyLayout = if (emptyLayoutId == 0) {
                    R.layout.item_empty_layout_default
                } else {
                    emptyLayoutId
                }
                val emptyView: View =
                    LayoutInflater.from(context).inflate(emptyLayout, parent, false)
                emptyHolder = HelloHolder(emptyView)
                return emptyHolder!!
            }
            FOOTER_VIEW -> {
                if (null == footerViewParentLayout) {
                    initFooterView()
                } else {
                    val footerViewParent = footerViewParentLayout!!.parent
                    if (footerViewParent is ViewGroup) {
                        footerViewParent.removeView(footerViewParentLayout)
                    }
                }
                return HelloHolder(footerViewParentLayout!!)
            }
            else -> {
                return onCreateViewHelloHolder(parent, viewType)
            }
        }
    }

    override fun onBindViewHolder(holder: HelloHolder<T>, position: Int) {
        try {
            if (holder.itemViewType != HEADER_VIEW && holder.itemViewType != FOOTER_VIEW && holder.itemViewType != EMPTY_VIEW) {

                var count = 0
                if (isShowHeadLayout) {
                    count++
                }
                if (baseData.size == 0) {
                    if (isShowEmptyLayout) {
                        count++
                    }
                }
                val dataPosition = position - count

                if (dataPosition < baseData.size) {
                    bindViewHolder(holder, baseData, dataPosition)
                    setOnClick(holder.itemView, dataPosition)
                    setOnLongClick(holder.itemView, dataPosition)
                    if (null != listenerViewsIds && listenerViewsIds!!.isNotEmpty()) {
                        for (viewId in listenerViewsIds!!) {
                            val cv: View? = holder.getView(viewId)
                            if (cv != null) {
                                if (null != onItemClickListener || null != onItemClickForDataListener) {
                                    setOnClick(cv, dataPosition)
                                }
                                if (null != onItemLongClickListener || null != onItemLongClickForDataListener) {
                                    setOnLongClick(cv, dataPosition)
                                }
                            }
                        }
                    }
                    holder.bindViewData(baseData, dataPosition)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBindViewHolder(
        holder: HelloHolder<T>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        onBindViewHelloHolder(holder, position, payloads)
    }

    /**
     *
     */
    open fun onBindViewHelloHolder(
        holder: HelloHolder<T>,
        position: Int,
        payloads: MutableList<Any>
    ) {

    }

    /**
     * 多布局时，自定义布局类型
     */
    open fun getItemViewHelloType(position: Int): Int {
        return DATA_VIEW
    }

    /**
     * 多布局时，通过viewType创建不同的HelloHolder
     */
    open fun onCreateViewHelloHolder(parent: ViewGroup, viewType: Int): HelloHolder<T> {
        val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
        return HelloHolder(view)
    }

    /**
     * 设置data填充的布局
     */
    open fun setLayoutId(layoutId: Int): HelloAdapter<T> {
        this.layoutId = layoutId
        return this
    }

    /**
     * 设置空数据时显示的暂无数据布局（不设置显示默认布局）
     */
    fun setEmptyLayoutId(emptyLayoutId: Int): HelloAdapter<T> {
        this.emptyLayoutId = emptyLayoutId
        return this
    }

    /**
     * 数据data为空时，是否显示空数据的页面（）
     */
    fun isShowEmptyLayout(isShow: Boolean): HelloAdapter<T> {
        isShowEmptyLayout = isShow
        notifyDataSetChanged()
        return this
    }

    /**
     * 是否显示尾部布局
     */
    fun isShowFooter(isShow: Boolean): HelloAdapter<T> {
        isShowFooterLayout = isShow
        notifyDataSetChanged()
        return this
    }

    /**
     * 是否显示头部布局
     */
    fun isShowHeader(isShow: Boolean): HelloAdapter<T> {
        isShowHeadLayout = isShow
        notifyDataSetChanged()
        return this
    }

    /**
     * 设置监听item上的view点击事件（传参view的id集合）
     */
    fun setListenerViewsIds(listenerViewsIds: MutableList<Int>?): HelloAdapter<T> {
        this.listenerViewsIds = listenerViewsIds
        return this
    }

    /**
     * 一个自定义的扩展字段String
     */
    fun setCustomData(customData: String?): HelloAdapter<T> {
        this.customData = customData
        return this
    }

    /**
     * 设置数据集合（覆盖原数据）
     */
    open fun setData(data: MutableList<T>?): HelloAdapter<T> {
        if (data != null) {
            this.baseData = data
            notifyDataSetChanged()
        }
        return this
    }

    /**
     * 添加数据（在原数据基础上添加数据）
     *
     * @param data
     */
    open fun addData(data: T) {
        if (null != data) {
            baseData.add(data)
        }
        notifyDataSetChanged()
    }

    /**
     * 添加数据集（在原数据基础上添加数据）
     * @param data
     */
    open fun addData(data: MutableList<T>) {
        baseData.addAll(data)
        notifyDataSetChanged()
    }

    /**
     * 获取所有的数据集合
     */
    open fun getData(): MutableList<T> {
        return baseData
    }

    /**
     * 获取所有的尾布局中添加的子view
     */
    open fun getAllFooterView(): Sequence<View>? {
        return footerViewParentLayout?.children
    }

    /**
     * 获取所有的头布局中添加的子view
     */
    open fun getAllHeaderView(): Sequence<View>? {
        return headerViewParentLayout?.children
    }

    /**
     * 获取头布局中添加的子view数量
     */
    open fun getHeaderViewCount(): Int? {
        return headerViewParentLayout?.childCount
    }

    /**
     * 获取尾布局中添加的子view数量
     */
    open fun getFooterViewCount(): Int? {
        return footerViewParentLayout?.childCount
    }

    /**
     * 添加尾布局
     *
     * @param layoutId
     * @return 添加尾的view
     */
    open fun addFooterView(layoutId: Int): View {
        return addFooterView(layoutId, -1)
    }

    /**
     * 在指定位置添加尾布局
     *
     * @param layoutId
     * @param index 放置尾布局view的下标
     * @return 添加尾的view
     */
    open fun addFooterView(layoutId: Int, index: Int): View {
        if (null == footerViewParentLayout) {
            initFooterView()
        }
        val foot =
            LayoutInflater.from(context).inflate(layoutId, footerViewParentLayout, false)
        footerViewParentLayout!!.addView(foot)
        if (!isShowFooterLayout && getFooterViewCount()!! > 0) {
            isShowFooterLayout = true
            notifyDataSetChanged()
        }
        return foot
    }

    /**
     * 添加尾部布局并给尾布局的子view设置点击事件
     *
     * @param layoutId 布局id
     * @param viewIds 设置点击事件的view的id
     * @return 添加尾的view
     */
    open fun addFooterAndClickListener(layoutId: Int, viewIds: MutableList<Int>): View? {
        val foot: View = addFooterView(layoutId)
        for (viewId in viewIds) {
            val view: View = foot.findViewById(viewId)
            view.setOnClickListener {
                if (null != onHeadAndFootClickListener) {
                    onHeadAndFootClickListener!!.headAndFootClick(view)
                }
            }
        }
        return foot
    }

    /**
     * 添加头布局
     *
     * @param layoutId
     * @return 添加头的view
     */
    open fun addHeaderView(layoutId: Int): View {
        return addHeaderView(layoutId, -1)
    }

    /**
     * 在指定位置添加头布局
     *
     * @param layoutId 布局id
     * @param index 放置头布局view的下标
     * @return 添加头的view
     */
    open fun addHeaderView(layoutId: Int, index: Int): View {
        isShowHeadLayout = true
        if (null == headerViewParentLayout) {
            initHeaderView()
        }
        val header =
            LayoutInflater.from(context).inflate(layoutId, headerViewParentLayout, false)
        headerViewParentLayout!!.addView(header, index)
        notifyDataSetChanged()
        return header
    }

    /**
     * 添加头部布局并给头布局的子view设置点击事件
     *
     * @param layoutId 头部布局id
     * @param viewIds 设置点击事件的view的id
     * @return 添加头的view
     */
    open fun addHeaderAndClickListener(layoutId: Int, viewIds: MutableList<Int>): View {
        val header = addHeaderView(layoutId)
        for (viewId in viewIds) {
            val view: View = header.findViewById(viewId)
            view.setOnClickListener {
                if (null != onHeadAndFootClickListener) {
                    onHeadAndFootClickListener!!.headAndFootClick(view)
                }
            }
        }
        return header
    }

    /**
     * 移除指定的尾布局view
     *
     * @param view
     */
    open fun removeFooterView(view: View?) {
        if (null != footerViewParentLayout) {
            footerViewParentLayout!!.removeView(view)
            if (getFooterViewCount() == 0) {
                if (isShowFooterLayout) {
                    isShowFooterLayout = false
                }
            }
        }
    }

    /**
     * 移除指定的尾布局view
     *
     * @param viewIndex（添加的View下标值）
     */
    open fun removeFooterView(viewIndex: Int?) {
        if (null != footerViewParentLayout && null != viewIndex) {
            footerViewParentLayout!!.removeViewAt(viewIndex)
            if (getFooterViewCount() == 0) {
                if (isShowFooterLayout) {
                    isShowFooterLayout = false
                }
            }
        }
    }

    /**
     * 移除所有尾布局View
     */
    open fun removeAllFooterView() {
        if (null != footerViewParentLayout) {
            footerViewParentLayout!!.removeAllViews()
            if (getFooterViewCount() == 0) {
                if (isShowFooterLayout) {
                    isShowFooterLayout = false
                }
            }
        }
    }

    /**
     * 移除指定的头布局View
     *
     * @param view
     */
    open fun removeHeaderView(view: View?) {
        if (null != headerViewParentLayout) {
            headerViewParentLayout!!.removeView(view)
        }
        checkHeaderChildCount()
    }

    /**
     * 根据view下标移除头布局View
     *
     * @param viewIndex（添加的View下标值）
     */
    open fun removeHeaderView(viewIndex: Int?) {
        if (null != headerViewParentLayout && null != viewIndex) {
            headerViewParentLayout!!.removeViewAt(viewIndex)
        }
        checkHeaderChildCount()
    }

    /**
     * 移除所有的头布局View
     */
    open fun removeAllHeaderView() {
        if (null != headerViewParentLayout) {
            headerViewParentLayout!!.removeAllViews()
        }
        checkHeaderChildCount()
    }

    /**
     * 检查头部view的子view数量，如果为0设置不显示头布局
     */
    private fun checkHeaderChildCount() {
        if (getHeaderViewCount() == 0) {
            isShowHeadLayout = false
            notifyDataSetChanged()
        }
    }

    /**
     * 初始化头部布局
     */
    private fun initHeaderView() {
        headerViewParentLayout = LinearLayout(context)
        headerViewParentLayout!!.orientation = LinearLayout.VERTICAL
        val layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        headerViewParentLayout!!.layoutParams = layoutParams
    }

    /**
     * 初始化底部布局
     */
    private fun initFooterView() {
        if (null == footerViewParentLayout) {
            footerViewParentLayout = LinearLayout(context)
            footerViewParentLayout!!.orientation = LinearLayout.VERTICAL
            val layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            footerViewParentLayout!!.layoutParams = layoutParams
        }
    }

    protected abstract fun bindViewHolder(
        holder: HelloHolder<T>,
        data: MutableList<T>,
        position: Int
    )

    /**
     * 设置头和尾view的监听事件
     */
    fun setOnHeadAndFootClickListener(onHeadAndFootClick: OnHeadAndFootClickListener?) {
        this.onHeadAndFootClickListener = onHeadAndFootClick
    }

    interface OnHeadAndFootClickListener {
        fun headAndFootClick(view: View?)
    }

    interface OnItemClickListener {
        fun itemClick(view: View?, position: Int)
    }

    interface OnItemClickForDataListener<T> {
        fun itemClickForData(view: View?, position: Int, data: T)
    }

    interface OnItemLongClickListener {
        fun itemLongClick(view: View?, position: Int)
    }

    interface OnItemLongClickForDataListener<T> {
        fun itemLongClickForData(view: View?, position: Int, data: T)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnItemClickForDataListener(onItemClickForDataListener: OnItemClickForDataListener<T>) {
        this.onItemClickForDataListener = onItemClickForDataListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener?) {
        this.onItemLongClickListener = onItemLongClickListener
    }

    fun setOnItemLongClickForDataListener(onItemLongClickForDataListener: OnItemLongClickForDataListener<T>) {
        this.onItemLongClickForDataListener = onItemLongClickForDataListener
    }

    private fun setOnClick(view: View, position: Int) {
        view.setOnClickListener {
            try {
                if (null != onItemClickListener) {
                    onItemClickListener!!.itemClick(view, position)
                }
                if (null != onItemClickForDataListener) onItemClickForDataListener!!.itemClickForData(
                    view,
                    position,
                    baseData[position]
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setOnLongClick(v: View, position: Int) {
        v.setOnLongClickListener {
            try {
                if (null != onItemLongClickListener) {
                    onItemLongClickListener!!.itemLongClick(v, position)
                }
                if (null != onItemLongClickForDataListener) {
                    onItemLongClickForDataListener!!.itemLongClickForData(
                        v,
                        position,
                        baseData[position]
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            true
        }
    }
}