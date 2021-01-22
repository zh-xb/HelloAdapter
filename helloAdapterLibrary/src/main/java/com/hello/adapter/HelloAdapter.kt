package com.hello.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
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
    private var baseData: MutableList<T> = ArrayList<T>()

    // 要监听的item上的viewid集合
    private var listenerViewsIds: List<Int>? = null

    // 自定义的一个String类型的数据
    private var customData: String? = null

    // 无数据时是否显示暂无数据提示页面
    private var showEmptyLayout = true
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemClickForDataListener: OnItemClickForDataListener<T>? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null
    private var onItemLongClickForDataListener: OnItemLongClickForDataListener<T>? = null
    private var onHeadAndFootClickListener: OnHeadAndFootClickListener? = null

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


    override fun getItemCount(): Int {
        // 增加 头、尾和空数据3个固定布局
        return baseData.size + 3
    }

    override fun getItemViewType(position: Int): Int {
        if (0 == position) {
            return HEADER_VIEW
        }
        if (1 == position) {
            return EMPTY_VIEW
        }
        return if (position < itemCount - 1) {
            getItemViewHelloType(position - 2)
        } else {
            FOOTER_VIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelloHolder<T> {
        when {
            HEADER_VIEW == viewType -> {
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
            EMPTY_VIEW == viewType -> {
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
            FOOTER_VIEW == viewType -> {
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
            if (holder.itemViewType == EMPTY_VIEW && null != emptyHolder) { // 数据为空的布局
                if (showEmptyLayout && baseData.size == 0) {
                    emptyHolder!!.setVisibility(true)
                } else {
                    emptyHolder!!.setVisibility(false)
                }
            } else if (holder.itemViewType != HEADER_VIEW && holder.itemViewType != FOOTER_VIEW && holder.itemViewType != EMPTY_VIEW) {
                val dataPosition = position - 2
                if (dataPosition < baseData.size) {
                    bindViewHolder(holder, baseData, dataPosition)
                    setOnClick(holder.itemView, dataPosition)
                    setOnLongClick(holder.itemView, dataPosition)
                    if (null != listenerViewsIds && listenerViewsIds!!.isNotEmpty()) {
                        for (viewId in listenerViewsIds!!) {
                            val cv: View? = holder.getView(viewId)
                            if (cv != null) {
                                setOnClick(cv, dataPosition)
                                setOnLongClick(cv, dataPosition)
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
    fun showEmptyLayout(isShow: Boolean): HelloAdapter<T> {
        showEmptyLayout = isShow
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
        return foot
    }

    /**
     * 添加尾布局并给尾布局的子view设置点击事件
     *
     * @param layoutId
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
     * @param layoutId
     * @param index 放置头布局view的下标
     * @return 添加头的view
     */
    open fun addHeaderView(layoutId: Int, index: Int): View {
        if (null == headerViewParentLayout) {
            initHeaderView()
        }
        val header =
            LayoutInflater.from(context).inflate(layoutId, headerViewParentLayout, false)
        headerViewParentLayout!!.addView(header, index)
        return header
    }

    /**
     * 添加头布局并给头布局的子view设置点击事件
     *
     * @param layoutId
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
        }
    }

    /**
     * 移除所有尾布局View
     */
    open fun removeAllFooterView() {
        if (null != footerViewParentLayout) {
            footerViewParentLayout!!.removeAllViews()
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
    }

    /**
     * 移除所有的头布局View
     */
    open fun removeAllHeaderView() {
        if (null != headerViewParentLayout) {
            headerViewParentLayout!!.removeAllViews()
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