package baseLibrary.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.baselibrary.R;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import baseLibrary.dialog.PromptDialog;
import baseLibrary.toast.ToastUtil;
import baseLibrary.util.StatusBarCompat;

/**
 * @author: majin
 * @date: 2019/6/24$
 * @desc: 基础Activity
 */
public abstract class BaseActivity extends Activity {
    public final int               exitApp = - 1;
    private      SparseArray<View> mViews  = new SparseArray<View>();
    protected    Toolbar           toolbar;
    public       Activity          context;
    public       PromptDialog      promptDialog;//加载框
    public       Boolean           isExit  = false;

    public TextView titleView;//标题
    public TextView tv_right;//左侧

    @SuppressLint("HandlerLeak")
    public Handler mBasehandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case exitApp:
                    isExit = false;
                    break;
                default:
                    break;
            }
        }
    };


    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.getInstance().addActivity(this);//将Activity添加到堆栈
        context = this;
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //硬件加速
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        //创建对象
        promptDialog = new PromptDialog(this);
        //设置自定义属性
        promptDialog.getDefaultBuilder().touchAble(false).round(3).loadingDuration(1000);
        initView();
        toolbar = getView(R.id.toolbar);
        //setStatusBar(toolbar);

        titleView = getView(R.id.tool_title);
        if (toolbar != null) {
            toolbar.setTitle("");
            //   setSupportActionBar(toolbar);
            toolbar.setOnMenuItemClickListener(onMenuItemClick);
        }
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            setFxOnMenuItemClick(menuItem);
            return true;
        }
    };

    protected void setFxOnMenuItemClick(MenuItem menuItem) {

    }


    public abstract void initView();


    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public void setStatusBar(Toolbar toolbar) {
        StatusBarCompat.initWindowParameter(this);
        // StatusBarCompat.compat(this, getResources().getColor(R.color.app_style));
    }

    /** 显示Toast */
    public void showToast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToast(context, str);
                if (promptDialog != null) dismissDialog();
            }
        });
    }


    public void showDialog() {
        promptDialog.showLoading("请稍等...");
    }

    /** 自定义文字 */
    public void showDialog(final String str) {
        promptDialog.showLoading(str);
    }

    public void dismissDialog() {
        promptDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.base_slide_right_in,
                R.anim.base_slide_remain);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void finishActivity() {
        dismissDialog();
        ActivityUtil.getInstance().finishThisActivity(this);
        if (setOnBackAnim()) {
            finishAnim();
        }
    }

    /**
     * 设置是否开关闭动画
     *
     * @return
     */
    protected boolean setOnBackAnim() {
        return true;
    }

    protected void finishAnim() {
        overridePendingTransition(0, R.anim.base_slide_right_out);
    }


    @Override
    protected void onDestroy() {
        dismissDialog();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //        hintKeyBoard();
        //        finishActivity();
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {
            //非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();
            //设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    public void hintKeyBoard() {
        //拿到InputMethodManager
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //如果window上view获取焦点 && view不为空
        if (imm.isActive() && getCurrentFocus() != null) {
            //拿到view的token 不为空
            if (getCurrentFocus().getWindowToken() != null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    //设置标题左边的图标
    @SuppressLint("NewApi")
    public void setLeftIco(int ids, View.OnClickListener onclick) {
        if (toolbar != null) {
            if (ids == 0) {
                Drawable navigationIcon = toolbar.getNavigationIcon();
                navigationIcon.setAlpha(0);
                toolbar.setNavigationOnClickListener(null);
                return;
            }
            toolbar.setNavigationIcon(ids);
            toolbar.setNavigationOnClickListener(onclick);
        }
    }


    @SuppressLint("NewApi")
    public void onBack() {
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ico_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishActivity();
                }
            });
        }
    }

    /* 返回图片*/
    public void onBackText() {
        if (toolbar != null) {
            TextView tv = getView(R.id.tool_left);
            tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_back, 0, 0, 0);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    /*返回 自定义图片*/
    public TextView onBackText(int resourceId, int text) {
        TextView tv = null;
        if (toolbar != null) {
            tv = getView(R.id.tool_left);
            tv.setText(text);
            tv.setCompoundDrawablesWithIntrinsicBounds(resourceId, 0, 0, 0);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        return tv;
    }

    /*右上角自定义文字加图片*/
    public void onRightBtn(int drawableId, int textId) {
        if (toolbar != null) {
            TextView tv = getView(R.id.tool_right);
            tv.setText(textId);
            tv.setVisibility(View.VISIBLE);
            tv.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRightBtnClick(v);
                }
            });
        }
    }

    /*右上角 自定义文字*/
    public void onRightBtnText(int textId) {
        if (toolbar != null) {
            tv_right = getView(R.id.tool_right);
            tv_right.setText(textId);
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRightBtnClick(v);
                }
            });
        }
    }

    /*右上角 自定义图片*/
    public TextView onRightBtnPic(int drawableId) {
        if (toolbar != null) {
            tv_right = getView(R.id.tool_right);
            //   tv_right.setText(textId);
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
            tv_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRightBtnClick(v);
                }
            });
        }
        return tv_right;
    }

    /**
     * 设置右侧按钮的文字及颜色
     *
     * @param textId
     * @param textColor
     */
    public void onRightText(int textId, int textColor) {
        if (toolbar != null) {
            TextView tv = getView(R.id.tool_right);
            tv.setTextColor(textColor);
            tv.setText(textId);
            tv.setVisibility(View.VISIBLE);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRightBtnClick(v);
                }
            });
        }
    }

    public void onRightBtnClick(View view) {

    }

    public void rightBtHine() {
        if (toolbar != null) {
            TextView tv = getView(R.id.tool_right);
            if (tv != null)
                tv.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NewApi")
    public void onBack(int resid) {
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ico_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishActivity();
                }
            });
        }
    }

    /**
     * 设置了 onBack 后 如果要隐藏按钮 就调用这个方法就可以了。 显示的话 还是在调用onBack()方法
     */
    @SuppressLint("NewApi")
    public void hideBack() {
        Drawable nullDrawable = null;
        toolbar.setNavigationIcon(nullDrawable);
    }

    protected void setTtitle(String title) {
        if (titleView != null)
            titleView.setText(title);
    }

    protected void setTtitle(String title, int color) {
        if (titleView != null)
            titleView.setTextColor(color);
        titleView.setText(title);
    }

    protected void setTitleOnclick(View.OnClickListener listener) {
        titleView.setOnClickListener(listener);
    }

    protected void setTtitle(int res) {
        if (titleView != null) {
            titleView.setText(res);
        }
    }
}
