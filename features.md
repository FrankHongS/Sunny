**\*\*\*2019/2/13\*\*\***
* 天气定时更新（使用foreground service实现或者使用WorkManager实现）
* 折叠天气信息以及查看更多功能 (done)
* 天气温度折线图展示 (done)

**\*\*\*2019/6/26\*\*\***
* 接入空气质量API (done)
* 优化多城市管理界面 (考虑换成StaggeredGridLayoutManager) (todo)

**\*\*\*2019/9/19\*\*\***
* 优化Setting页面  
    * UI布局 (done)
    * 首页weather和收藏城市的RecyclerView动画 (done)
    * 天气定时更新 (todo) 
* 优化SearchCity页面  
    * checkbox隐藏动画有问题 (todo)
    * 以及其他 (todo)
* refactor网络请求，主要是RetrofitSingleton (todo)
* 添加MyLogger (done)

**\*\*\*2020/7/11\*\*\***
* 在`onSaveInstanceState`和`onRestoreInstanceState`之间不要进行数据更新，
使用`ViewModel`来保存数据；这样既可以避免过多网络请求占用网络，也可以避免在这些请求过程中服务器抛异常(todo)
