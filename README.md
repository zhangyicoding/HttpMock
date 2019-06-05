# HttpMock

### 诞生由来：
- 1/ 有时UI写好了，后台接口还没出来，但json结构定好了，我们不必等，可以使用假数据模拟后台接口
- 2/ 有时接口参数的不同决定了json内容也不同，比如：登陆个人或企业账户会得到不同的json结果，为了方便测试，直接修改假数据即可，节省了切换账户的时间。（该功能有待改进）

### 不用HttpMock可不可以？
肯定可以，但如果有假数据的需求，我们只能把假数据写进业务代码中，改来改去，影响业务代码的维护。
使用HttpMock可以做到业务代码零入侵，而且release版毫不影响性能。

### 使用：
- 0/ 一些注意事项：</br>
一，首先声明，该框架只针对于retrofit方式的网络请求。</br>
    二，目前gradle 5.1.1版本无法正常工作，gradle 4.10.1是可以的。猜测gradle 5在AnnotationProcessor生成java文件时都不会生效，期待和大神们讨论解决。</br>
    三，demo使用了RxJava和Kotlin等其他技术，这里只展示HttpMock相关的代码片段。

- 1/ 导包。</br>
在app/build.gradle中，添加如下配置：

```java
// 导入该插件可以在release版本中删除assets路径下debug用的json文件，墙裂建议保留
apply from: 'httpmock.gradle'

dependencies {
    // HttpMock
    debugImplementation project(path: ':httpmock')
    releaseImplementation project(path: ':httpmock_no_op')
    kaptDebug project(path: ':httpmock_processor')// java使用debugAnnotationProcessor
}
```

httpmock.gradle文件在demo的app/路径中，目前HttpMock还暂时只是本地module形式的dependencies，如何实现上传到jcenter的代码还可以用apply plugin 'balabala'插件这种方式，还请大神们不吝赐教，这也是代码暂时是本地module，没上传到jcenter的根本原因。

- 2/ 假数据放在哪不会影响业务代码，前面说到的assets文件夹是个不错的选择，所以我们在assets/httpmock_debug/这个文件夹中存放假数据文件。</br>

文件格式没要求，但httpmock_debug这个文件夹名字是固定的。

assets/httpmock_debug/dish.json

```java
{
  "ret": 1,
  "data": [
    {
      "id": "1234",
      "collect_num": "5678",
      "title": "爱的猪大哥",
      "food_str": "罗志祥",
      "pic": "https:\/\/www.baidu.com\/img\/bd_logo1.png",
      "num": 666
    }
  ]
}
```

这内容随意程度，肯定是我们自己随便写的，但json的字段记得和后台接口提供的json要一致。

- 3/ 对retrofit对应HttpService的方法使用@HttpMock注解，声明fileName指定假数据文件名，这样假数据文件就和@GET或@POST中的url地址建立了对应关系。目前仅支持@GET和@POST这两个注解。

```java
    interface DishService {

        @HttpMock(fileName = "dish.json", enable = true)
        @GET("ios/cf/dish_list.php?stage_id=1&limit=10")
        fun getDish(@Query("page") page: Int): Observable<DishEntity>
    }
```

   @HttpMock的假数据功能默认是启用的，可以省略enable = true，如果false，则关闭当前网络请求的假数据功能，提升测试场景的灵活性，当然啦，release版是强制关闭假数据功能的，enable是啥都无所谓了。


- 4/ 当debug版至少使用一处@HttpMock后，在Android Studio -> Build -> Rebuild Project（或make应该也行）一下，就生成了一个类HttpMockGenerator，它的作用是保存全部@HttpMock的信息。

我们在OkHttp的配置中添加如下代码：

```java
        val okHttpClientBuilder = OkHttpClient.Builder()

        // 添加HttpMockInterceptor后依然返回OkHttpClient.Builder
        val okHttpClient = HttpMock.addHttpMockInterceptor(context, okHttpClientBuilder, HttpMockGenerator::class.java)
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .build()
```
到此全部结束了。我们通过okhttp的Interceptor实现了请求和响应的拦截，这样就可以加载假数据了，还是那句话，在release版中一个@HttpMock都不写也ok，但在debug版中，为了能够自动生成HttpMockGenerator，至少要写一处@HttpMock。</br>
还有诸多地方有待改良，欢迎大家一起讨论。