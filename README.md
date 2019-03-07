## KFloatLog简介
* KFloatLog主要用于显示的查看自定义日志，通过悬浮窗，直接可查看应用运行过程中的自定义日志，可脱离ADB连接，方便研发人员查找问题，方便测试人员定位问题。

### 其他特性
* 支持超大文件(超过2G)上传
* 更全面的http请求协议支持(11种谓词)
* 拥有更加灵活的ORM, 和greenDao一致的性能
* 更多的事件注解支持且不受混淆影响...
* 图片绑定支持gif(受系统兼容性影响, 部分gif文件只能静态显示), webp; 支持圆角, 圆形, 方形等裁剪, 支持自动旋转...
* 从3.5.0开始不再包含libwebpbackport.so, 需要在Android4.2以下设备兼容webp的请使用3.4.0版本.

#### 使用Gradle构建:
```javascript
repositories {
	...
	maven { url 'https://jitpack.io' }
}
dependencies {
	implementation 'com.github.zhouyige66:KFloatLog:Tag'
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
* 有任何建议或者使用中遇到问题都可以给我发邮件, *_*
