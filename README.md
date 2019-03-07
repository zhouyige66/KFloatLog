## KFloatLog简介
* KFloatLog主要用于显示的查看自定义日志，通过悬浮窗，直接可查看应用运行过程中的自定义日志，可脱离ADB连接，方便研发人员查找问题，方便测试人员定位问题。

### 其他特性
* 应用中已添加悬浮窗授权请求页面。
* WindManager使用可供参考。
* 后期会增加Log4j选项，供开发者自定义是否将日志存储到本地。

#### 使用Gradle构建:
```javascript
repositories {
	...
	maven { url 'https://jitpack.io' }
}
dependencies {
	implementation 'com.github.zhouyige66:KFloatLog:1.0.3'
}
```

#### 使用前配置
##### 需要的权限
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
##### 初始化
```java
// 在application的onCreate中初始化
@Override
public void onCreate() {
    super.onCreate();
    FloatUtil.init(this);
    FloatUtil.setDebug(BuildConfig.DEBUG); // 是否显示悬浮窗日志.
    ...
}
```

----
### 关于作者
* Email： <751664206@qq.com>
* 有任何建议或者使用中遇到问题都可以给我发邮件, '*_*'.
