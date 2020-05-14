package baseLibrary.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import com.orhanobut.logger.Logger;

import java.util.Stack;

import androidx.annotation.RequiresApi;

/**
 * @author djh-majin
 * @version :1
 * @CreateDate 2018年8月7日
 * @description : Activity管理工具类
 */
public class ActivityUtil {

    private Stack<Activity> activityStack;
    private static ActivityUtil activityUtil;

    private ActivityUtil() {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
    }

    /**
     * 单一实例
     */
    public static ActivityUtil getInstance() {
        if (activityUtil == null) {
            synchronized (ActivityUtil.class) {
                if (activityUtil == null) {
                    activityUtil = new ActivityUtil();
                }
            }
        }
        return activityUtil;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity getCurrentActivity() {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        Activity activity = activityStack.lastElement();
        return activity;
    }


    /**
     * 结束指定的Activity
     */
    public void finishThisActivity(Activity activity) {
        if (activity != null && activityStack != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        finishThisActivity(getActivity(cls));
    }

    /**
     * 获取指定activity
     *
     * @param cls
     * @return
     */
    public Activity getActivity(Class<?> cls) {
        Activity finishActivity = null;
        if (activityStack != null) {
            for (Activity activity : activityStack) {
                if (activity.getClass().getName().equals(cls.getName())) {
                    finishActivity = activity;
                }
            }
        }
        return finishActivity;
    }

    /**
     * 关闭所有的App
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            android.os.Process.killProcess(android.os.Process.myPid());
            Logger.d("System.exit(0)");
            System.exit(0);

        } catch (Exception e) {

        }
    }

}
