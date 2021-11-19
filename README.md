# HelloAdapter
一个给adapter添加头和尾，并给头和尾的子view设置点击事件的一个简单自用库，并对多布局adapter的封装。

addFootView 方法仅添加尾布局

addHeadView 方法仅添加头布局

实现OnHeadAndFootClick接口并通过如下设置监听

addFooterAndClickListener 方法添加尾布局并对尾布局中的view设置点击监听，会接收一个view的id集合。

addHeaderAndClickListener 方法添加头布局并对头布局中的view设置点击监听，会接收一个view的id集合。

实现OnHeadAndFootClickListener并重写headAndFootClick方法，


TestAdapter继承自HelloAdapter，初始化Adapter。

adapter = TestAdapter(this)

.setLayoutId(R.layout.item_layout) // 设置item的布局文件（必选）

.setData(data) // 设置adapter的数据集合（必选）

.setEmptyLayoutId(R.layout.item_empty_layout_a) // 设置adapter无数据时的布局（可选，有默认布局）

.showEmptyLayout(true) // adapter无数据时是否显示“暂无数据”的布局（默认显示）

头布局设置点击事件的view id

var layoutIds: MutableList<Int> = arrayListOf()
	
layoutIds.add(R.id.head_bt1)

layoutIds.add(R.id.head_bt2)

layoutIds.add(R.id.head_bt3)

设置头布局，并设置点击事件

val header: View = adapter?.addHeaderAndClickListener(R.layout.header_layout3,layoutIds)!!

只添加头布局，不设置点击监听

val header: View = adapter?.addHeaderView(R.layout.header_layout3)!!

自定义adapter后继承HelloAdapter并实现抽象方法bindViewHolder，并在bindViewHolder中绑定数据。

如果是多布局adapter，可复写getItemViewHelloType方法和onCreateViewHelloHolder方法，在getItemViewHelloType方法中返回自定义的布局类型viewType，在onCreateViewHelloHolder方法中根据getItemViewHelloType返回的viewType判断需要初始化的布局和要创建的holder。

在onCreateViewHelloHolder方法中创建holder的时候，可以自定义holder并继承HelloHolder，复写HelloHolder中的bindViewData方法，直接在自定义的holder中bindViewData方法里做数据绑定，这样在adapter比较复杂时，可以显得代码非常简洁清晰。


	   
	repositories {
          maven { url 'https://jitpack.io' }
	}
	   
    dependencies {
	      implementation 'com.github.zh-xb:HelloAdapter:1.0.3'
	}
	    
  
