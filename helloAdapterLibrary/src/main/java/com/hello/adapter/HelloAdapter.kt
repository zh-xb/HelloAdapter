package com.hello.adapter

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * @Description:
 * @Author: zhangxiaobai
 * @Date: 2020/8/29 16:14
 */
abstract class HelloAdapter<T>(var context: Context) : RecyclerView.Adapter<HelloHolder>() {
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
    private var onItemClick: OnItemClick? = null
    private var onItemClickForData: OnItemClickForData<T>? = null
    private var onItemLongClick: OnItemLongClick? = null
    private var onItemLongClickForData: OnItemLongClickForData<T>? = null
    private var onHeadAndFootClick: OnHeadAndFootClick? = null
    // 头布局view
    private val HEADER_VIEW = 0x10000111
    // 尾布局view
    private val FOOTER_VIEW = 0x10000222
    // 数据填充的view
    private val DATA_VIEW = 0x10000333
    // 空数据显示的view
    private val EMPTY_VIEW = 0x10000444
    // 空数据的viewHolder
    private var emptyHolder: HelloHolder? = null
    // 头部view的父布局
    private var headerViewLayout: LinearLayout? = null
    // 尾部view的父布局
    private var footerViewLayout: LinearLayout? = null

    override fun getItemViewType(position: Int): Int {
        if (0 == position) {
            return HEADER_VIEW
        }
        if (1 == position) {
            return EMPTY_VIEW
        }
        return if (position < itemCount - 1) {
            DATA_VIEW
        } else {
            FOOTER_VIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelloHolder {
        when {
            HEADER_VIEW == viewType -> {
                if (null == headerViewLayout) {
                    initHeaderView()
                } else {
                    val headerViewParent = headerViewLayout!!.parent
                    if (headerViewParent is ViewGroup) {
                        headerViewParent.removeView(headerViewLayout)
                    }
                }
                return HelloHolder(headerViewLayout!!)
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
            DATA_VIEW == viewType -> {
                val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
                return HelloHolder(view)
            }
            FOOTER_VIEW == viewType -> {
                if (null == footerViewLayout) {
                    initFooterView()
                } else {
                    val footerViewParent = footerViewLayout!!.parent
                    if (footerViewParent is ViewGroup) {
                        footerViewParent.removeView(footerViewLayout)
                    }
                }
                return HelloHolder(footerViewLayout!!)
            }
            else -> {
                val view: View =
                    LayoutInflater.from(context).inflate(R.layout.item_empty_layout2, parent, false)
                return HelloHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        // 增加 头、尾和空数据3个固定布局
        return baseData.size + 3
    }

    override fun onBindViewHolder(holder: HelloHolder, position: Int) {
        try {
            if (holder.itemViewType == EMPTY_VIEW && null != emptyHolder) { // 数据为空的布局
                if (showEmptyLayout && baseData.size == 0) {
                    emptyHolder!!.setVisibility(true)
                } else {
                    emptyHolder!!.setVisibility(false)
                }
            } else if (holder.itemViewType == DATA_VIEW) {
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
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
     * 添加尾布局
     *
     * @param layoutId
     * @return 添加尾的view
     */
    open fun addFooterView(layoutId: Int): View {
        if (null == footerViewLayout) {
            initFooterView()
        }
        val foot =
            LayoutInflater.from(context).inflate(layoutId, footerViewLayout, false)
        footerViewLayout!!.addView(foot)
        return foot
    }

    /**
     * 添加尾布局并给尾布局的子view设置点击事件
     *
     * @param layoutId
     * @return 添加尾的view
     */
    open fun addFooterAndClickListener(layoutId: Int, layoutIds: MutableList<Int>): View? {
        val foot:View = addFooterView(layoutId)
        for (layoutId in layoutIds) {
            val view: View = foot.findViewById(layoutId)
            view.setOnClickListener {
                if (null != onHeadAndFootClick) {
                    onHeadAndFootClick!!.headAndFootClick(view)
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
        if (null == headerViewLayout) {
            initHeaderView()
        }
        val header =
            LayoutInflater.from(context).inflate(layoutId, headerViewLayout, false)
        headerViewLayout!!.addView(header)
        return header
    }

    /**
     * 添加头布局并给头布局的子view设置点击事件
     *
     * @param layoutId
     * @return 添加头的view
     */
    open fun addHeaderAndClickListener(layoutId: Int, layoutIds: MutableList<Int>): View {
        val header = addHeaderView(layoutId)
        for (layoutId in layoutIds) {
            val view: View = header.findViewById(layoutId)
            view.setOnClickListener {
                if (null != onHeadAndFootClick) {
                    onHeadAndFootClick!!.headAndFootClick(view)
                }
            }
        }
        return header
    }

    /**
     * 移除尾
     *
     * @param view
     */
    open fun removeFooterView(view: View?) {
        if (null != footerViewLayout) {
            footerViewLayout!!.removeView(view)
        }
    }

    /**
     * 移除尾
     *
     * @param viewIndex（添加的View下标值）
     */
    open fun removeFooterView(viewIndex: Int?) {
        if (null != footerViewLayout && null != viewIndex) {
            footerViewLayout!!.removeViewAt(viewIndex)
        }
    }

    /**
     * 移除头View
     *
     * @param view
     */
    open fun removeHeaderView(view: View?) {
        if (null != headerViewLayout) {
            headerViewLayout!!.removeView(view)
        }
    }

    /**
     * 移除头View
     *
     * @param viewIndex（添加的View下标值）
     */
    open fun removeHeaderView(viewIndex: Int?) {
        if (null != headerViewLayout && null != viewIndex) {
            headerViewLayout!!.removeViewAt(viewIndex)
        }
    }

    /**
     * 初始化头部布局
     */
    private fun initHeaderView() {
        headerViewLayout = LinearLayout(context)
        headerViewLayout!!.orientation = LinearLayout.VERTICAL
        val layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        headerViewLayout!!.layoutParams = layoutParams
    }

    /**
     * 初始化底部布局
     */
    private fun initFooterView() {
        if (null == footerViewLayout) {
            footerViewLayout = LinearLayout(context)
            footerViewLayout!!.orientation = LinearLayout.VERTICAL
            val layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            footerViewLayout!!.layoutParams = layoutParams
        }
    }

    protected abstract fun bindViewHolder(holder: HelloHolder, data: MutableList<T>, position: Int)

    /**
     * 设置头和尾view的监听事件
     */
    fun setOnHeadAndFootClick(onHeadAndFootClick: OnHeadAndFootClick?) {
        this.onHeadAndFootClick = onHeadAndFootClick
    }

    interface OnHeadAndFootClick {
        fun headAndFootClick(view: View?)
    }

    interface OnItemClick {
        fun itemClick(view: View?, position: Int)
    }

    interface OnItemClickForData<T> {
        fun itemClickForData(view: View?, position: Int, data: T)
    }

    interface OnItemLongClick {
        fun itemLongClick(view: View?, position: Int)
    }

    interface OnItemLongClickForData<T> {
        fun itemLongClickForData(view: View?, position: Int, data: T)
    }

    fun setOnItemClick(onItemClick: OnItemClick?) {
        this.onItemClick = onItemClick
    }

    fun setOnItemClickForData(onItemClickForData: OnItemClickForData<T>) {
        this.onItemClickForData = onItemClickForData
    }

    fun setOnItemLongClick(onItemLongClick: OnItemLongClick?) {
        this.onItemLongClick = onItemLongClick
    }

    fun setOnItemLongClickForData(onItemLongClickForData: OnItemLongClickForData<T>) {
        this.onItemLongClickForData = onItemLongClickForData
    }

    private fun setOnClick(view: View, position: Int) {
        view.setOnClickListener {
            try {
                if (null != onItemClick) {
                    onItemClick!!.itemClick(view, position)
                }
                if (null != onItemClickForData) onItemClickForData!!.itemClickForData(
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
                if (null != onItemLongClick) {
                    onItemLongClick!!.itemLongClick(v, position)
                }
                if (null != onItemLongClickForData) {
                    onItemLongClickForData!!.itemLongClickForData(
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