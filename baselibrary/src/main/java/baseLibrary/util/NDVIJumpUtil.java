package baseLibrary.util;

import android.content.Context;


/**
 * @author zaws-majin
 * @version :1
 * @CreateDate 2018年8月7日
 * @description :跳转工具类
 */
public class NDVIJumpUtil extends JumpUtil {
    private static NDVIJumpUtil nUtil;



    private NDVIJumpUtil() {
    }

    /**
     * 单一实例
     */
    public static NDVIJumpUtil getInstance() {
        if (nUtil == null) {
            synchronized (NDVIJumpUtil.class) {
                if (nUtil == null) {
                    nUtil = new NDVIJumpUtil();
                }
            }
        }
        return nUtil;
    }


    /**
     * 访问网页
     *
     * @param context
     * @param title
     * @param Url
     */
    public void startWebActivity(Context context, String title, String Url, Boolean type) {
        //  startBaseWebActivity(context, title, Url, type, WebActivity.class);
    }

    /**
     * 访问网页
     *
     * @param context
     * @param title
     * @param Url
     */
    public void startBaseWebActivity(Context context, String title, String Url, Boolean type, Class classs) {
        //        Bundle bundle = new Bundle();
        //        bundle.putString(Constant.bundle_title, title);
        //        bundle.putString(Constant.bundle_obj, Url);
        //        bundle.putBoolean(Constant.bundle_type, type);
        //        startBaseActivity(context, classs, bundle, 0);
    }


}