# XSnow
基于RxJava+Retrofit精心打造的Android基础框架，包含网络、下载、缓存、事件总线、数据库、图片加载、UI模块，基本都是项目中必用功能，每个模块充分解耦，可随意替换。
XSnow，X：未知一切，取其通用之意；Snow：雪，取其纯净之意。该框架通用纯净，只依赖公共核心库，每个模块都可单独作为一个库，如只需要网络相关，那么只需拷贝common和net包就行。

- 项目地址：[https://github.com/xiaoyaoyou1212/XSnow](https://github.com/xiaoyaoyou1212/XSnow)

- 项目依赖：`compile project('com.vise.xiaoyaoyou:xsnow:1.1.1')`

### QQ交流群
![QQ群](http://img.blog.csdn.net/20170327191310083)

### 版本说明

- V1.1.1
优化BaseActivity、BaseFragment以及BaseApplication，简化findViewById操作以及点击事件处理。

- V1.1.0
增加状态视图切换功能，如网络异常、无数据等情况下的显示，让视图显示更方便管理。

- V1.0.1
优化部分模块间的耦合性。

- V1.0.0
项目初始提交。

版本号说明：版本号第一位为大版本更新时使用，第二位为小功能更新时使用，第三位则是用来bug修复管理。

### *注意事项*

- 项目中的每个功能都附有使用示例，但示例可能不是很全，主要是提供一个使用的思路，有些现象需要通过查看日志才能看到，如数据库的增删改查操作、事件总线的通知功能等。
- 项目中的下载功能由于是参考RxDownload项目，只是优化了部分功能，如果要看详细使用介绍可参考RxDownload项目，下面有项目地址说明。
- 有部分使用者说demo中有崩溃问题，在此进行说明一下：下载功能的demo展示中普通下载和服务下载只能选择一个，不能来回切换，不然会出现崩溃现象，还有如果下载暂停可以再次点击实现继续下载，这是我为图省事遗留的问题，后期会考虑写一个完成的下载示例，望各位见谅。

## 网络
网络算是项目的灵魂，基本每个项目都离不开网络，而一个简单好用，又支持各种配置的网络访问库就显得尤为重要了。该模块也是XSnow的核心功能，使用简单，支持定制常用配置，如各种拦截器、缓存策略、请求头等。上层项目基于RxJava+Retrofit请求网络时需要每个接口都写一个服务接口，这样非常不便利，如何将响应结果通用处理就成为该模块的重点，后面采用泛型转换方式，将响应结果ResponseBody通过map操作符转换成需要的T，具体实现参考项目中net包下的func包，如果需要Http响应码，也可以将响应结果包装成Response<ResponseBody>这样进行转换成T，考虑到项目中很少需要Http响应码来进行判定，一般使用服务器自定义的响应码就可以了，故该模块采用ResponseBody统一接收这种处理方式。

- 支持OKHttp本身的HTTP缓存，也支持外部自定义的在线离线缓存，可配置常用缓存策略，如优先获取缓存策略等。

- 支持请求与响应统一处理，不需要上层每个模块都定义ApiService接口。

- 支持泛型T接收处理响应数据，也可根据服务器返回的统一数据模式定制如包含Code、Data、Message的通用Model ApiResult<T>。

- 支持异常统一处理，定制了ApiException拦截处理，可根据服务器各种响应码定制异常提示。

- 支持不需订阅者的回调数据处理。

- 支持自定义请求头。

- ......

## 下载
基本思路都来源于此项目[https://github.com/ssseasonnn/RxDownload](https://github.com/ssseasonnn/RxDownload)，这里我就不赘述了，具体使用详情可参考该项目地址，RxDownload算是基于RxJava+Retrofit写得比较优秀的下载框架了。

## 缓存
包含内存、磁盘二级缓存以及SharedPreferences缓存，可自由拓展。支持磁盘缓存支持KEY加密存储，可定制缓存时长。SharedPreferences支持内容安全存储，采用Base64加密解密。

## 事件总线
采用Rx响应式编程思想建立的RxBus模块，采用注解方式标识事件消耗地，通过遍历查找事件处理方法。支持可插拔，可替换成EventBus库，只需上层采用的同样是注解方式，那么上层是不需要动任何代码的。

## 数据库
采用greenDao数据库，其优势就不多说了，网上有解释，其主要优点就是性能高。该模块定制数据库操作接口，有统一的实现类DBManager，上层只需实现getAbstractDao()方法告知底层DaoSession，增删改查操作不需要关心具体细节，调用DBManager中的方法就行。

## 图片加载
采用Glide库进行图片加载，支持轻量级图片加载，该模块支持可插拔，可根据需求替换成任意图片加载库，如果项目中对于图片处理要求比较高，那么可以替换成Facebook提供的Fresco库。

## UI
包含BaseActivity、BaseFragment以及万能适配器，适配器可满足所有基于BaseAdapter的适配器组装，使用方便，易拓展。

## 使用介绍
网络请求对象初始化：
```
//不带缓存
ViseApi mViseApi = new ViseApi.Builder(mContext).build();
//带缓存
ViseApi mViseApi = new ViseApi.Builder(mContext).cacheKey(ApiHost.getHost()).cacheMode(CacheMode.FIRST_CACHE).build();
```
数据库操作初始化：`DbHelper.getInstance().init(this);`
图片加载初始化：`LoaderFactory.getLoader().init(this);`
下载对象初始化：`ViseDownload.getInstance().maxThread(3).context(mContext);`

注：其他配置和具体使用详情请参考demo和docs中的API介绍，demo中有详细的使用展示，基本能明白具体怎么使用，XSnow库中核心的API都有注释，能满足基本的调用需求，如果有不明白的地方欢迎留言交流！

## 效果展示
![这里写图片描述](http://img.blog.csdn.net/20170120152904942?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGlhb3lhb3lvdTEyMTI=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
![这里写图片描述](http://img.blog.csdn.net/20170120152916952?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGlhb3lhb3lvdTEyMTI=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
![这里写图片描述](http://img.blog.csdn.net/20170120152933661?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGlhb3lhb3lvdTEyMTI=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
![这里写图片描述](http://img.blog.csdn.net/20170120152955708?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGlhb3lhb3lvdTEyMTI=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
![这里写图片描述](http://img.blog.csdn.net/20170120153009922?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGlhb3lhb3lvdTEyMTI=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
![这里写图片描述](http://img.blog.csdn.net/20170120153021266?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGlhb3lhb3lvdTEyMTI=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

*注：该框架引用了日志系统和公共工具库，这两个库都很轻量级，具体使用详情可分别参考[https://github.com/xiaoyaoyou1212/ViseLog](https://github.com/xiaoyaoyou1212/ViseLog)和[https://github.com/xiaoyaoyou1212/ViseUtils](https://github.com/xiaoyaoyou1212/ViseUtils)。*

### 关于作者
#### 作者：胡伟
#### 网站：[http://www.huwei.tech](http://www.huwei.tech)
#### 博客：[http://blog.csdn.net/xiaoyaoyou1212](http://blog.csdn.net/xiaoyaoyou1212)
