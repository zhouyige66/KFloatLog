## KFloatLog简介
* Android查看logcat的一个辅助工具，开发KFloatLog主要是方便未连接ADB调试的时候，直接查看自定义日志。原理是通过悬浮窗，将自定义日志显示在悬浮视图上，方便研发人员查找问题，方便测试人员定位问题。

### 其他特性
* 添加悬浮窗授权请求页面。
* 应用外悬浮视图的包装与使用。
* 后期会增加Logback选项，供开发者自定义是否将日志存储到本地。

### 使用
#### Gradle配置:
```javascript
repositories {
	...
	maven { url 'https://jitpack.io' }
}
dependencies {
	implementation 'com.github.zhouyige66:KFloatLog:1.0.4'
}
```

#### 代码中使用
* 使用FloatLogUtil.bind(Context context)先关联context，建议在Application的onCreate()方法中调用一次即可。
* 使用以下方法输入日志到悬浮窗显示。
```java
// 配置是否自动保存日志到文件，默认是true
FloatLogUtil.setSyncSaveLog(boolean sync)
// 添加日志
FloatLogUtil.v(String tag,String msg)
FloatLogUtil.d(String tag,String msg)
FloatLogUtil.i(String tag,String msg)
FloatLogUtil.w(String tag,String msg)
FloatLogUtil.e(String tag,String msg)
// 关闭悬浮窗
FloatLogUtil.close();
```

----
### 关于作者
* Email： <751664206@qq.com>
* 有任何建议或者使用中遇到问题都可以给我发邮件。
