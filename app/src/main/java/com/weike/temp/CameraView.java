//package com.weike.temp;//package com.weike.customview;
//
//import android.content.Context;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.widget.Toast;
//
//public class CameraView extends View {
//
//
//    public CameraView(Context context) {
//        super(context);
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        try {
//            if (mCamera == null) {
//                openCamera();
//            }
//            if (null != mCamera) {
//                mCamera.setPreviewDisplay(mSurfaceHolder);//Camera屏幕通过SurfaceHolder与SurfaceView 进行绑定
//                mCamera.startPreview();//开始预览
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(MainActivity.this, "打开相机失败", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//        mScreenWidth = width;
//        mScreenHeight = height;
//        setCameraParameters();
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        releaseCameraResource();
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        try {
//            if (mCamera == null) {
//                openCamera();
//            }
//            if (null != mCamera) {
//                mCamera.setPreviewDisplay(mSurfaceHolder);//Camera屏幕通过SurfaceHolder与SurfaceView 进行绑定
//                mCamera.startPreview();//开始预览
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(MainActivity.this, "打开相机失败", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//        mScreenWidth = width;
//        mScreenHeight = height;
//        setCameraParameters();
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        releaseCameraResource();
//    }
//}