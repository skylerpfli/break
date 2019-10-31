package com.example.ly_lipengfei.abreak;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * @author qzns木雨
 * @date 2019/1/22
 * @email yingfeng.li@qq.com
 * @description 程序时钟控件
 */
public class BreakTime extends View {

    //绘制相关
    private Paint mCirclePaint;
    private Paint mTimePaint;
    private Paint mTipsPaint;
    private Paint mTextPaint;
    private Paint mMinPaint;
    private Paint mSecPaint;
    private int mViewWidth;
    private int mViewHeight;

    //时钟半径、中心点
    int radius;
    int centerX;
    int centerY;

    //时间相关
    long startTime;
    long duration;
    String time;
    String tips;

    //是否正在计时
    boolean isRunning;

    //时间字符串
    private final String BREAK_TIPS = "Break";
    private final String CONTINUE_TIPS = "Continue";
    private final String NO_INIT_TIME_STR = "00:00";

    //onDraw更新相关
    private final int TIME_MESSAGE = 0;
    private final int REFRESH_DELAY_TIME = 50;

    //刻度字符串
    private final String[] TIME_DEGREE = {"onCreate", "onStart", "onResume", "onPause", "onStop", "onDestroy"};

    //绘制表盘的路径和矩阵
    Path onCreateDestroyPath;
    Path onStartPath;
    Path onSumePath;
    Path onPausePath;
    Path onStopPath;
    Rect timeBound;
    Rect tipsBound;

    private BreakTimeListener mBreakTimeListener;

    private static final String TAG = "BreakTime";

    public BreakTime(Context context) {
        super(context);
        initConfig();
        initPaint();
        initPath();
    }

    public BreakTime(Context context, AttributeSet attrs) {
        super(context, attrs);
        initConfig();
        initPaint();
        initPath();
    }

    public BreakTime(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfig();
        initPaint();
        initPath();
    }

    public BreakTime(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initConfig();
        initPaint();
        initPath();
    }

    //初始化数据
    private void initConfig() {
        tips = CONTINUE_TIPS;
        isRunning = false;
        startTime = 0;
        duration = 0;
        time = NO_INIT_TIME_STR;
    }

    //初始化画笔
    private void initPaint() {
        //绘制圆形的画笔
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置类型为描边
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setColor(Color.GRAY);
        //设置画笔粗细
        mCirclePaint.setStrokeWidth(5f);

        //绘制时间字符串的画笔
        mTimePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimePaint.setColor(Color.GRAY);
        mTimePaint.setTextSize(150f);

        //绘制tips的画笔
        mTipsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTipsPaint.setColor(Color.GRAY);
        mTipsPaint.setTextSize(60f);

        //绘制刻度的画笔
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setTextSize(40f);

        //绘制分的画笔
        mMinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMinPaint.setARGB(180, 72, 209, 204);

        //绘制秒的画笔
        mSecPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSecPaint.setColor(Color.RED);
    }

    //初始化绘制路径
    private void initPath() {
        RectF RectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        //onCreate/Destroy的路径
        onCreateDestroyPath = new Path();
        onCreateDestroyPath.addArc(RectF, 241, 80);

        //onStartPath的路径
        onStartPath = new Path();
        onStartPath.addArc(RectF, 42, -25);

        //onResume的路径
        onSumePath = new Path();
        onSumePath.addArc(RectF, 104, -25);

        //onPause的路径
        onPausePath = new Path();
        onPausePath.addArc(RectF, 161, -25);

        //onStop的路径
        onStopPath = new Path();
        onStopPath.addArc(RectF, 200, 25);

        //时间的绘制矩阵
        timeBound = new Rect();
        mTimePaint.getTextBounds(time, 0, time.length(), timeBound);

        //break|continue的绘制矩阵
        tipsBound = new Rect();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;

        //记录时钟的半径、中心点
        radius = mViewWidth * 2 / 5;
        centerX = mViewWidth / 2;
        centerY = mViewHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制表框
        canvas.drawCircle(centerX, centerY, radius, mCirclePaint);

        //---开始绘制刻度
        //绘制onDestroy/onCreate
        canvas.drawTextOnPath(TIME_DEGREE[5] + " / " + TIME_DEGREE[0], onCreateDestroyPath, 0, 38, mTextPaint);

        //绘制onStart
        canvas.drawTextOnPath(TIME_DEGREE[1], onStartPath, 0, -18, mTextPaint);

        //绘制onResume
        canvas.drawTextOnPath(TIME_DEGREE[2], onSumePath, 0, -18, mTextPaint);

        //绘制onPause
        canvas.drawTextOnPath(TIME_DEGREE[3], onPausePath, 0, -18, mTextPaint);

        //绘制onPause
        canvas.drawTextOnPath(TIME_DEGREE[4], onStopPath, 0, 38, mTextPaint);

        //绘制时间
        canvas.drawText(time, (float) centerX - timeBound.width() / 2, (float) centerY, mTimePaint);

        //绘制tips文字:continue | break
        mTipsPaint.getTextBounds(tips, 0, tips.length(), tipsBound);
        canvas.drawText(tips, (float) centerX - tipsBound.width() / 2, (float) mViewHeight * 3 / 5 + tipsBound.height() / 2, mTipsPaint);

        //绘制分
        //计算分的角度
        double minAngle = ((double) duration / 3600000) * 360;
        setMinPaintColor(minAngle);
        //角度转弧度公式为：1弧度 = 180/π = 57.3度
        canvas.drawCircle((float) (sin(minAngle / 57.3) * radius) + centerX, -(float) (cos(minAngle / 57.3) * radius) + centerY, 15, mMinPaint);

        //绘制秒
        double secAngle = ((double) duration / 60000) * 360 / 57.3;
        canvas.drawCircle((float) (sin(secAngle) * radius) + centerX, -(float) (cos(secAngle) * radius) + centerY, 13, mSecPaint);
    }

    //设置分的颜色渐变
    //颜色的角度梯度
    private final int[] ANGLE_LEVEL = {120, 240, 300, 360};
    //默认渐变颜色数值 level_1:蓝；level_2:绿；level_3:橙；level_4:红
    private final int[] ARGB_LEVEL1 = {230, 72, 209, 204};
    private final int[] ARGB_LEVEL2 = {230, 0, 255, 127};
    private final int[] ARGB_LEVEL3 = {230, 255, 165, 0};
    private final int[] ARGB_LEVEL4 = {230, 255, 0, 0};

    //设置分钟画笔的颜色，在不同位置有不同颜色，渐变
    private void setMinPaintColor(double minAngle) {
        if (minAngle < ANGLE_LEVEL[0]) {
            //第一层级不渐变
            mMinPaint.setARGB(ARGB_LEVEL1[0], ARGB_LEVEL1[1], ARGB_LEVEL1[2], ARGB_LEVEL1[3]);

        } else if (minAngle < ANGLE_LEVEL[1]) {
            //时针在120~240度时，从蓝到绿色渐变

            //渐变比率
            double b = (minAngle - ANGLE_LEVEL[0]) / (ANGLE_LEVEL[1] - ANGLE_LEVEL[0]);
            //根据比率设置颜色数值
            mMinPaint.setARGB(ARGB_LEVEL1[0] + (int) (b * (ARGB_LEVEL2[0] - ARGB_LEVEL1[0])),
                    ARGB_LEVEL1[1] + (int) (b * (ARGB_LEVEL2[1] - ARGB_LEVEL1[1])),
                    ARGB_LEVEL1[2] + (int) (b * (ARGB_LEVEL2[2] - ARGB_LEVEL1[2])),
                    ARGB_LEVEL1[3] + (int) (b * (ARGB_LEVEL2[3] - ARGB_LEVEL1[3])));
        } else if (minAngle < ANGLE_LEVEL[2]) {
            //时针在240~300度时，从绿到橙色渐变

            double b = (minAngle - ANGLE_LEVEL[1]) / (ANGLE_LEVEL[2] - ANGLE_LEVEL[1]);
            mMinPaint.setARGB(ARGB_LEVEL2[0] + (int) (b * (ARGB_LEVEL3[0] - ARGB_LEVEL2[0])),
                    ARGB_LEVEL2[1] + (int) (b * (ARGB_LEVEL3[1] - ARGB_LEVEL2[1])),
                    ARGB_LEVEL2[2] + (int) (b * (ARGB_LEVEL3[2] - ARGB_LEVEL2[2])),
                    ARGB_LEVEL2[3] + (int) (b * (ARGB_LEVEL3[3] - ARGB_LEVEL2[3])));
        } else if (minAngle < ANGLE_LEVEL[3]) {
            //时针在300~360度，从橙色到红色渐变

            double b = (minAngle - ANGLE_LEVEL[2]) / (ANGLE_LEVEL[3] - ANGLE_LEVEL[2]);
            mMinPaint.setARGB(ARGB_LEVEL3[0] + (int) (b * (ARGB_LEVEL4[0] - ARGB_LEVEL3[0])),
                    ARGB_LEVEL3[1] + (int) (b * (ARGB_LEVEL4[1] - ARGB_LEVEL3[1])),
                    ARGB_LEVEL3[2] + (int) (b * (ARGB_LEVEL4[2] - ARGB_LEVEL3[2])),
                    ARGB_LEVEL3[3] + (int) (b * (ARGB_LEVEL4[3] - ARGB_LEVEL3[3])));
        } else {
            //此后设置为红色
            mMinPaint.setARGB(ARGB_LEVEL4[0], ARGB_LEVEL4[1], ARGB_LEVEL4[2], ARGB_LEVEL4[3]);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRunning) {
            //恢复初始
            reset();
        } else {
            isRunning = true;
            tips = BREAK_TIPS;
            startTime = System.currentTimeMillis();
            duration = 0;
            startUpdateTime();
        }
        invalidate();

        return super.onTouchEvent(event);
    }


    @SuppressLint("HandlerLeak")
    private Handler mMusicHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            duration = System.currentTimeMillis() - startTime;
            time = duration2Time(duration);
            invalidate();
            startUpdateTime();
        }
    };

    //启动更新进度条
    private void startUpdateTime() {
        /*避免重复发送Message*/
        stopUpdateTime();
        mMusicHandler.sendEmptyMessageDelayed(TIME_MESSAGE, REFRESH_DELAY_TIME);
    }

    //暂停进度条
    private void stopUpdateTime() {
        mMusicHandler.removeMessages(TIME_MESSAGE);
    }

    //重置
    public void reset() {
        //恢复初始
        isRunning = false;
        tips = CONTINUE_TIPS;
        startTime = 0;
        stopUpdateTime();
        mBreakTimeListener.breakTime(duration);
    }

    //把时间数值转化为可显示的文字
    private String duration2Time(long duration) {
        long min = duration / 1000 / 60;
        long sec = duration / 1000 % 60;
        return (min < 10 ? "0" + min : min + "") + ":" + (sec < 10 ? "0" + sec : sec + "");
    }

    //外部设置监听
    public void setBreakTimeListener(BreakTimeListener breakTimeListener) {
        mBreakTimeListener = breakTimeListener;
    }

    public interface BreakTimeListener {
        void breakTime(long time);
    }
}
