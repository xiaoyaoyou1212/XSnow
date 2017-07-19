# XSnow

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/bfcabf1d9793485d84f090e542255710)](https://www.codacy.com/app/xiaoyaoyou1212/XSnow?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=xiaoyaoyou1212/XSnow&amp;utm_campaign=Badge_Grade) [![License](https://img.shields.io/badge/License-Apache--2.0-green.svg)](https://github.com/xiaoyaoyou1212/XSnow/blob/master/LICENSE) [![API](https://img.shields.io/badge/API-12%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=12)

基于RxJava2+Retrofit2精心打造的Android基础框架，包含网络、上传、下载、缓存、事件总线、权限管理、数据库、图片加载、UI模块，基本都是项目中必用功能，每个模块充分解耦，可自由拓展。

XSnow，X：未知一切，取其通用之意；Snow：雪，取其纯净之意。该框架通用纯净，只依赖公共核心库。

- 项目地址：[https://github.com/xiaoyaoyou1212/XSnow](https://github.com/xiaoyaoyou1212/XSnow)

- 项目依赖：`compile 'com.vise.xiaoyaoyou:xsnow:2.0.3'`

### 版本说明
[![LatestVersion](https://img.shields.io/badge/LatestVersion-2.0.3-orange.svg)](https://github.com/xiaoyaoyou1212/XSnow/blob/master/VERSION.md)

### 代码托管
[![JCenter](https://img.shields.io/badge/JCenter-2.0.3-orange.svg)](https://jcenter.bintray.com/com/vise/xiaoyaoyou/xsnow/2.0.3/)

## 效果展示
![操作演示动画](https://github.com/xiaoyaoyou1212/XSnow/blob/master/screenshot/screenshot.gif)

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

- 支持根据Tag中途取消请求，也可以取消所有请求。

- ......

### 使用示例：

第一步需要在application中进行全局初始化以及添加全局相关配置，具体使用如下：
```
ViseHttp.init(this);
ViseHttp.CONFIG()
        //配置请求主机地址
        .baseUrl("http://192.168.1.100/")
        //配置全局请求头
        .globalHeaders(new HashMap<String, String>())
        //配置全局请求参数
        .globalParams(new HashMap<String, String>())
        //配置读取超时时间，单位秒
        .readTimeout(30)
        //配置写入超时时间，单位秒
        .writeTimeout(30)
        //配置连接超时时间，单位秒
        .connectTimeout(30)
        //配置请求失败重试次数
        .retryCount(3)
        //配置请求失败重试间隔时间，单位毫秒
        .retryDelayMillis(1000)
        //配置是否使用cookie
        .setCookie(true)
        //配置自定义cookie
        .apiCookie(new ApiCookie(this))
        //配置是否使用OkHttp的默认缓存
        .setHttpCache(true)
        //配置OkHttp缓存路径
        .setHttpCacheDirectory(new File(ViseHttp.getContext().getCacheDir(), ViseConfig.CACHE_HTTP_DIR))
        //配置自定义OkHttp缓存
        .httpCache(new Cache(new File(ViseHttp.getContext().getCacheDir(), ViseConfig.CACHE_HTTP_DIR), ViseConfig.CACHE_MAX_SIZE))
        //配置自定义离线缓存
        .cacheOffline(new Cache(new File(ViseHttp.getContext().getCacheDir(), ViseConfig.CACHE_HTTP_DIR), ViseConfig.CACHE_MAX_SIZE))
        //配置自定义在线缓存
        .cacheOnline(new Cache(new File(ViseHttp.getContext().getCacheDir(), ViseConfig.CACHE_HTTP_DIR), ViseConfig.CACHE_MAX_SIZE))
        //配置开启Gzip请求方式，需要服务器支持
        .postGzipInterceptor()
        //配置应用级拦截器
        .interceptor(new HttpLogInterceptor()
                .setLevel(HttpLogInterceptor.Level.BODY))
        //配置网络拦截器
        .networkInterceptor(new NoCacheInterceptor())
        //配置转换工厂
        .converterFactory(GsonConverterFactory.create())
        //配置适配器工厂
        .callAdapterFactory(RxJava2CallAdapterFactory.create())
        //配置请求工厂
        .callFactory(new Call.Factory() {
            @Override
            public Call newCall(Request request) {
                return null;
            }
        })
        //配置连接池
        .connectionPool(new ConnectionPool())
        //配置主机证书验证
        .hostnameVerifier(new SSLUtil.UnSafeHostnameVerifier("http://192.168.1.100/"))
        //配置SSL证书验证
        .SSLSocketFactory(SSLUtil.getSslSocketFactory(null, null, null))
        //配置主机代理
        .proxy(new Proxy(Proxy.Type.HTTP, new SocketAddress() {}));
```
后面就是具体调用请求的过程，请求的类型有多种情形，下面就以最常用的几种类型举例说明，具体效果可以查看demo，以下为使用示例：

- GET 不带缓存
```
ViseHttp.GET().suffixUrl("getAuthor").request(new ACallback<AuthorModel>() {
    @Override
    public void onSuccess(AuthorModel authorModel) {
    }

    @Override
    public void onFail(int errCode, String errMsg) {
    }
});
```
提供了多种参数的添加方式，具体可查看BaseRequest中提供的API。

- GET 带缓存
```
ViseHttp.GET()
        .suffixUrl("getAuthor")
        .setLocalCache(true)//设置是否使用缓存，如果使用缓存必须设置为true
        .cacheMode(CacheMode.FIRST_CACHE) //配置缓存策略
        .request(new ACallback<CacheResult<AuthorModel>>() {
            @Override
            public void onSuccess(CacheResult<AuthorModel> cacheResult) {
            }

            @Override
            public void onFail(int errCode, String errMsg) {
            }
        });
```
由于带缓存方式有点不一样，需要告知上层是否是缓存数据，所以需要外部包裹一层CacheResult结构，使用时必须要按照这种方式设置model，还有需要注意的是必须要设置缓存开关为true，如果为false是没法解析CacheResult结构的，这点一定切记。

- GET 返回String
```
ViseHttp.GET().suffixUrl("getString").request(new ACallback<String>() {
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
ViseHttp.GET().suffixUrl("getListAuthor").request(new ACallback<List<AuthorModel>>() {
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
ViseHttp.BASE(new ApiGetRequest()).suffixUrl("getApiResultAuthor").request(new ACallback<AuthorModel>() {
    @Override
    public void onSuccess(AuthorModel authorModel) {
    }

    @Override
    public void onFail(int errCode, String errMsg) {
    }
});
```
由于ApiResult这个结构没法通用，用的是拓展库方式提供一种解决方案，可替换成各自服务器定义的字段，具体参考netexpand库，这里使用需要通过BASE将自定义的请求方式设置进去，内部会自动去掉外部层级ApiResult，直接返回你所需的数据部分。

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
        .request(new ACallback<String>() {
            @Override
            public void onSuccess(String data) {
            }

            @Override
            public void onFail(int errCode, String errMsg) {
            }
        });
```
上传表单时需要通过addForm将键值对一个个添加进去，支持上传中文字符。

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
        .request(new ACallback<String>() {
            @Override
            public void onSuccess(String data) {
            }

            @Override
            public void onFail(int errCode, String errMsg) {
            }
        });
```
上传JSON格式数据时需要先将数据转换成JSON格式，再通过setJson添加进去。

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
        .request(new ACallback<String>() {
            @Override
            public void onSuccess(String data) {
            }

            @Override
            public void onFail(int errCode, String errMsg) {
            }
        });
```
有些POST请求可能URL后面也带有参数，这样的话需要通过addUrlParam进行设置，与添加到请求body的参数设置方式addParam是不一样的，这点需要注意。

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
        .request(new ACallback<Object>() {
    @Override
    public void onSuccess(Object data) {
    }

    @Override
    public void onFail(int errCode, String errMsg) {
    }
});
```
由于API请求成功与上传回调没法统一处理，故将请求成功与上传进度回调分离，上传进度通过UCallback告知调用者，上传进度支持拦截器返回，也支持添加文件上传时设置回调。

- 下载示例：
```
ViseHttp.DOWNLOAD()
        .baseUrl("http://dldir1.qq.com/")
        .suffixUrl("weixin/android/weixin6330android920.apk")
        .setFileName(saveName)
        .request(new ACallback<DownProgress>() {
            @Override
            public void onSuccess(DownProgress downProgress) {
            }

            @Override
            public void onFail(int errCode, String errMsg) {
            }
        });
```
通过读取返回的DownProgress获取下载进度，下载文件默认保存在该应用的/cache/download目录下。

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
如果需要定制使用其他Bus如EventBus，那么只需将实现IBus接口的对象在应用初始化时通过`BusFactory.setBus(new EventBus())`传进去即可。

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
如果需要定制使用其他图片加载框架如Fresco，那么只需将实现ILoader接口的对象在应用初始化时通过`LoaderFactory.setLoader(new FrescoLoader())`传进去即可。

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

### 注意事项

- 该框架引用了日志系统和公共工具库，这两个库都很轻量级，具体使用详情可分别参考[https://github.com/xiaoyaoyou1212/ViseLog](https://github.com/xiaoyaoyou1212/ViseLog)和[https://github.com/xiaoyaoyou1212/ViseUtils](https://github.com/xiaoyaoyou1212/ViseUtils)。

- 项目中的每个功能都附有使用示例，但示例可能不是很全，主要是提供一个使用的思路，有些现象需要通过查看日志才能看到，如数据库的增删改查操作、事件总线的通知功能等。

- ==网络访问的API调试采用的是moco服务进行处理的，项目中有提供开启该服务的命令，需要在使用时调用命令开启该服务，还有需要将应用初始化的baseurl设置为本地电脑的IP地址。(重点注意)==

- 上传文件功能由于使用的是公司内部服务器调试，所以在demo中只提供了一个使用示例，无法看到效果。

### 关于我
[![Website](https://img.shields.io/badge/Website-huwei-blue.svg)](http://www.huwei.tech/)
[![GitHub](https://img.shields.io/badge/GitHub-xiaoyaoyou1212-blue.svg)](https://github.com/xiaoyaoyou1212)
[![CSDN](https://img.shields.io/badge/CSDN-xiaoyaoyou1212-blue.svg)](http://blog.csdn.net/xiaoyaoyou1212)

### 最后
如果觉得该项目有帮助，请点下Star，您的支持是我开源的动力。如果有好的想法和建议，也欢迎Fork项目参与进来。使用中如果有任何问题和建议都可以进群交流，QQ群二维码如下：

![QQ群](http://img.blog.csdn.net/20170327191310083)

*欢迎进群交流！*

