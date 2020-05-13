package com.weike.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * @author: majin
 * @date: 2020/5/6$
 * @desc:
 */
public class PermissionUtils {

    private Activity mActivity;
    private int mReqCode;
    private CallBack mCallBack;

    public static interface CallBack {
        void grantAll();//

        void denied();
    }

    public PermissionUtils(Activity activity) {
        mActivity = activity;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void request(List<String> needPermissions, int reqCode, CallBack callback) {

        if (Build.VERSION.SDK_INT < 23) {
            callback.grantAll();
            return;
        }

        if (mActivity == null) {
            throw new IllegalArgumentException("activity is null.");
        }

        mReqCode = reqCode;
        mCallBack = callback;

        List<String> reqPermissions = new ArrayList<>();

        for (String permission : needPermissions) {
            if (mActivity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                reqPermissions.add(permission);
            }
        }

        // fixed!!!
        if (reqPermissions.isEmpty()) {
            callback.grantAll();
            return;
        }

        mActivity.requestPermissions(reqPermissions.toArray(new String[]{}), reqCode);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == mReqCode) {
            boolean grantAll = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    grantAll = false;
                    Toast.makeText(mActivity, permissions[i] + " 未授权", Toast.LENGTH_SHORT).show();
                    break;
                }
            }

            if (grantAll) {
                mCallBack.grantAll();
            } else {
                mCallBack.denied();
            }
        }

    }

}
