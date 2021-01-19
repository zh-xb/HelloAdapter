# HelloAdapter
一个给adapter添加头和尾并设置点击事件的简单自用库，只有HelloAdapter和HelloHolder两个类。

addFootView 方法仅添加尾布局

addHeadView 方法仅添加头布局

实现OnHeadAndFootClick接口并通过如下设置监听

addFootAndClickListener 方法添加尾布局并对尾布局中的view设置点击监听，会接收一个view的id集合。

addHeadAndClickListener 方法添加头布局并对头布局中的view设置点击监听，会接收一个view的id集合。

实现OnHeadAndFootClick即可



	   
	   repositories{
	         maven { url 'https://jitpack.io' }
	   }
	   

	    dependencies {
	          implementation 'com.github.zh-xb:HelloAdapter:v1.0.0'
	    }
	    
  
