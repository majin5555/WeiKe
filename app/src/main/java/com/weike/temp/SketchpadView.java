//package com.weike.customview;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.Surface;
//import android.view.View;
//
//import com.weike.contral.BitmapCtl;
//import com.weike.contral.BitmapUtil;
//import com.weike.contral.Circlectl;
//import com.weike.contral.ClearMempry;
//import com.weike.contral.EraserCtl;
//import com.weike.contral.LineCtl;
//import com.weike.contral.OvaluCtl;
//import com.weike.contral.PenuCtl;
//import com.weike.contral.PlygonCtl;
//import com.weike.contral.RectuCtl;
//import com.weike.contral.Spraygun;
//import com.weike.data.CommonDef;
//import com.weike.interfaces.ISketchpadDraw;
//import com.weike.interfaces.IUndoRedoCommand;
//
//import java.util.ArrayList;
//
//
///**
// * function:
// * 自定义画板View
// */
//public class SketchpadView extends View implements IUndoRedoCommand {
//
//    public static final int STROKE_PEN      = 12;
//    public static final int STROKE_ERASER   = 2;
//    public static final int STROKE_PLYGON   = 10;
//    public static final int STROKE_RECT     = 9;
//    public static final int STROKE_CIRCLE   = 8;
//    public static final int STROKE_OVAL     = 7;
//    public static final int STROKE_LINE     = 6;
//    public static final int STROKE_SPRAYGUN = 5;
//    public static final int STROKE_PAINTPOT = 4;
//    public              int flag            = 0;
//
//    public static final int UNDO_SIZE     = 90;       //回退次数
//    public static final int BITMAP_WIDTH  = 1280;    //备份图片的宽
//    public static final int BITMAP_HEIGHT = 600;    //备份图片的高
//
//    private        int m_strokeType  = STROKE_PEN;   //  画笔状态
//    private static int m_strokeColor = Color.RED;   //画笔颜色红色
//    private static int m_penSize     = CommonDef.SMALL_PEN_WIDTH;// 画笔尺寸（默认为6）
//    private static int m_eraserSize  = CommonDef.LARGE_ERASER_WIDTH;   //橡皮默认宽度
//
//    private boolean m_isEnableDraw = true;   //是否是可画状态
//    //    private boolean m_isDirty = false;
//    private boolean m_isTouchUp    = false;    //是否抬起
//    private boolean m_isSetForeBmp = false;
//
//
//    private int     m_canvasWidth  = 100;
//    private int     m_canvasHeight = 100;
//    private boolean m_canClear     = true;
//
//    private Bitmap m_foreBitmap     = null;
//    private Bitmap m_tempForeBitmap = null;
//    private Bitmap m_bkBitmap       = null;
//
//    private Canvas             m_canvas;     //画布
//    private Paint              m_bitmapPaint = null;   //画笔
//    private SketchPadUndoStack m_undoStack   = null;
//    private ISketchpadDraw     m_curTool     = null;   //接口（绘制各种图形时都继承了接口）
//
//    private int     antiontemp = 0;
//    private boolean myLoop     = false;
//    private float   minX, minY, maxX, maxY;//浮点行的最小X ，Y坐标
//
//    public static Surface mIpnutSurface;
//    private       Thread  captureBitmapThread;
//    private       Boolean isFirstDown = true;//第一次按下标记
//    private       Context context;
//
//
//    //    public void setDrawStrokeEnable(boolean isEnable) {
//    //        m_isEnableDraw = isEnable;
//    //    }
//
//    //    //调整画笔粗细的静态类
//    //    public static void setStrokeSize(int size, int type) {
//    //        switch (type) {
//    //            case STROKE_PEN:
//    //                m_penSize = size;
//    //                break;
//    //
//    //            case STROKE_ERASER:
//    //                m_eraserSize = size;
//    //                break;
//    //        }
//    //    }
//
//    //设置颜色默认为红色
//    public static void setStrokeColor(int color) {
//        m_strokeColor = color;
//    }
//
//    //画笔宽度
//    public static int getStrokeSize() {
//        return m_penSize;
//    }
//
//    //橡皮宽度
//    public static int getEraser() {
//        return m_eraserSize;
//    }
//
//    //获取颜色默认为红色
//    public static int getStrokeColor() {   //画笔颜色
//        return m_strokeColor;
//    }
//
//    //    //清除所有状态
//    //    public void clearAllStrokes() {
//    //        if (m_canClear) {
//    //
//    //            m_undoStack.clearAll();
//    //            if (null != m_tempForeBitmap) {
//    //                m_tempForeBitmap.recycle();
//    //                m_tempForeBitmap = null;
//    //            }
//    //            // Create a new fore bitmap and set to canvas.
//    //            createStrokeBitmap(m_canvasWidth, m_canvasHeight);
//    //
//    //            invalidate();
//    //
//    //            m_canClear = false;
//    //        }
//    //    }
//
//    /*获取切图（自己操作在屏幕上操作的）*/
//    public Bitmap getCanvasSnapshot() {
//        if (minX == 0 || minY == 0 || maxX == 0 || maxY == 0) {//或||有一个成立则成立 全为false 则为false
//            return null;
//        }
//        setDrawingCacheEnabled(false);//销毁原来的cache(可以清理开启时的申请的内存)
//        setDrawingCacheEnabled(true);//开启cache
//        buildDrawingCache(true);//生成cache
//        Bitmap bmp = getDrawingCache(true);//获得可画视图的缓存
//
//        if (bmp == null) {
//        }
//
//        isFirstDown = true;
//        return BitmapUtil.duplicateBitmap(bmp, minX, minY, maxX, maxY);//小手截屏代码
//    }
//
//    /**
//     * 截图
//     */
// /*   public Bitmap getCanvasSnapshot4Video(final Context context) {
//
//
//        if (AppFlag.getAppflag().recording) {//如果为True就开始截图 //如果为false就停止截图
//            ////////////
//            AppFlag.getAppflag().canClean = false;
//            AppFlag.getAppflag().canRecord = true;
//            //截图 开启线程截图
//            captureBitmapThread = new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//
//                    if (MemoryUtils.getAppSurplusMe() > 10240) {
//
//                        while (AppFlag.getAppflag().recording) {//判断标记recording=true截图
//                            // Log.d("ding", "recording----------------->截图");
//                            //  Log.d("XXX", "当前线程：---------------" + Thread.currentThread().getName());
//                            while (AppFlag.getAppflag().canRecord == false) {
//
//                            }
//
//                            AppFlag.getAppflag().canClean = false;
//                            try {
//                                Thread.sleep(5);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//
//                            if (Config.screenHeight != 0 && Config.screenWidth != 0) {
//                                //截屏幕核心模块
//                                Bitmap bitmap = Bitmap.createBitmap(Config.screenHeight, Config.screenWidth, Bitmap.Config.RGB_565);
//                                Canvas canvas = new Canvas(bitmap);
//                                //Screenlayer是截图的图层
//                                if (Screenlayer != null && bitmap != null && canvas != null) {
//                                    Screenlayer.draw(canvas);
//                                    //获取截图的bitmap对象
//                                    AppFlag.getAppflag().setBitmap(bitmap);
//                                    AppFlag.getAppflag().canClean = true;
//
//                                }
//                            }
//
//                        }
//
//                    } else {
//                        Toast.makeText(context, "系统内存不足无法录制！", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            }
//
//            );
//
//            captureBitmapThread.start();
//
//        } else {
//
//            captureBitmapThread = null;
//
//        }
//
//
//        return null;
//    }*/
//    public void setBkBitmap(Bitmap bmp) {
//        if (m_bkBitmap != bmp) {
//
//            m_bkBitmap = BitmapUtil.duplicateBitmap(bmp, minX, minY, maxX, maxY);
//            invalidate();
//        }
//    }
//
//    //    public Bitmap getBkBitmap() {
//    //        return m_bkBitmap;
//    //    }
//
//    protected void createStrokeBitmap(int w, int h) {
//        m_canvasWidth = w;
//        m_canvasHeight = h;
//        Bitmap bitmap = Bitmap.createBitmap(m_canvasWidth, m_canvasHeight, Bitmap.Config.ARGB_8888);
//        if (null != bitmap) {
//            m_foreBitmap = bitmap;
//            // Set the fore bitmap to m_canvas to be as canvas of strokes.
//            m_canvas.setBitmap(m_foreBitmap);
//        }
//    }
//
//    protected void setTempForeBitmap(Bitmap tempForeBitmap) {
//        if (null != tempForeBitmap) {
//            if (null != m_foreBitmap) {
//                m_foreBitmap.recycle();
//            }
//            m_foreBitmap = BitmapCtl.duplicateBitmap(tempForeBitmap);
//            if (null != m_foreBitmap && null != m_canvas) {
//                m_canvas.setBitmap(m_foreBitmap);
//                invalidate();
//            }
//        }
//    }
//
//    protected void setCanvasSize(int width, int height) {
//        if (width > 0 && height > 0) {
//            if (m_canvasWidth != width || m_canvasHeight != height) {
//                m_canvasWidth = width;
//                m_canvasHeight = height;
//                createStrokeBitmap(m_canvasWidth, m_canvasHeight);
//            }
//        }
//    }
//
//    //初始化数据（画布和画笔）
//    public void initialize() {
//        m_canvas = new Canvas();//初始化画布
//        m_bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//初始化画笔
//        m_undoStack = new SketchPadUndoStack(this, UNDO_SIZE);//第二个参数是回退次数
//
//    }
//
//
//    //画图形
//    public void setStrokeType(int type) {
//        m_strokeColor = SketchpadView.getStrokeColor();//获取画笔颜色
//        m_penSize = SketchpadView.getStrokeSize();//画笔尺寸
//        switch (type) {
//            case STROKE_PEN://铅笔 12
//                m_curTool = new PenuCtl(m_penSize, m_strokeColor);//实现了ISketchpadDraw的接口
//                break;
//
//            case STROKE_ERASER://橡皮 2
//                m_curTool = new EraserCtl(m_eraserSize);
//                break;
//            case STROKE_PLYGON:  //多边形  10
//
//                //绘制任意多边形
//                m_curTool = new PlygonCtl(m_penSize, m_strokeColor);
//
//                break;
//            case STROKE_RECT:   //绘制矩形(正常)  9
//                m_curTool = new RectuCtl(m_penSize, m_strokeColor, 1);
//                break;
//
//            case 13:            //绘制矩形（圆角矩形）13
//                m_curTool = new RectuCtl(m_penSize, m_strokeColor, 2);
//                break;
//
//            case STROKE_CIRCLE:  //绘制圆形  8
//                m_curTool = new Circlectl(m_penSize, m_strokeColor);
//                break;
//            case STROKE_OVAL:    //绘制椭圆   7
//                m_curTool = new OvaluCtl(m_penSize, m_strokeColor);
//                break;
//            case STROKE_LINE:    //划线 6
//                m_curTool = new LineCtl(m_penSize, m_strokeColor);
//                break;
//            case STROKE_SPRAYGUN:  //绘制任意多边形 5
//                m_curTool = new Spraygun(m_penSize, m_strokeColor);
//                break;
//        }
//
//        m_strokeType = type;//绘制完之后还是画笔状态
//    }
//
//    /**
//     * 一共3个构造函数
//     * 分别初始化
//     */
//    //1个参数的构造
//    public SketchpadView(Context context) {
//        super(context);
//        // TODO Auto-generated constructor stub
//        initialize();
//        setWillNotDraw(false);
//        this.context = context;
//    }
//
//    //2个参数的构造
//    public SketchpadView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//        initialize();
//        setWillNotDraw(false);
//        this.context = context;
//    }
//
//    //3个参数的构造
//    public SketchpadView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        // TODO Auto-generated constructor stub
//        initialize();
//        setWillNotDraw(false);
//        this.context = context;
//    }
//
//    public boolean canRedo() {
//        // TODO Auto-generated method stub
//        if (null != m_undoStack) {
//            return m_undoStack.canUndo();
//        }
//        return false;
//    }
//
//    public boolean canUndo() {
//        // TODO Auto-generated method stub
//        if (null != m_undoStack) {
//            return m_undoStack.canRedo();
//        }
//        return false;
//    }
//
//    public void onDeleteFromRedoStack() {
//        // TODO Auto-generated method stub
//    }
//
//    public void onDeleteFromUndoStack() {
//        // TODO Auto-generated method stub
//    }
//
//    //页面数据右回撤
//    public void redo() {
//        // TODO Auto-generated method stub
//        if (null != m_undoStack) {
//            m_undoStack.redo();
//        }
//    }
//
//    //页面数据左回撤
//    public void undo() {
//        // TODO Auto-generated method stub
//        if (null != m_undoStack) {
//            m_undoStack.undo();
//            Log.i("sada022", "undo00");
//        }
//    }
//
//    ///////////////////////////////////////////
//    protected void onDraw(Canvas canvas) {
//        // TODO Auto-generated method stub
//        super.onDraw(canvas);
//        //        Log.d("ding","recording---------------------->"+recording +"  "+mIpnutSurface);
//        //canvas.drawBitmap(m_bkBitmap, 0, 0,null);
//        //	canvas.drawColor(m_bkColor);
//        // Draw background bitmap.
//        if (null != m_bkBitmap) {
//            RectF dst = new RectF(getLeft(), getTop(), getRight(), getBottom());
//            Rect rst = new Rect(0, 0, m_bkBitmap.getWidth(), m_bkBitmap.getHeight());
//            canvas.drawBitmap(m_bkBitmap, rst, dst, m_bitmapPaint);
//        }
//        if (null != m_foreBitmap) {
//            canvas.drawBitmap(m_foreBitmap, 0, 0, m_bitmapPaint);
//        }
//        if (null != m_curTool) {
//            if (STROKE_ERASER != m_strokeType) {
//                if (! m_isTouchUp) {
//                    m_curTool.draw(canvas);
//                }
//            }
//        }
//        //        if(recording && mIpnutSurface != null){
//        //            final Canvas surfaceCanvas = mIpnutSurface.lockCanvas( null ); // Android canvas from surface
//        ////            super.onDraw( surfaceCanvas ); // Call the WebView onDraw targetting the canvas
//        //            super.dispatchDraw(surfaceCanvas);
//        //            mIpnutSurface.unlockCanvasAndPost( surfaceCanvas ); // We're done with the canvas!
//        //        }
//    }
//
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        // TODO Auto-generated method stub
//        super.onSizeChanged(w, h, oldw, oldh);
//        if (! m_isSetForeBmp) {
//            setCanvasSize(w, h);
//        }
//        Log.i("sada022", "Canvas");
//        m_canvasWidth = w;
//        m_canvasHeight = h;
//        m_isSetForeBmp = false;
//    }
//
//    //更新当前的最大最小X Y(传入发的是当前坐标X Y)
//    private void updateMaxValue(float x, float y) {
//
//        if (this.maxX < x) {//原先 < >
//            this.maxX = x;
//        }
//        if (this.maxY < y) {
//            this.maxY = y;
//        }
//
//        if (this.minX > x) {
//            this.minX = x;
//        }
//        if (this.minY > y) {
//            this.minY = y;
//
//        }
//
//
//    }
//
//
//    //画任意图形时
//    public boolean onTouchEvent(MotionEvent event) {
//
//        float yx = event.getX();//获取点的XY坐标
//        float yy = event.getY();
//
//        if (m_isEnableDraw)   //判断前是true
//        {
//            m_isTouchUp = false;//m_isTouchUp也是false
//
//            switch (event.getAction()) {  //按下0 移动2 抬起 1
//
//                case MotionEvent.ACTION_DOWN://按下
//                    ClearMempry.ClearMempry(this.context);//清理内存
//
//
//                    setStrokeType(m_strokeType);//绘制图形如果是画笔状态
//                    m_curTool.touchDown(event.getX(), event.getY());//按下时传进X Y坐标（接口回调的方式）
//                    if (isFirstDown) {//如果是第一次按下
//                        minX = event.getX();
//                        minY = event.getY();
//                    }
//                    isFirstDown = false;
//
//                    this.updateMaxValue(event.getX(), event.getY());
//
//                    if (flag == 1) { //(参数是X Y )   m_foreBitmap.getPixel((int) yx, (int) yy)=获取像素的ＲＧＢ  m_strokeColor=画笔颜色
//                        seed_fill((int) yx, (int) yy, m_foreBitmap.getPixel((int) yx, (int) yy), m_strokeColor);
//                        invalidate();//重新绘制
//                        flag = 0;
//                    }
//                    lastDrawPly();//上一次画的
//                    //如果绘制任意多边形
//                    if (STROKE_SPRAYGUN == m_strokeType) {
//                        myLoop = true;
//                        spraygunRun();
//                    }
//                    invalidate();//重新绘制
//
//                    break;
//
//                case MotionEvent.ACTION_MOVE:
//
//                    m_curTool.touchMove(event.getX(), event.getY());//移动时传入的坐标
//
//                    if (m_curTool instanceof PenuCtl) {
//                        this.updateMaxValue(event.getX(), event.getY());
//                    }
//
//
//                    //如果是橡皮
//                    if (STROKE_ERASER == m_strokeType) {
//                        m_curTool.draw(m_canvas);
//                    }
//                    //如果是画笔（除了橡皮之外）
//                    if (STROKE_SPRAYGUN == m_strokeType) {
//                        m_curTool.draw(m_canvas);
//                    }
//                    invalidate();//重新绘制
//
//                    m_canClear = true;
//
//                    break;
//
//
//                case MotionEvent.ACTION_UP:
//                    m_isTouchUp = true;
//                    if (m_curTool.hasDraw()) {//m_curTool.hasDraw()=true
//                        // Add to undo stack.
//                        this.m_undoStack.push(m_curTool);
//                    }
//                    //抬起时的X    //抬起时的Y
//                    m_curTool.touchUp(event.getX(), event.getY());//抬起时的X Y 坐标
//
//                    if (m_curTool instanceof PenuCtl) {
//                        this.updateMaxValue(event.getX(), event.getY());
//                    }
//                    if (m_curTool instanceof RectuCtl) {
//                        this.updateMaxValue(event.getX(), event.getY());
//                    }
//                    if (m_curTool instanceof Circlectl) {
//                        this.updateMaxValue(event.getX(), event.getY());
//                    }
//                    if (m_curTool instanceof EraserCtl) {
//                        this.updateMaxValue(event.getX(), event.getY());
//                    }
//
//
//                    //获取抬起时的最大 最小 xy的值进行比较
//                    this.updateMaxValue(m_curTool.getMinX(), m_curTool.getMinY());
//                    this.updateMaxValue(m_curTool.getMaxX(), m_curTool.getMaxY());
//                    this.updateMaxValue(event.getX(), event.getY());
//
//                    // Draw strokes on bitmap which is hold by m_canvas.
//                    m_curTool.draw(m_canvas);
//                    invalidate();//重新绘制
//
//                    m_canClear = true;
//                    myLoop = false;
//                    ClearMempry.ClearMempry(this.context);
//
//                    break;
//            }
//        }
//        return true;
//    }
//
//    //传过来当前活动 SketchpadMainActivity
//    public Surface gen2Video(Context context) {
//      //  getCanvasSnapshot4Video(context);
//
//        return null;
//    }
//
//
//    public class SketchPadUndoStack {
//        private int                       m_stackSize    = 0;
//        private SketchpadView             m_sketchPad    = null;
//        private ArrayList<ISketchpadDraw> m_undoStack    = new ArrayList<ISketchpadDraw>();
//        private ArrayList<ISketchpadDraw> m_redoStack    = new ArrayList<ISketchpadDraw>();
//        private ArrayList<ISketchpadDraw> m_removedStack = new ArrayList<ISketchpadDraw>();
//
//        //内部类的构造函数（2各参数的）
//        public SketchPadUndoStack(SketchpadView sketchPad, int stackSize) {
//            m_sketchPad = sketchPad;
//            m_stackSize = stackSize;
//        }
//
//        //接口回调
//        public void push(ISketchpadDraw sketchPadTool) {
//            if (null != sketchPadTool) {
//                if (m_undoStack.size() == m_stackSize && m_stackSize > 0) {
//                    ISketchpadDraw removedTool = m_undoStack.get(0);
//                    m_removedStack.add(removedTool);
//                    m_undoStack.remove(0);
//                }
//                m_undoStack.add(sketchPadTool);
//            }
//        }
//
//        public void clearAll() {
//            m_redoStack.clear();
//            m_undoStack.clear();
//            m_removedStack.clear();
//        }
//
//        public void undo() {
//            if (canUndo() && null != m_sketchPad) {
//                ISketchpadDraw removedTool = m_undoStack.get(m_undoStack.size() - 1);
//                m_redoStack.add(removedTool);
//                m_undoStack.remove(m_undoStack.size() - 1);
//
//                if (null != m_tempForeBitmap) {
//                    // Set the temporary fore bitmap to canvas.
//                    m_sketchPad.setTempForeBitmap(m_sketchPad.m_tempForeBitmap);
//                } else {
//                    // Create a new bitmap and set to canvas.
//                    m_sketchPad.createStrokeBitmap(m_sketchPad.m_canvasWidth, m_sketchPad.m_canvasHeight);
//                }
//                Canvas canvas = m_sketchPad.m_canvas;
//                // First draw the removed tools from undo stack.
//                for (ISketchpadDraw sketchPadTool : m_removedStack) {
//                    sketchPadTool.draw(canvas);
//                }
//                for (ISketchpadDraw sketchPadTool : m_undoStack) {
//                    sketchPadTool.draw(canvas);
//                }
//                m_sketchPad.invalidate();
//            }
//        }
//
//        public void redo() {
//            if (canRedo() && null != m_sketchPad) {
//                ISketchpadDraw removedTool = m_redoStack.get(m_redoStack.size() - 1);
//                m_undoStack.add(removedTool);
//                m_redoStack.remove(m_redoStack.size() - 1);
//
//                if (null != m_tempForeBitmap) {
//                    // Set the temporary fore bitmap to canvas.
//                    m_sketchPad.setTempForeBitmap(m_sketchPad.m_tempForeBitmap);
//                } else {
//                    // Create a new bitmap and set to canvas.
//                    m_sketchPad.createStrokeBitmap(m_sketchPad.m_canvasWidth, m_sketchPad.m_canvasHeight);
//                }
//                Canvas canvas = m_sketchPad.m_canvas;
//
//                // First draw the removed tools from undo stack.
//                for (ISketchpadDraw sketchPadTool : m_removedStack) {
//                    sketchPadTool.draw(canvas);
//                }
//                for (ISketchpadDraw sketchPadTool : m_undoStack) {
//                    sketchPadTool.draw(canvas);
//                }
//                m_sketchPad.invalidate();
//            }
//        }
//
//        public boolean canUndo() {//
//            return (m_undoStack.size() > 0);
//        }
//
//        public boolean canRedo() {//�ж�ջ�Ĵ�С
//            return (m_redoStack.size() > 0);
//        }
//    }
//
//    public void lastDrawPly() {
//        if (((PlygonCtl.getStartPoint().getX() != PlygonCtl.getmPoint().getX())
//                || (PlygonCtl.getStartPoint().getY() != PlygonCtl.getmPoint().getY()))
//                //               && ((STROKE_PLYGON != m_strokeType)  || (SketchpadMainActivity.isPlygon_Click()
//                //                && PlygonCtl.getCountLine() == 0))
//                //						||SketchpadMainActivity.isSave_Click())
//                && STROKE_ERASER != m_strokeType) {
//            PlygonCtl lastLine = new PlygonCtl(m_penSize, m_strokeColor);
//            lastLine.lineDraw(m_canvas);
//            //            SketchpadMainActivity.setPlygon_Click(false);
//            //			SketchpadMainActivity.setSave_Click(false);
//            PlygonCtl.setmPoint(PlygonCtl.getStartPoint().getX(), PlygonCtl.getStartPoint().getY());
//        }
//    }
//
//    public void spraygunRun() {
//        new Thread(new Runnable() {
//            public void run() {
//                while (myLoop) {
//                    m_curTool.draw(m_canvas);
//                    try {
//                        Thread.sleep(50);
//                        if (antiontemp == MotionEvent.ACTION_UP) {
//                            myLoop = false;
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    postInvalidate();//更新View
//                }
//            }
//        }).start();
//    }
//
//    public void seed_fill(int x, int y, int t_color, int r_color) {
//
//        int MAX_ROW = 400;
//        int MAX_COL = 650;
//        int row_size = 400;
//        int col_size = 650;
//        if (x < 0 || x >= col_size || y < 0 || y >= row_size || m_foreBitmap.getPixel(x, y) == r_color) {
//            return;
//        }
//        int queue[][] = new int[MAX_ROW * MAX_COL + 1][2];
//        int head = 0, end = 0;
//        int tx, ty;
//        /* Add node to the end of queue. */
//        queue[end][0] = x;
//        queue[end][1] = y;
//        end++;
//        while (head < end) {
//            tx = queue[head][0];
//            ty = queue[head][1];
//            if (m_foreBitmap.getPixel(tx, ty) == t_color) {
//                m_foreBitmap.setPixel(tx, ty, r_color);
//            }
//            /* Remove the first element from queue. */
//            head++;
//
//            /* West */
//            if (tx - 1 >= 0 && m_foreBitmap.getPixel(tx - 1, ty) == t_color) {
//                m_foreBitmap.setPixel(tx - 1, ty, r_color);
//                queue[end][0] = tx - 1;
//                queue[end][1] = ty;
//                end++;
//            } else if (tx - 1 >= 0 && m_foreBitmap.getPixel(tx - 1, ty) != t_color) {
//                m_foreBitmap.setPixel(tx - 1, ty, r_color);
//
//
//            }
//
//
//            /* East */
//            if (tx + 1 < col_size && m_foreBitmap.getPixel(tx + 1, ty) == t_color) {
//                m_foreBitmap.setPixel(tx + 1, ty, r_color);
//                queue[end][0] = tx + 1;
//                queue[end][1] = ty;
//                end++;
//            } else if (tx + 1 < col_size && m_foreBitmap.getPixel(tx + 1, ty) != t_color) {
//                m_foreBitmap.setPixel(tx + 1, ty, r_color);
//
//
//            }
//            /* North */
//            if (ty - 1 >= 0 && m_foreBitmap.getPixel(tx, ty - 1) == t_color) {
//                m_foreBitmap.setPixel(tx, ty - 1, r_color);
//                queue[end][0] = tx;
//                queue[end][1] = ty - 1;
//                end++;
//            } else if (ty - 1 >= 0 && m_foreBitmap.getPixel(tx, ty - 1) != t_color) {
//                m_foreBitmap.setPixel(tx, ty - 1, r_color);
//
//
//            }
//            /* South */
//            if (ty + 1 < row_size && m_foreBitmap.getPixel(tx, ty + 1) == t_color) {
//                m_foreBitmap.setPixel(tx, ty + 1, r_color);
//                queue[end][0] = tx;
//                queue[end][1] = ty + 1;
//                end++;
//            } else if (ty + 1 < row_size && m_foreBitmap.getPixel(tx, ty + 1) != t_color) {
//                m_foreBitmap.setPixel(tx, ty + 1, r_color);
//
//
//            }
//        }
//        return;
//    }
//
//
//}
