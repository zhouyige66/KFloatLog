package cn.kk20.floatlog.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/3/8 11:14
 * @Version: v1.0
 */
public class AppFileUtil {

    private AppFileUtil() {

    }

    public static boolean isSDCardExit() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取应用主目录存储路径
     *
     * @param context
     * @return 末尾以"/"结尾
     */
    public static String getAppStoragePath(Context context) {
        String path;
        if (isSDCardExit()) {
            String pkgName = context.getPackageName();
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (path.endsWith(File.separator)) {
                path = path + pkgName;
            } else {
                path = path + File.separator + pkgName;
            }
        } else {
            path = Environment.getDataDirectory().getAbsolutePath();
        }
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        return path;
    }

    /**
     * 获取目录路径（包含创建文件夹操作）
     *
     * @param context
     * @param dirName 格式为"/content/img"，"/content/img/"，"content/img","content/img/"
     * @return "/storage/emulated/0/content/img/"
     */
    public static String getDirPath(Context context, String dirName) {
        String appFilePath = getAppStoragePath(context);
        String dirPath;
        if (dirName.startsWith(File.separator)) {
            dirPath = appFilePath + dirName.substring(1);
        } else {
            dirPath = appFilePath + dirName;
        }
        if (!dirPath.endsWith(File.separator)) {
            dirPath = dirPath + File.separator;
        }
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        return dirPath;
    }

}
