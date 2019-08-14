package com.gobangview.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


/**
 * 介绍: (这里用一句话描述这个类的作用)
 * 邮箱: zhahaijun@bearead.cn
 *
 * @author: zhahaijun
 * @date: 2019/7/31.
 */
public class MyView extends View {

    private String tag = getClass().getSimpleName().intern();
    private Paint linePaint = new Paint();
    private Paint bgPaint = new Paint();
    private Paint textPaint = new Paint();
    int color = Color.parseColor("#287fab");
    int bgColor = Color.parseColor("#343434");
    private int pading = dp2px(10);

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint.setAntiAlias(true);
        linePaint.setColor(color);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(dp2px(2));


        bgPaint.setAntiAlias(true);
        bgPaint.setColor(bgColor);

        textPaint.setAntiAlias(true);
        textPaint.setTextSize(dp2px(10));
        textPaint.setColor(Color.parseColor("#ffffff"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(getWidth() / 2, getHeight() / 2);
        draw3Ciecle(canvas);
        drawLine(canvas);
        drawNumberText(canvas);
    }

    String[] numberText = new String[]{"180", "210", "", "", "0", "30", "60", "90", "120", "150"};

    /**
     * 绘制文字
     *
     * @param canvas
     */
    private void drawNumberText(Canvas canvas) {
        int startX = getWidth() / 2 - pading - dp2px(35);
        for (int i = 0; i < 10; i++) {
            String str = numberText[i];
            float textWidth = textPaint.measureText(str);
            canvas.save();
            canvas.rotate(6 * i);
            canvas.drawText(str, startX - textWidth, 0, textPaint);
            canvas.restore();
        }
    }

    /**
     * 绘制仪表盘
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        linePaint.setColor(color);
        int startX = getWidth() / 2 - pading - dp2px(20);
        int stopX = getWidth() / 2 - pading - dp2px(10);
        int temp, lineCount = 0;
        for (int i = 0; i < 360; i += 6) {
            canvas.save();
            canvas.rotate(i);
            temp = startX;
            if (lineCount % 6 == 0) {
                temp = startX - dp2px(10);
            }
            canvas.drawLine(temp, 0, stopX, 0, linePaint);
            lineCount++;
            canvas.restore();
        }
    }

    /**
     * 绘制外部圆环
     *
     * @param canvas
     */
    private void draw3Ciecle(Canvas canvas) {
        canvas.drawCircle(0, 0, getWidth() / 2 - pading, linePaint);
        canvas.drawCircle(0, 0, getWidth() / 2 - pading - dp2px(10), linePaint);
        canvas.drawCircle(0, 0, getWidth() / 4 - pading, linePaint);
        linePaint.setColor(Color.parseColor("#80287fab"));
        canvas.drawCircle(0, 0, getWidth() / 4 - pading + dp2px(2), linePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        log("widthMode->" + widthMode + "--widthSize->" + widthSize);
        log("heightMode->" + heightMode + "--heightSize->" + heightSize);
        int width = widthSize < heightSize ? widthSize : heightSize;
        setMeasuredDimension(width, width);
    }

    private void log(String msg) {
        Log.d(tag, msg);
    }

    private int dp2px(int dp) {
        return ScreenUtils.dpToPx(dp);
    }
}
