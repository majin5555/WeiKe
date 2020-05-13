//package com.weike.contral;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//
//
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//
//
///*
//*截取图片的工具类
//* **/
//public class BitmapUtil {
//    @SuppressWarnings("unused")
//    private SketchpadView canvasView;
//
//    //                                                  左minX        上minY        右maxX    下 maxY
//    public static Bitmap duplicateBitmap(Bitmap bmpSrc, float left, float top, float right, float bottom) {
//        if (null == bmpSrc) {
//            return null;
//        }
//
//        Bitmap bmpDest = null;
//        //当bmpDest=null时
//        if (left == 0 && top == 0 && right == 0 && bottom == 0) {//全都等于0的情况下
//            bmpDest = Bitmap.createBitmap(SketchpadView.BITMAP_WIDTH, SketchpadView.BITMAP_HEIGHT, Bitmap.Config.ARGB_8888);
//        }
//
//        /*x：剪裁x方向的起始位置;    y：剪裁y方向的起始位置;    width：剪裁的宽度;   height：剪裁的高度;*/
//        else {
//            /**2016.12.20
//             * Auther:majin
//             * 修改在超出边界后报错问题
//             *
//             * */
//            float r_l = right - left;
//            float b_t = bottom - top;
//
//            if (left < 0) {
//                left = 0;
//            }
//            if (top < 0) {
//                top = 0;
//            }
//            if (left + r_l > bmpSrc.getWidth()) {
//                r_l = bmpSrc.getWidth() - left;
//            }
//            if (top + b_t > bmpSrc.getHeight()) {
//                b_t = bmpSrc.getHeight() - top;
//            }
//            bmpDest = Bitmap.createBitmap(bmpSrc, (int) left, (int) top, (int) (r_l), (int) (b_t));
//        }
//
//        if (bmpDest != null) {
//            Canvas canvas = new Canvas(bmpDest);
//            canvas.save();
//            canvas.clipRect((int) left, (int) top, (int) right, (int) bottom);
//            canvas.restore();
//        }
//
//        return bmpDest;
//    }
//
//    public static Bitmap byteArrayToBitmap(byte[] array) {
//        if (null == array) {
//            return null;
//        }
//
//        return BitmapFactory.decodeByteArray(array, 0, array.length);
//    }
//
//    public static byte[] bitampToByteArray(Bitmap bitmap) {
//        byte[] array = null;
//        try {
//            if (null != bitmap) {
//                ByteArrayOutputStream os = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
//                array = os.toByteArray();
//                os.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return array;
//    }
//
//    /**
//     * 保存方法
//     */
//    public static void saveBitmaptoTemporaryPicture(Bitmap bm, String path, String picname) {
//
//
//        File f = new File(path + picname + ".png");
//        FileOutputStream fOut = null;
//        try {
//            fOut = new FileOutputStream(f);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        if (bm != null) {
//            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//        }
//        try {
//            fOut.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            fOut.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public static void saveBitmapToSDCard(Bitmap bmp, String strPath) {
//        if (null != bmp && null != strPath && !strPath.equalsIgnoreCase("")) {
//            try {
//                File file = new File(strPath);
//                FileOutputStream fos = new FileOutputStream(file);
//                byte[] buffer = BitmapUtil.bitampToByteArray(bmp);
//                fos.write(buffer);
//                fos.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    public static Bitmap loadBitmapFromSDCard(String strPath) {
//        File file = new File(strPath);
//
//        try {
//            FileInputStream fis = new FileInputStream(file);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = false;
//            options.inSampleSize = 2;   //width��hight��Ϊԭ���Ķ���һ
//            Bitmap btp = BitmapFactory.decodeStream(fis, null, options);
//            return btp;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//}
