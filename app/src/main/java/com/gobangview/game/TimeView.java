package com.gobangview.game;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

import java.util.Calendar;

/**
 * 介绍: (这里用一句话描述这个类的作用)
 * 邮箱: zhahaijun@bearead.cn
 *
 * @author: zhahaijun
 * @date: 2019/7/23.
 */
public class TimeView extends View {

    private int mColor = Color.parseColor("#ffffff"),
            mAlphaColor = Color.argb(255 - 180, 255, 255, 255),
            mBgColor = Color.parseColor("#287fab");

    private int mWidth, mHeight, mCenterX, mCenterY;

    private Paint mPaintOutCircle, mTextPaint, mPaintProgressBg, mPaintProgress,
            mPaintTriangle, mPointPaint, mPaintHour, mPaintMinute;
    private float paddingOut = dp2px(25), innerRadius = dp2px(6);

    private float mHourDegress = 0f, mMinuteDegress = 0f, mSecondMillsDegress = 0f, mSecondDegress = 0f;


    private String tag = getClass().getSimpleName().intern();

    public TimeView(Context context) {
        this(context, null);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        setVisibility(GONE);
    }

    private void init() {
        setBackgroundColor(mBgColor);
        mPaintOutCircle = new Paint();
        mPaintOutCircle.setAntiAlias(true);
        mPaintOutCircle.setColor(Color.parseColor("#ffffff"));
        mPaintOutCircle.setStrokeWidth(dp2px(1));
        mPaintOutCircle.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setColor(mColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setStrokeWidth(dp2px(1));
        mTextPaint.setTextSize(dp2px(15));
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mPaintProgressBg = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProgressBg.setColor(Color.argb(255 - 180, 255, 255, 255));
        mPaintProgressBg.setStrokeWidth(dp2px(2));
        mPaintProgressBg.setAntiAlias(true);
        mPaintProgressBg.setStyle(Paint.Style.STROKE);

        mPaintTriangle = new Paint();
        mPaintTriangle.setAntiAlias(true);
        mPaintTriangle.setColor(mColor);
        mPaintTriangle.setStyle(Paint.Style.FILL);

        mPaintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProgress.setColor(mColor);
        mPaintProgress.setAntiAlias(true);
        mPaintProgress.setStrokeWidth(dp2px(2));
        mPaintProgress.setStyle(Paint.Style.STROKE);

        mPointPaint = new Paint();
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setColor(mBgColor);
        mPointPaint.setAntiAlias(true);

        mPaintHour = new Paint();
        mPaintHour.setColor(mAlphaColor);
        mPaintHour.setStyle(Paint.Style.FILL);
        mPaintHour.setAntiAlias(true);

        mPaintMinute = new Paint();
        mPaintMinute.setAntiAlias(true);
        mPaintMinute.setColor(mColor);
        mPaintMinute.setStrokeWidth(dp2px(3));
        mPaintMinute.setStyle(Paint.Style.STROKE);
        mPaintMinute.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width < height ? width : height;
        setMeasuredDimension(size, size);
        log("size->" + size);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        canvas.translate(mCenterX, mCenterY);
        setCameraRotate();
        drawOutArcCircle(canvas);
        drawText(canvas);
        drawArcLine(canvas);
        drawSecond(canvas);
        drawHour(canvas);
        drawMinute(canvas);
        drawCenterPoint(canvas);
    }

    /**
     * 绘制时针
     *
     * @param canvas
     */
    private void drawHour(Canvas canvas) {
        canvas.save();
        canvas.rotate(mHourDegress);
        canvas.drawCircle(0f, 0f, innerRadius, mPaintTriangle);
        Path path = new Path();
        path.moveTo(-innerRadius / 2, 0f);
        path.lineTo(innerRadius / 2, 0f);
        path.lineTo(innerRadius / 6, -(mWidth / 4));
        path.lineTo(-innerRadius / 6, -(mWidth / 4));
        path.close();
        canvas.drawPath(path, mPaintHour);
        canvas.restore();
    }

    /**
     * 绘制分针
     *
     * @param canvas
     */
    private void drawMinute(Canvas canvas) {
        canvas.save();
        canvas.rotate(mMinuteDegress);
        canvas.drawLine(0, 0, 0, -(mWidth / 3), mPaintMinute);
        canvas.restore();
    }

    /**
     * 绘制中心圈
     *
     * @param canvas
     */
    private void drawCenterPoint(Canvas canvas) {
        canvas.drawCircle(0, 0, dp2px(4), mPointPaint);
    }

    /**
     * 绘制秒钟
     *
     * @param canvas
     */
    private void drawSecond(Canvas canvas) {
        //先绘制秒针的三角形
        canvas.save();
        canvas.rotate(mSecondMillsDegress);
        Path path = new Path();
        path.moveTo(0f, -mWidth * 3f / 8 + dp2px(5));
        path.lineTo(dp2px(8), -mWidth * 3f / 8 + dp2px(20));
        path.lineTo(-dp2px(8), -mWidth * 3f / 8 + dp2px(20));
        path.close();
        canvas.drawPath(path, mPaintTriangle);
        canvas.restore();
        //绘制渐变刻度
        int min = Math.min(mWidth, mHeight) / 2;
        for (int i = 0; i < 90; i += 2) {
            //第一个参数设置透明度，实现渐变效果，从255到0
            canvas.save();
            mPaintProgress.setARGB((int) (255 - 2.7 * i), 255, 255, 255);

            //这里的先减去90°，是为了旋转到开始角度，因为开始角度是y轴的负方向
            canvas.rotate(((mSecondDegress - 90 - i)));
            canvas.drawLine(min * 3 / 4, 0f, min * 3 / 4 + dp2px(10), 0f, mPaintProgress);
            canvas.restore();
        }
    }

    /**
     * 绘制刻度
     *
     * @param canvas
     */
    private void drawArcLine(Canvas canvas) {
        int startX = Math.min(mWidth, mHeight) / 2 * 3 / 4;
        for (int i = 0; i < 360; i += 2) {
            canvas.save();
            canvas.rotate(i);
            canvas.drawLine(startX, 0f, startX + dp2px(10), 0f, mPaintProgressBg);
            canvas.restore();
        }
    }

    /**
     * 绘制时钟文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        int min = Math.min(mWidth, mHeight);
        float textRadius = (min - paddingOut) / 2;
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        double mTxtHeight = Math.ceil((fm.leading - fm.ascent));
        canvas.drawText("3", textRadius, (float) (mTxtHeight / 2), mTextPaint);
        canvas.drawText("6", 0, textRadius + ((float) (mTxtHeight / 2)), mTextPaint);
        canvas.drawText("9", -textRadius, (float) (mTxtHeight / 2), mTextPaint);
        canvas.drawText("12", 0, -textRadius + (float) (mTxtHeight / 2), mTextPaint);
    }

    /**
     * 绘制外层圆弧
     *
     * @param canvas
     */
    private void drawOutArcCircle(Canvas canvas) {
        int min = Math.min(mWidth, mHeight);
        RectF rectF = new RectF(-(min - paddingOut) / 2, -(min - paddingOut) / 2,
                (min - paddingOut) / 2, (min - paddingOut) / 2);
        canvas.drawArc(rectF, 5f, 80f, false, mPaintOutCircle);
        canvas.drawArc(rectF, 95f, 80f, false, mPaintOutCircle);
        canvas.drawArc(rectF, 185f, 80f, false, mPaintOutCircle);
        canvas.drawArc(rectF, 275f, 80f, false, mPaintOutCircle);
    }

    private void calculateDegree() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        int minute = mCalendar.get(Calendar.MINUTE);
        int secondMills = mCalendar.get(Calendar.MILLISECOND);
        int second = mCalendar.get(Calendar.SECOND);
        int hour = mCalendar.get(Calendar.HOUR);
        float perMinute = (float) minute / 60;
        float perSecond = (float) second / 60;
        mHourDegress = hour * 30 + 30 * perMinute;
        mMinuteDegress = minute * 6 + 6 * perSecond;
        mSecondMillsDegress = second * 6 + secondMills * 0.006f;
        mSecondDegress = second * 6;
        float mills = secondMills * 0.006f;
        //因为是每2°旋转一个刻度，所以这里要根据毫秒值来进行计算
        if (2 <= mills && mills < 4) {
            mSecondDegress += 2;
        }
        if (4 <= mills && mills < 6) {
            mSecondDegress += 4;
        }
    }

    // 指针转动的方法
    void startTick() {
        // 一秒钟刷新一次
        postDelayed(mRunnable, 150);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            calculateDegree();
            invalidate();
            startTick();
        }
    };

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        log("width->" + mWidth);
        log("height->" + mHeight);
        log("mCenterX->" + mCenterX);
        log("mCenterY->" + mCenterY);

        mRadius = (Math.min(w - getPaddingLeft() - getPaddingRight(),
                h - getPaddingTop() - getPaddingBottom()) / 2);

        mMaxCanvasTranslate = 0.02f * mRadius;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startTick();
//        setVisibility(VISIBLE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mRunnable);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (null != mShakeAnim) {
                    if (mShakeAnim.isRunning()) {
                        mShakeAnim.cancel();
                    }
                }
                getCameraRotate(event);
                getCanvasTranslate(event);
                break;
            case MotionEvent.ACTION_MOVE:
                //根据手指坐标计算camera应该旋转的大小
                getCameraRotate(event);
                getCanvasTranslate(event);
            case MotionEvent.ACTION_UP:
                //松开手指，时钟复原并伴随晃动动画
                startShakeAnim();
        }
        return true;
    }

    /**
     * 设置3D时钟效果，触摸矩阵的相关设置、照相机的旋转大小
     * 应用在绘制图形之前，否则无效
     * desc:Camera的坐标系是左手坐标系。当手机平整的放在桌面上，X轴是手机的水平方向，Y轴是手机的竖直方向，Z轴是垂直于手机向里的那个方向。
     * Camera内部机制实际上还是opengl，不过大大简化了使用。
     */
    private void setCameraRotate() {
        mCameraMatrix.reset();
        mCamera.save();
        mCamera.rotateX(mCameraRotateX);//绕x轴旋转角度
        mCamera.rotateY(mCameraRotateY);//绕y轴旋转角度
        mCamera.getMatrix(mCameraMatrix);//相关属性设置到matrix中
        mCamera.restore();
        mCanvas.concat(mCameraMatrix);//matrix与canvas相关联
    }


    /* 时钟半径，不包括padding值 */
    private float mRadius = 0f;
    /* 手指松开时时钟晃动的动画 */
    private ValueAnimator mShakeAnim = null;
    /* 触摸时作用在Camera的矩阵 */
    private Matrix mCameraMatrix = new Matrix();
    /* 照相机，用于旋转时钟实现3D效果 */
    private Camera mCamera = new Camera();
    /* camera绕X轴旋转的角度 */
    private float mCameraRotateX = 0f;
    /* camera绕Y轴旋转的角度 */
    private float mCameraRotateY = 0f;
    /* camera旋转的最大角度 */
    private float mMaxCameraRotate = 10f;
    /* 指针的在x轴的位移 */
    private float mCanvasTranslateX = 0f;
    /* 指针的在y轴的位移 */
    private float mCanvasTranslateY = 0f;
    /* 指针的最大位移 */
    private float mMaxCanvasTranslate = 0f;
    /* 画布 */
    private Canvas mCanvas;

    /**
     * 时钟晃动动画
     */
    private void startShakeAnim() {
        final String cameraRotateXName = "cameraRotateX";
        final String cameraRotateYName = "cameraRotateY";
        final String canvasTranslateXName = "canvasTranslateX";
        final String canvasTranslateYName = "canvasTranslateY";
        PropertyValuesHolder cameraRotateXHolder = PropertyValuesHolder.ofFloat(cameraRotateXName, mCameraRotateX, 0f);
        PropertyValuesHolder cameraRotateYHolder = PropertyValuesHolder.ofFloat(cameraRotateYName, mCameraRotateY, 0f);
        PropertyValuesHolder canvasTranslateXHolder = PropertyValuesHolder.ofFloat(canvasTranslateXName, mCanvasTranslateX, 0f);
        PropertyValuesHolder canvasTranslateYHolder = PropertyValuesHolder.ofFloat(canvasTranslateYName, mCanvasTranslateY, 0f);
        mShakeAnim = ValueAnimator.ofPropertyValuesHolder(cameraRotateXHolder,
                cameraRotateYHolder, canvasTranslateXHolder, canvasTranslateYHolder);
        mShakeAnim.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                float f = 0.571429f;
                return (float) (Math.pow(2.0, (-2 * input)) * Math.sin((input - f / 4) * (2 * Math.PI) / f) + 1);
            }
        });
        mShakeAnim.setDuration(1000);
        mShakeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCameraRotateX = (float) animation.getAnimatedValue(cameraRotateXName);
                mCameraRotateY = (float) animation.getAnimatedValue(cameraRotateYName);
                mCanvasTranslateX = (float) animation.getAnimatedValue(canvasTranslateXName);
                mCanvasTranslateY = (float) animation.getAnimatedValue(canvasTranslateYName);
            }
        });
        mShakeAnim.start();
    }

    /**
     * 获取camera旋转的大小
     *
     * @param event motionEvent
     */
    private void getCameraRotate(MotionEvent event) {
        float rotateX = -(event.getY() - mHeight / 2);
        float rotateY = event.getX() - mWidth / 2;
        //求出此时旋转的大小与半径之比
        Float[] percentArr = getPercent(rotateX, rotateY);
        //最终旋转的大小按比例匀称改变
        mCameraRotateX = percentArr[0] * mMaxCameraRotate;
        mCameraRotateY = percentArr[1] * mMaxCameraRotate;
    }

    /**
     * 当拨动时钟时，会发现时针、分针、秒针和刻度盘会有一个较小的偏移量，形成近大远小的立体偏移效果
     * 一开始我打算使用 matrix 和 camera 的 mCamera.translate(x, y, z) 方法改变 z 的值
     */
    private void getCanvasTranslate(MotionEvent event) {
        float translateX = event.getX() - getWidth() / 2;
        float translateY = event.getY() - getHeight() / 2;
        //求出此时位移的大小与半径之比
        Float[] percentArr = getPercent(translateX, translateY);
        //最终位移的大小按比例匀称改变
        mCanvasTranslateX = percentArr[0] * mMaxCanvasTranslate;
        mCanvasTranslateY = percentArr[1] * mMaxCanvasTranslate;
    }

    /**
     * 获取一个操作旋转或位移大小的比例
     *
     * @return 装有xy比例的float数组
     */
    private Float[] getPercent(Float x, Float y) {
        Float[] percentArr = new Float[2];
        float percentX = x / mRadius;
        float percentY = y / mRadius;
        if (percentX > 1) {
            percentX = 1f;
        } else if (percentX < -1) {
            percentX = -1f;
        }
        if (percentY > 1) {
            percentY = 1f;
        } else if (percentY < -1) {
            percentY = -1f;
        }
        percentArr[0] = percentX;
        percentArr[1] = percentY;
        return percentArr;
    }


    private void log(String msg) {
        Log.d(tag, msg);
    }

    private int dp2px(int dp) {
        return ScreenUtils.dpToPx(dp);
    }
}
