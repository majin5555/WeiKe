package baseLibrary.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.baselibrary.R;

/**
 * @author: mj
 * @date: 2019/6/21$
 * @desc: 图片工具类  增加水印
 */
public class ImageUtils {


    /**
     * 设置水印图片在左上角
     */
    public static Bitmap createWaterMaskLeftTop(Context context, Bitmap src, Bitmap watermark,
                                                int paddingLeft, int paddingTop) {
        return createWaterMaskBitmap(src, watermark, dp_to_px(context, paddingLeft),
                dp_to_px(context, paddingTop));
    }

    /**
     * 设置水印图片到右上角
     */
    public static Bitmap createWaterMaskRightTop(
            Context context, Bitmap src, Bitmap watermark,
            int paddingRight, int paddingTop) {
        return createWaterMaskBitmap(src, watermark,
                src.getWidth() - watermark.getWidth() - dp_to_px(context, paddingRight),
                dp_to_px(context, paddingTop));
    }

    /**
     * 设置水印图片到中间
     */
    public static Bitmap createWaterMaskCenter(Bitmap src, Bitmap watermark) {
        return createWaterMaskBitmap(src, watermark,
                (src.getWidth() - watermark.getWidth()) / 2,
                (src.getHeight() - watermark.getHeight()) / 2);
    }

    /**
     * 设置水印图片到左下角
     */
    public static Bitmap createWaterMaskLeftBottom(
            Context context, Bitmap src, Bitmap watermark,
            int paddingLeft, int paddingBottom) {
        return createWaterMaskBitmap(src, watermark, dp_to_px(context, paddingLeft),
                src.getHeight() - watermark.getHeight() - dp_to_px(context, paddingBottom));
    }

    /**
     * 设置水印图片在右下角
     */
    public static Bitmap createWaterMaskRightBottom(
            Context context, Bitmap src, Bitmap watermark,
            int paddingRight, int paddingBottom) {
        return createWaterMaskBitmap(src, watermark,
                src.getWidth() - watermark.getWidth() - dp_to_px(context, paddingRight),
                src.getHeight() - watermark.getHeight() - dp_to_px(context, paddingBottom));
    }

    /**
     * 添加图片水印
     *
     * @param bitmap
     * @param watermark
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    private static Bitmap createWaterMaskBitmap(Bitmap bitmap, Bitmap watermark, int paddingLeft, int paddingTop) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //创建一个bitmap
        Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        //将该图片作为画布
        Canvas canvas = new Canvas(newb);
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(bitmap, 0, 0, null);
        //在画布上绘制水印图片
        canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
        // 保存
        canvas.save();
        // 存储
        canvas.restore();
        return newb;
    }

    /**
     * 给图片添加文字到左上角
     */
    public static Bitmap drawTextToLeftTop(Context context, Bitmap bitmap, String text,
                                           int size, int color, int paddingLeft, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(dp_to_px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                dp_to_px(context, paddingLeft),
                dp_to_px(context, paddingTop) + bounds.height(), size);
    }

    /**
     * 给图片添加文字到右上角
     */
    public static Bitmap drawTextToRightTop(Context context, Bitmap bitmap, String text,
                                            int size, int color, int paddingRight, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(dp_to_px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                bitmap.getWidth() - bounds.width() - dp_to_px(context, paddingRight),
                dp_to_px(context, paddingTop) + bounds.height(), size);
    }

    /**
     * 给图片添加文字到中间
     */
    public static Bitmap drawTextToCenter(Context context, Bitmap bitmap, String text,
                                          int size ) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(context.getResources().getColor(R.color.black_14191d));
        paint.setTextSize(dp_to_px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                (bitmap.getWidth() - bounds.width()) / 2,
                (bitmap.getHeight() + bounds.height()) / 2, size);
    }

    /**
     * 给图片添加文字到左下角
     */
    public static Bitmap drawTextToLeftBottom(Context context, Bitmap bitmap, String text,
                                              int size, int color, int paddingLeft, int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(dp_to_px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                dp_to_px(context, paddingLeft),
                bitmap.getHeight() - dp_to_px(context, paddingBottom), size);
    }

    /**
     * 给图片添加文字到右下角
     */
    @SuppressLint("ResourceAsColor")
    public static Bitmap drawTextToRightBottom(Context context, Bitmap bitmap, String text,
                                               int size, int paddingRight, int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(context.getResources().getColor(R.color.black_14191d));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(size);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds, bitmap.getWidth() - bounds.width() - dp_to_px(context, paddingRight), bitmap.getHeight() - dp_to_px(context, paddingBottom), size);
    }


    /**
     * @param context
     * @param bitmap
     * @param text
     * @param paint
     * @param bounds
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    @SuppressLint("ResourceAsColor")
    private static Bitmap drawTextToBitmap(Context context, Bitmap bitmap, String text,
                                           Paint paint, Rect bounds, int paddingLeft, int paddingTop, int size) {
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.RGB_565;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        RectF rect = new RectF();
        rect.set(paddingLeft, paddingTop - size, bitmap.getWidth() - 10, bitmap.getHeight() - 10);
        paint.setColor(context.getResources().getColor(R.color.white_FFFFFF));
        canvas.drawRoundRect(rect, 12, 12, paint);//绘制背景

        paint.setColor(context.getResources().getColor(R.color.black_14191d));
        paint.setStyle(Paint.Style.FILL);//实心文字
        paint.setFakeBoldText(true);
        canvas.drawText(text, paddingLeft+2, paddingTop+8, paint);//绘制文字
        return bitmap;
    }


    /**
     * dip转pix
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp_to_px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
