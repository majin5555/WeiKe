//package com.weike.customview;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//
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
//
//import java.util.ArrayList;
//
///**
// * 截取图片 合成视频
// */
//public class SurfaceViewTemplate extends SurfaceView implements Runnable {
//    private static final String TAG = "SurfaceViewTemplate";
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
//
//    private          Thread  mThread;
//    private volatile boolean isRunning;
//
//    private Paint              m_bitmapPaint;
//    private SketchPadUndoStack m_undoStack;
//
//    public void setRunning(boolean running) {
//        isRunning = running;
//    }
//
//    public boolean isRunning() {
//        return isRunning;
//    }
//
//    private boolean m_isSetForeBmp = false;
//    private int     m_canvasWidth  = 100;
//    private int     m_canvasHeight = 100;
//
//    private             Bitmap m_foreBitmap     = null;
//    private             Bitmap m_tempForeBitmap = null;
//    private             Bitmap m_bkBitmap       = null;
//    private             int    m_strokeType     = STROKE_PEN;   //  画笔状态
//    private static      int    m_strokeColor    = Color.RED;   //画笔颜色红色
//    private static      int    m_penSize        = CommonDef.SMALL_PEN_WIDTH;// 画笔尺寸（默认为6）
//    private static      int    m_eraserSize     = CommonDef.LARGE_ERASER_WIDTH;   //橡皮默认宽度
//    public static final int    UNDO_SIZE        = 90;       //回退次数
//    public static final int    BITMAP_WIDTH     = 1280;    //备份图片的宽
//    public static final int    BITMAP_HEIGHT    = 600;    //备份图片的高
//
//    private ISketchpadDraw m_curTool   = null;   //接口（绘制各种图形时都继承了接口）
//    private Boolean        isFirstDown = true;//第一次按下标记
//
//    private Paint  mPaint;
//    private Canvas canvas;
//
//
//    /**
//     * 开始录制
//     */
//    public void startRecording() {
//        setRunning(true);
//        Log.d(TAG, " 开始  " + isRunning());
//    }
//
//    /**
//     * 结束录制
//     */
//    public void stopRecording() {
//        setRunning(false);
//        Log.d(TAG, "  停止  " + isRunning());
//    }
//
//    public SurfaceViewTemplate(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//
//        SurfaceHolder holder = getHolder();
//        holder.addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                // 监听到Surface创建完毕
//                isRunning = true;
//                mThread = new Thread(SurfaceViewTemplate.this);
//                mThread.start();
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//                isRunning = false;
//            }
//        });
//
//        setFocusable(true);
//        setFocusableInTouchMode(true);
//        setKeepScreenOn(true);
//
//        initPaint();
//    }
//
//    /**
//     * 初始化数据（画布和画笔）
//     */
//    private void initPaint() {
//
//        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setDither(true);
//        mPaint.setAntiAlias(true);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(6);
//        mPaint.setColor(Color.GREEN);
//
//        getCanvas();
//        //  m_undoStack = new SketchPadUndoStack(this, UNDO_SIZE);
//    }
//
//    /**
//     * 获取画板
//     */
//    private void getCanvas() {
//
//        try {
//            canvas = getHolder().lockCanvas();
//
//            if (canvas != null) {
//                //TODO  进行绘制操作
//                drawCircle(canvas);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//
//        } finally {
//            if (canvas != null) {
//                getHolder().unlockCanvasAndPost(canvas);
//            }
//        }
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        // TODO Auto-generated method stub
//        super.onSizeChanged(w, h, oldw, oldh);
//        if (! m_isSetForeBmp) {
//            setCanvasSize(w, h);
//        }
//        Log.d(TAG, "Canvas");
//        m_canvasWidth = w;
//        m_canvasHeight = h;
//        m_isSetForeBmp = false;
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
//    protected void createStrokeBitmap(int w, int h) {
//        m_canvasWidth = w;
//        m_canvasHeight = h;
//        Bitmap bitmap = Bitmap.createBitmap(m_canvasWidth, m_canvasHeight, Bitmap.Config.ARGB_8888);
//        if (null != bitmap) {
//            m_foreBitmap = bitmap;
//            // Set the fore bitmap to canvas to be as canvas of strokes.
//            canvas.setBitmap(m_foreBitmap);
//        }
//    }
//
//
//    @Override
//    public void run() {
//
//
//        while (true) {
//
//            if (isRunning()) {
//                //TODO 进行屏幕截图  保存起来
//
//                //  drawSelf();
//                //  Log.d(TAG, "  绘制 ");
//
//            } else {
//                //TODO 什么也不做
//                //Log.d(TAG, "  停止绘制 ");
//            }
//        }
//    }
//
//    private boolean m_isTouchUp = false;    //是否抬起
//    private float   minX, minY, maxX, maxY;//浮点行的最小X ，Y坐标
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//
//        float yx = event.getX();//获取点的XY坐标
//        float yy = event.getY();
//
//
//        m_isTouchUp = false;//m_isTouchUp也是false
//
//        switch (event.getAction()) {  //按下0 移动2 抬起 1
//
//            case MotionEvent.ACTION_DOWN://按下
//                //  ClearMempry.ClearMempry(this.context);//清理内存
//
//                setStrokeType(m_strokeType);//绘制图形如果是画笔状态
//
//                m_curTool.touchDown(event.getX(), event.getY());//按下时传进X Y坐标（接口回调的方式）
//                if (isFirstDown) {//如果是第一次按下
//                    minX = event.getX();
//                    minY = event.getY();
//                }
//                isFirstDown = false;
//
//                this.updateMaxValue(event.getX(), event.getY());
//
//                if (flag == 1) { //(参数是X Y )   m_foreBitmap.getPixel((int) yx, (int) yy)=获取像素的ＲＧＢ  m_strokeColor=画笔颜色
//                    seed_fill((int) yx, (int) yy, m_foreBitmap.getPixel((int) yx, (int) yy), m_strokeColor);
//                    invalidate();//重新绘制
//                    flag = 0;
//                }
//                lastDrawPly();//上一次画的
//                //如果绘制任意多边形
//                if (STROKE_SPRAYGUN == m_strokeType) {
//                    myLoop = true;
//                    spraygunRun();
//                }
//                invalidate();//重新绘制
//
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//
//                m_curTool.touchMove(event.getX(), event.getY());//移动时传入的坐标
//
//                if (m_curTool instanceof PenuCtl) {
//                    this.updateMaxValue(event.getX(), event.getY());
//                }
//
//
//                //如果是橡皮
//                if (STROKE_ERASER == m_strokeType) {
//                    m_curTool.draw(m_canvas);
//                }
//                //如果是画笔（除了橡皮之外）
//                if (STROKE_SPRAYGUN == m_strokeType) {
//                    m_curTool.draw(m_canvas);
//                }
//                invalidate();//重新绘制
//
//                m_canClear = true;
//
//                break;
//
//
//            case MotionEvent.ACTION_UP:
//                m_isTouchUp = true;
//                if (m_curTool.hasDraw()) {//m_curTool.hasDraw()=true
//                    // Add to undo stack.
//                    this.m_undoStack.push(m_curTool);
//                }
//                //抬起时的X    //抬起时的Y
//                m_curTool.touchUp(event.getX(), event.getY());//抬起时的X Y 坐标
//
//                if (m_curTool instanceof PenuCtl) {
//                    this.updateMaxValue(event.getX(), event.getY());
//                }
//                if (m_curTool instanceof RectuCtl) {
//                    this.updateMaxValue(event.getX(), event.getY());
//                }
//                if (m_curTool instanceof Circlectl) {
//                    this.updateMaxValue(event.getX(), event.getY());
//                }
//                if (m_curTool instanceof EraserCtl) {
//                    this.updateMaxValue(event.getX(), event.getY());
//                }
//
//
//                //获取抬起时的最大 最小 xy的值进行比较
//                this.updateMaxValue(m_curTool.getMinX(), m_curTool.getMinY());
//                this.updateMaxValue(m_curTool.getMaxX(), m_curTool.getMaxY());
//                this.updateMaxValue(event.getX(), event.getY());
//
//                // Draw strokes on bitmap which is hold by m_canvas.
//                m_curTool.draw(m_canvas);
//                invalidate();//重新绘制
//
//                m_canClear = true;
//                myLoop = false;
//                ClearMempry.ClearMempry(this.context);
//
//                break;
//        }
//
//        return true;
//    }
//
//    /**
//     * 更新当前的最大最小X Y(传入发的是当前坐标X Y)
//     */
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
//        }
//    }
//    public void lastDrawPly() {
//        if (((PlygonCtl.getStartPoint().getX() != PlygonCtl.getmPoint().getX())
//                || (PlygonCtl.getStartPoint().getY() != PlygonCtl.getmPoint().getY()))
//                //               && ((STROKE_PLYGON != m_strokeType)  || (SketchpadMainActivity.isPlygon_Click()
//                //                && PlygonCtl.getCountLine() == 0))
//                //						||SketchpadMainActivity.isSave_Click())
//                && STROKE_ERASER != m_strokeType) {
//            PlygonCtl lastLine = new PlygonCtl(m_penSize, m_strokeColor);
//            lastLine.lineDraw(mc);
//            //            SketchpadMainActivity.setPlygon_Click(false);
//            //			SketchpadMainActivity.setSave_Click(false);
//            PlygonCtl.setmPoint(PlygonCtl.getStartPoint().getX(), PlygonCtl.getStartPoint().getY());
//        }
//    }
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
//    public class SketchPadUndoStack {
//        private int                       m_stackSize    = 0;
//        //private SketchpadView             m_sketchPad    = null;
//        private ArrayList<ISketchpadDraw> m_undoStack    = new ArrayList<ISketchpadDraw>();
//        private ArrayList<ISketchpadDraw> m_redoStack    = new ArrayList<ISketchpadDraw>();
//        private ArrayList<ISketchpadDraw> m_removedStack = new ArrayList<ISketchpadDraw>();
//
//        //内部类的构造函数（2各参数的）
//        public SketchPadUndoStack(SketchpadView sketchPad, int stackSize) {
//            // m_sketchPad = sketchPad;
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
//        /*public void undo() {
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
//                Canvas canvas = m_sketchPad.canvas;
//                // First draw the removed tools from undo stack.
//                for (ISketchpadDraw sketchPadTool : m_removedStack) {
//                    sketchPadTool.draw(canvas);
//                }
//                for (ISketchpadDraw sketchPadTool : m_undoStack) {
//                    sketchPadTool.draw(canvas);
//                }
//                m_sketchPad.invalidate();
//            }
//        }*/
//
//      /*  public void redo() {
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
//                Canvas canvas = m_sketchPad.canvas;
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
//        }*/
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
//
//    private void drawCircle(Canvas canvas) {
//        canvas.drawColor(Color.WHITE);
//        //  canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mPaint);
//
//        //        if (mRadius >= mMaxRadius) {
//        //            mFlag = - 1;
//        //        } else if (mRadius <= mMinRadius) {
//        //            mFlag = 1;
//        //        }
//        //
//        //        mRadius += mFlag * 2;
//        //    }
//    }
//}
