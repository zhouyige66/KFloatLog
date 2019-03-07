## KFloatLog简介
* KFloatLog主要用于显示的查看自定义日志，通过悬浮窗，直接可查看应用运行过程中的自定义日志，可脱离ADB连接，方便研发人员查找问题，方便测试人员定位问题。

### 其他特性
* 添加悬浮窗授权请求页面。
* 应用外悬浮视图的包装与使用。
* 后期会增加Log4j选项，供开发者自定义是否将日志存储到本地。

### 使用
#### Gradle配置:
```javascript
repositories {
	...
	maven { url 'https://jitpack.io' }
}
dependencies {
	implementation 'com.github.zhouyige66:KFloatLog:1.0.3'
}
```

#### 代码中使用
* 使用FloatLogUtil.bind(Context context)先关联context，建议在Application的onCreate()方法中调用一次即可。
* 使用以下方法输入日志到悬浮窗显示。
```java
FloatLogUtil.v(String tag,String msg)
FloatLogUtil.d(String tag,String msg)
FloatLogUtil.i(String tag,String msg)
FloatLogUtil.w(String tag,String msg)
FloatLogUtil.e(String tag,String msg)
```

----
### 关于作者
* Email： <751664206@qq.com>
* 有任何建议或者使用中遇到问题都可以给我发邮件。
