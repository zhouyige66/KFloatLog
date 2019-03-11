package cn.kk20.floatlog;

import android.content.Context;
import android.content.Intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import cn.kk20.floatlog.bean.LogItemBean;
import cn.kk20.floatlog.component.AlertWindowPermissionGrantActivity;
import cn.kk20.floatlog.component.LogService;
import cn.kk20.floatlog.util.AppFileUtil;
import cn.kk20.floatlog.util.AppOpsManagerUtil;

/**
 * @Description 悬浮日志工具
 * @Author kk20
 * @Date 2018/4/16
 * @Version V1.0.0
 */
public class FloatLogUtil {
    private static Context appContext = null;
    private static ConcurrentHashMap<String, Logger> loggerMap = new ConcurrentHashMap<>();
    private static boolean syncSaveLog = true;

    private FloatLogUtil() {

    }

    private static void configLogback(Context context) {
        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.stop();

        // setup FileAppender
        PatternLayoutEncoder encoder1 = new PatternLayoutEncoder();
        encoder1.setContext(lc);
        encoder1.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        encoder1.start();
        // 日志文件夹
        String logFilePath = AppFileUtil.getDirPath(context, "log");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
        // 循环存储
        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
        fileAppender.setContext(lc);
        fileAppender.setName("RollingFileAppender");
        fileAppender.setFile(logFilePath + format.format(new Date()) + File.separator + "log.log");
        fileAppender.setPrudent(false);
        fileAppender.setAppend(true);
        // 日志保存策略配置
        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy =
                new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(lc);
        rollingPolicy.setFileNamePattern(logFilePath + "%d{yyyy-MM-dd}" + File.separator
                + "log-%i.log");
        rollingPolicy.setMaxFileSize(FileSize.valueOf("50kb"));
        rollingPolicy.setTotalSizeCap(FileSize.valueOf("1gb"));
        rollingPolicy.setMaxHistory(5);
        rollingPolicy.setParent(fileAppender);
        rollingPolicy.start();
        fileAppender.setRollingPolicy(rollingPolicy);
        fileAppender.setEncoder(encoder1);
        fileAppender.start();

        // setup LogcatAppender
        PatternLayoutEncoder encoder2 = new PatternLayoutEncoder();
        encoder2.setContext(lc);
        encoder2.setPattern("[%thread] %msg%n");
        encoder2.start();
        LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(lc);
        logcatAppender.setEncoder(encoder2);
        logcatAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)
                LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(fileAppender);
        root.addAppender(logcatAppender);
        root.setLevel(Level.DEBUG);
    }

    private static void addLog(int level, String tag, String msg) {
        if (syncSaveLog) {
            Logger logger = loggerMap.get(tag);
            if (logger == null) {
                logger = LoggerFactory.getLogger(tag);
                loggerMap.put(tag, logger);
            }
            // 存储日志到本地
            switch (level) {
                case LogItemBean.DEBUG:
                    logger.debug(msg);
                    break;
                case LogItemBean.INFO:
                    logger.info(msg);
                    break;
                case LogItemBean.WARN:
                    logger.warn(msg);
                    break;
                case LogItemBean.ERROR:
                    logger.error(msg);
                    break;
                default:
                    logger.trace(msg);
                    break;
            }
        }

        if (appContext == null) {
            throw new RuntimeException("FloatLogUtil未绑定Context，请先调用bind(Context context)！");
        }

        if (!AppOpsManagerUtil.checkDrawOverlays(appContext)) {
            Intent i = new Intent(appContext, AlertWindowPermissionGrantActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            appContext.startActivity(i);
            return;
        }

        LogItemBean bean = new LogItemBean(level, tag, msg);
        Intent intent = new Intent(appContext, LogService.class);
        intent.putExtra("data", bean);
        appContext.startService(intent);
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void bind(Context context) {
        appContext = context.getApplicationContext();
        configLogback(context);
    }

    public static void close() {
        if (appContext == null) {
            return;
        }
        Intent intent = new Intent(appContext, LogService.class);
        appContext.stopService(intent);
    }

    public static boolean isSyncSaveLog() {
        return syncSaveLog;
    }

    public static void setSyncSaveLog(boolean sync) {
        syncSaveLog = sync;
    }

    public static void v(String tag, String msg) {
        addLog(LogItemBean.VERBOSE, tag, msg);
    }

    public static void d(String tag, String msg) {
        addLog(LogItemBean.DEBUG, tag, msg);
    }

    public static void i(String tag, String msg) {
        addLog(LogItemBean.INFO, tag, msg);
    }

    public static void w(String tag, String msg) {
        addLog(LogItemBean.WARN, tag, msg);
    }

    public static void e(String tag, String msg) {
        addLog(LogItemBean.ERROR, tag, msg);
    }

}
