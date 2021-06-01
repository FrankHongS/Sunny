**bugs list**

2018/10/28

* 多城市加载页面中，当选择城市返回时，城市会重复出现两次
（fixed,由于在onStart方法中加载天气信息，导致Activity跳转之后再回来会重复执行加载方法，
解决方法：将加载方法放在onActivityCreated中执行）

2019/12/2

* 当前天气温度显示不全
(fixed)