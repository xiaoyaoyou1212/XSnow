# XSnow

[![Author](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-%E8%83%A1%E4%BC%9F-blue.svg)](http://www.huwei.tech/) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/bfcabf1d9793485d84f090e542255710)](https://www.codacy.com/app/xiaoyaoyou1212/XSnow?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=xiaoyaoyou1212/XSnow&amp;utm_campaign=Badge_Grade) [![License](https://img.shields.io/badge/License-Apache--2.0-green.svg)](https://github.com/xiaoyaoyou1212/XSnow/blob/master/LICENSE) [![JCenter](https://img.shields.io/badge/JCenter-2.0.1-orange.svg)](https://jcenter.bintray.com/com/vise/xiaoyaoyou/xsnow/2.0.1/) [![API](https://img.shields.io/badge/API-12%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=12)

基于RxJava+Retrofit精心打造的Android基础框架，包含网络、上传、下载、缓存、事件总线、权限管理、数据库、图片加载、UI模块，基本都是项目中必用功能，每个模块充分解耦，可随意替换。
XSnow，X：未知一切，取其通用之意；Snow：雪，取其纯净之意。该框架通用纯净，只依赖公共核心库。

- 项目地址：[https://github.com/xiaoyaoyou1212/XSnow](https://github.com/xiaoyaoyou1212/XSnow)

- 项目依赖：`compile 'com.vise.xiaoyaoyou:xsnow:2.0.1'`

### QQ交流群
![QQ群](http://img.blog.csdn.net/20170327191310083)

### 版本说明
- V2.0.1
    - 修复权限申请异常。（2017-06-08）

- V2.0.0
	- 项目大范围重构，将一些不通用功能抽离，让每一个模块功能更纯净通用，基础框架也由RxJava迁移到RxJava2。（2017-06-08）

- V1.1.1
	- 优化BaseActivity、BaseFragment以及BaseApplication，简化findViewById操作以及点击事件处理。（2017-04-04）

- V1.1.0
	- 增加状态视图切换功能，如网络异常、无数据等情况下的显示，让视图显示更方便管理。（2017-03-08）

- V1.0.1
	- 优化部分模块间的耦合性。（2017-01-19）

- V1.0.0
	- 项目初始提交。（2017-01-16）

版本号说明：版本号第一位为大版本更新时使用，第二位为小功能更新时使用，第三位则是用来bug修复管理。

### *注意事项*

- 项目中的每个功能都附有使用示例，但示例可能不是很全，主要是提供一个使用的思路，有些现象需要通过查看日志才能看到，如数据库的增删改查操作、事件总线的通知功能等。

- 网络访问的API调试采用的是moco服务进行处理的，项目中有提供开启该服务的命令，需要在使用时调用命令开启该服务，还有需要将应用初始化的baseurl设置为本地电脑的IP地址。

- 上传文件功能由于使用的是公司内部服务器调试，所以在demo中只提供了一个使用示例，无法看到效果。

## 网络

### 简介：

网络算是项目的灵魂，基本每个项目都离不开网络，而一个简单好用，又支持各种配置的网络访问库就显得尤为重要了。该模块也是XSnow的核心功能，使用简单，支持定制常用配置，如各种拦截器、缓存策略、请求头等。

如果不进行二次封装，上层项目基于RxJava+Retrofit请求网络时需要每个接口都写一个服务接口，这样非常不便利。如何将响应结果通用处理就成为该模块的重点，项目中采用泛型转换方式，将响应结果ResponseBody通过map操作符转换成需要的T，具体实现参考项目中http包下的func包，如果需要Http响应码，也可以将响应结果包装成Response<ResponseBody>这样进行转换成T，考虑到项目中很少需要Http响应码来进行判定，一般使用服务器自定义的响应码就可以了，故该模块采用ResponseBody统一接收这种处理方式。

### 功能：

- 支持全局配置和单个请求的局部配置，如果局部配置与全局配置冲突，那么局部配置会替换全局配置。

- 全局配置支持`CallAdapter.Factory`、`Converter.Factory`、`okhttp3.Call.Factory`、`SSLSocketFactory`、`HostnameVerifier`、`ConnectionPool`、主机URL、请求头、请求参数、代理、拦截器、Cookie、OKHttp缓存、连接超时时间、读写超时时间、失败重试次数、失败重试间隔时间的一系列配置。

- 局部请求配置支持主机URL、请求后缀、请求头、请求参数、拦截器、本地缓存策略、本地缓存时间、本地缓存key、连接超时时间、读写超时时间的一系列配置。

- 支持OKHttp本身的Http缓存，也支持外部自定义的在线离线缓存，可配置缓存策略，共有五种缓存策略，如优先获取缓存策略，具体实现参考http包下的strategy包。

- 支持请求与响应统一处理，不需要上层每个模块都定义请求服务接口。

- 支持泛型T接收处理响应数据，也可根据服务器返回的统一数据模式定制如包含Code、Data、Message的通用Model ApiResult<T>。由于ApiResult的属性不定，无法做到统一处理，所以单独放到netexpand module中，里面包含与其相关的请求处理，可以根据该module定制属于各自服务器的相关功能。

- 支持异常统一处理，定制了ApiException拦截处理，统一返回异常信息。

- 支持返回Observable，可继续定制请求的相关特性，也支持返回回调的处理结果。

- 支持失败重试机制，可配置失败重试次数以及重试时间间隔。

- ......

### 使用示例：

第一步需要在application中进行全局初始化以及添加全局相关配置，具体使用如下：

```
ViseHttp.init(this);
        ViseHttp.CONFIG()
                .baseUrl("http://10.8.4.39/")
                .setCookie(true)
                .converterFactory(GsonConverterFactory.create())
                .interceptor(new HttpLogInterceptor()
                        .setLevel(HttpLogInterceptor.Level.BODY));
```

后面就是具体调用请求的过程，请求的类型有多种情形，下面就以最常用的几种类型举例说明，具体效果可以查看demo，以下为使用示例：

- GET 不带缓存
```
ViseHttp.GET().suffixUrl("getAuthor").request(mContext, new ACallback<AuthorModel>() {
    @Override
    public void onSuccess(AuthorModel authorModel) {
    }

    @Override
    public void onFail(int errCode, String errMsg) {
    }
});
```

- GET 带缓存
```
ViseHttp.GET()
                .suffixUrl("getAuthor")
                .setLocalCache(true)
                .cacheMode(CacheMode.FIRST_CACHE) //配置缓存策略
                .request(mContext, new ACallback<CacheResult<AuthorModel>>() {
                    @Override
                    public void onSuccess(CacheResult<AuthorModel> cacheResult) {
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                    }
                });
```

- GET 返回String
```
ViseHttp.GET().suffixUrl("getString").request(mContext, new ACallback<String>() {
    @Override
    public void onSuccess(String data) {
    }

    @Override
    public void onFail(int errCode, String errMsg) {
    }
});
```

- GET 返回List
```
ViseHttp.GET().suffixUrl("getListAuthor").request(mContext, new ACallback<List<AuthorModel>>() {
    @Override
    public void onSuccess(List<AuthorModel> authorModel) {
    }

    @Override
    public void onFail(int errCode, String errMsg) {
    }
});
```

- GET 返回ApiResult
```
ViseHttp.BASE(new ApiGetRequest()).suffixUrl("getApiResultAuthor").request(mContext, new ACallback<AuthorModel>() {
    @Override
    public void onSuccess(AuthorModel authorModel) {
    }

    @Override
    public void onFail(int errCode, String errMsg) {
    }
});
```

- POST 上传表单
```
ViseHttp.BASE(new ApiPostRequest()
                .addForm("author_name", getString(R.string.author_name))
                .addForm("author_nickname", getString(R.string.author_nickname))
                .addForm("author_account", "xiaoyaoyou1212")
                .addForm("author_github", "https://github.com/xiaoyaoyou1212")
                .addForm("author_csdn", "http://blog.csdn.net/xiaoyaoyou1212")
                .addForm("author_websit", "http://www.huwei.tech/")
                .addForm("author_introduction", getString(R.string.author_introduction)))
                .suffixUrl("postFormAuthor")
                .request(mContext, new ACallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                    }
                });
```

- POST 上传json
```
AuthorModel mAuthorModel = new AuthorModel();
mAuthorModel.setAuthor_id(1008);
mAuthorModel.setAuthor_name(getString(R.string.author_name));
mAuthorModel.setAuthor_nickname(getString(R.string.author_nickname));
mAuthorModel.setAuthor_account("xiaoyaoyou1212");
mAuthorModel.setAuthor_github("https://github.com/xiaoyaoyou1212");
mAuthorModel.setAuthor_csdn("http://blog.csdn.net/xiaoyaoyou1212");
mAuthorModel.setAuthor_websit("http://www.huwei.tech/");
mAuthorModel.setAuthor_introduction(getString(R.string.author_introduction));
ViseHttp.BASE(new ApiPostRequest()
        .setJson(GSONUtil.gson().toJson(mAuthorModel)))
        .suffixUrl("postJsonAuthor")
        .request(mContext, new ACallback<String>() {
            @Override
            public void onSuccess(String data) {
            }

            @Override
            public void onFail(int errCode, String errMsg) {
            }
        });
```

- POST 后缀带请求参数
```
AuthorModel mAuthorModel = new AuthorModel();
mAuthorModel.setAuthor_id(1009);
mAuthorModel.setAuthor_name(getString(R.string.author_name));
mAuthorModel.setAuthor_nickname(getString(R.string.author_nickname));
mAuthorModel.setAuthor_account("xiaoyaoyou1212");
mAuthorModel.setAuthor_github("https://github.com/xiaoyaoyou1212");
mAuthorModel.setAuthor_csdn("http://blog.csdn.net/xiaoyaoyou1212");
mAuthorModel.setAuthor_websit("http://www.huwei.tech/");
mAuthorModel.setAuthor_introduction(getString(R.string.author_introduction));
ViseHttp.BASE(new ApiPostRequest()
        .addUrlParam("appId", "10001")
        .addUrlParam("appType", "Android")
        .setJson(GSONUtil.gson().toJson(mAuthorModel)))
        .suffixUrl("postUrlAuthor")
        .request(mContext, new ACallback<String>() {
            @Override
            public void onSuccess(String data) {
            }

            @Override
            public void onFail(int errCode, String errMsg) {
            }
        });
```

## 上传下载

### 简介：

该库提供的上传下载功能比较简洁实用，基本能满足单个线程下的常用相关操作，如果需要多线程和断点续传功能就需要上层实现，也可以依赖如RxDownload库。

### 功能：

- 支持单文件和多文件上传。

- 支持每个文件都有对应的回调进度。

- 支持传入字节流或者字节数组进行上传。

- 支持下载进度回调，每秒刷新下载进度。

### 使用示例：

由于上传下载功能需要用到网络相关，所以也需要像网络那样进行初始化和添加全局配置。下面是上传下载使用示例，具体效果可以查看demo。

- 上传示例：
```
ViseHttp.UPLOAD(new UCallback() {
    @Override
    public void onProgress(long currentLength, long totalLength, float percent) {
    }

    @Override
    public void onFail(int errCode, String errMsg) {
    }}).addFile("androidIcon", getUploadFile(mContext, "test.jpg"))
        .baseUrl("https://200.200.200.50/")
        .suffixUrl("addImageFile")
        .request(mContext, new ACallback<Object>() {
    @Override
    public void onSuccess(Object data) {
    }

    @Override
    public void onFail(int errCode, String errMsg) {
    }
});
```

- 下载示例：
```
ViseHttp.DOWNLOAD()
                .baseUrl("http://dldir1.qq.com/")
                .suffixUrl("weixin/android/weixin6330android920.apk")
                .setFileName(saveName)
                .request(mContext, new ACallback<DownProgress>() {
                    @Override
                    public void onSuccess(DownProgress downProgress) {
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                    }
                });
```

## 缓存

### 简介：
包含内存、磁盘二级缓存以及SharedPreferences缓存，可自由拓展。磁盘缓存支持KEY加密存储，可定制缓存时长。SharedPreferences支持内容安全存储，采用Base64加密解密。

### 使用示例：

- 内存存储：`MemoryCache.getInstance().put("authorInfo", mAuthorModel);`

- 内存获取：`MemoryCache.getInstance().get("authorInfo");`

- 磁盘缓存存储：`diskCache.put("authorInfo", mAuthorModel);`

- 磁盘缓存获取：`diskCache.get("authorInfo");`

- SharedPreferences缓存存储：`spCache.put("authorInfo", mAuthorModel);`

- SharedPreferences缓存获取：`spCache.get("authorInfo");`

## 事件总线

### 简介：
采用Rx响应式编程思想建立的RxBus模块，采用注解方式标识事件消耗地，通过遍历查找事件处理方法。支持可插拔，可替换成EventBus库，只需上层采用的同样是注解方式，那么上层是不需要动任何代码的。

### 使用示例：

- 发送事件：`BusFactory.getBus().post(new AuthorEvent().setAuthorModel(mAuthorModel));`

- 注册事件：`BusFactory.getBus().register(this);`

- 取消注册：`BusFactory.getBus().unregister(this);`

- 接收事件：
```
@EventSubscribe
public void showAuthor(IEvent event) {
    if (event != null && event instanceof AuthorEvent) {
        ViseLog.i("Receive Event Message:" + ((AuthorEvent) event).getAuthorModel());
    }
}
```

## 数据库

### 简介：

采用greenDao数据库，其优势就不多说了，网上有解释，其主要优点就是性能高。该模块定制数据库操作接口，有统一的实现类DBManager，上层只需实现getAbstractDao()方法告知底层DaoSession，增删改查操作不需要关心具体细节，调用DBManager中的方法就行。

### 使用示例：

- 配置：需要通过gradle添加greendao的相关插件配置，还有需要在当前应用module中的android配置下添加如下配置信息，具体可以参考demo的示例
```
greendao{
    schemaVersion 1	//数据库版本
    targetGenDir 'src/main/java'	//包目录所在文件夹
    daoPackage 'com.vise.snowdemo.db'	//存放数据库相关的包目录
}
```

- 初始化：在application中进行如下初始化操作：`DbHelper.getInstance().init(this);`

- 增：`DbHelper.getInstance().author().insert(mAuthorModel);`

- 删：`DbHelper.getInstance().author().delete(mAuthorModel);`
 
- 改：`DbHelper.getInstance().author().update(mAuthorModel);`

- 查：`DbHelper.getInstance().author().loadAll()`

## 图片加载

### 简介：
采用Glide库进行图片加载，支持轻量级图片加载，该模块支持可插拔，可根据需求替换成任意图片加载库，如果项目中对于图片处理要求比较高，那么可以替换成Facebook提供的Fresco库。

### 使用示例：

- 初始化：在application中进行如下初始化操作：`LoaderFactory.getLoader().init(this);`

- 调用过程：
```
LoaderFactory.getLoader().loadNet(imageView, url, new ILoader.Options(R.mipmap.github_head_portrait, R.mipmap.github_head_portrait));
```

## 权限管理

### 简介：

由于Android6.0以上系统对于权限管理更严格，安全性也有很大的提高，但是随之带来的就是权限管理的代码编写更麻烦，如在权限被用户拒绝时该怎么提示，或者在用户勾选永远拒绝下该怎么处理。针对此种情况，该模块尽量以最小的调用完成权限的管理，只需要一行代码就搞定权限的申请过程，并返回所有需要的回调结果。

### 使用示例：
```
//具体使用效果请查看demo
PermissionManager.instance().with(this).request(new OnPermissionCallback() {
    @Override
    public void onRequestAllow(String permissionName) {
        DialogUtil.showTips(mContext, getString(R.string.permission_control),
                getString(R.string.permission_allow) + "\n" + permissionName);
    }

    @Override
    public void onRequestRefuse(String permissionName) {
        DialogUtil.showTips(mContext, getString(R.string.permission_control),
                getString(R.string.permission_refuse) + "\n" + permissionName);
    }

    @Override
    public void onRequestNoAsk(String permissionName) {
        DialogUtil.showTips(mContext, getString(R.string.permission_control),
                getString(R.string.permission_noAsk) + "\n" + permissionName);
    }
}, Manifest.permission.CALL_PHONE);
```

## UI

### 简介：

包含基础组件、视图状态管理以及万能适配器。适配器可满足所有基于BaseAdapter的适配器组装，使用方便，易拓展；视图状态管理包含空视图、网络异常、其他异常的重试以及视图切换机制。

### 使用示例：
```
//创建视图管理
mStatusLayoutManager = StatusLayoutManager.newBuilder(mContext)
        .contentView(R.layout.status_switch_content_layout)//配置内容视图
        .loadingView(R.layout.loading_layout)//配置加载视图
        .emptyView(R.layout.empty_layout)//配置空视图
        .networkErrorView(R.layout.network_error_layout)//配置网络异常视图
        .otherErrorView(R.layout.other_error_layout)//配置其他异常视图
        .retryViewId(R.id.reload_view)//配置重试ViewID
        .onStatusViewListener(new OnStatusViewListener() {//配置状态监听
            @Override
            public void onShowView(View view, int id) {//显示
            }

            @Override
            public void onHideView(View view, int id) {//隐藏
            }
        })
        .onRetryListener(new OnRetryListener() {//配置重试监听
            @Override
            public void onRetry() {
            	//模拟重试，显示加载视图
                mStatusLayoutManager.showLoadingView();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            	//加载成功，显示内容视图
                                mStatusLayoutManager.showContentView();
                            }
                        });
                    }
                }).start();
            }
        }).build();
mLayoutMain.addView(mStatusLayoutManager.getStatusLayout());关联根视图
mStatusLayoutManager.showLoadingView();//显示加载视图
```


## 效果展示
![](https://github.com/xiaoyaoyou1212/XSnow/blob/master/screenshot/main_view.png)
![](https://github.com/xiaoyaoyou1212/XSnow/blob/master/screenshot/function_view.png)
![](https://github.com/xiaoyaoyou1212/XSnow/blob/master/screenshot/http_get_view.png)
![](https://github.com/xiaoyaoyou1212/XSnow/blob/master/screenshot/http_post_view.png)
![](https://github.com/xiaoyaoyou1212/XSnow/blob/master/screenshot/download_view.png)
![](https://github.com/xiaoyaoyou1212/XSnow/blob/master/screenshot/image_loader_view.png)
![](https://github.com/xiaoyaoyou1212/XSnow/blob/master/screenshot/status_switch_view.png)
![](https://github.com/xiaoyaoyou1212/XSnow/blob/master/screenshot/other_view.png)

*注：该框架引用了日志系统和公共工具库，这两个库都很轻量级，具体使用详情可分别参考
[https://github.com/xiaoyaoyou1212/ViseLog](https://github.com/xiaoyaoyou1212/ViseLog)和
[https://github.com/xiaoyaoyou1212/ViseUtils](https://github.com/xiaoyaoyou1212/ViseUtils)。*

### 关于作者
#### 作者：胡伟
#### 网站：[http://www.huwei.tech](http://www.huwei.tech)
#### 博客：[http://blog.csdn.net/xiaoyaoyou1212](http://blog.csdn.net/xiaoyaoyou1212)
