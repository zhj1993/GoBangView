package com.gobangview.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 介绍: (五子棋view)
 * 邮箱: zhahaijun@bearead.cn
 *
 * @author: zhahaijun
 * @date: 2019/8/2.
 */
public class GoBangPanel extends View {

    //宽度
    private int mPanelWidth;
    //格子高度 格子为正方形 也可以视为宽度
    private float mLineHeight;
    //棋盘行数
    private int MAX_LINE = 10;
    //五子棋
    private int MAX_COUNT_IN_LINE = 5;

    /**
     * 横 竖 左斜 右斜
     */
    private int HORIZONTAL = 1;
    private int VERTICAL = 2;
    private int LEFTSLASH = 3;
    private int RIGHTSLASH = 4;

    private Paint mPaint = new Paint();

    private Bitmap mWhitePiece, mBlackPiece;

    //棋子占格子高度的占比
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;

    //白棋先手
    private boolean mIsWhite = true;
    //白棋位置数组
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    //黑棋位置数组
    private ArrayList<Point> mBlackArray = new ArrayList<>();

    private boolean mIsOver;
    private boolean mIsWhiteWinner;

    public GoBangPanel(Context context) {
        this(context, null);
    }

    public GoBangPanel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoBangPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint.setAntiAlias(true);
        mPaint.setColor(0x88000000);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.mipmap.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.mipmap.stone_b1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int size = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            size = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            size = widthSize;
        }
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;

        int size = (int) (mLineHeight * ratioPieceOfLineHeight);
        //重新绘制棋子大小
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, size, size, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, size, size, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPiece(canvas);
        checkGameOver();
    }

    /**
     * 检查是否游戏结束
     */
    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        if (whiteWin) {
            mIsOver = true;
            mIsWhiteWinner = true;
            toast("白棋胜利");
            return;
        }
        boolean blackWin = checkFiveInLine(mBlackArray);
        if (blackWin) {
            mIsOver = true;
            mIsWhiteWinner = false;
            toast("黑棋胜利");
        }
    }

    /**
     * 检查是否有五子连成
     * <p>
     * 五子练成 分别4种情况 横 竖  左斜 右斜
     *
     * @param pointArray
     * @return
     */
    private boolean checkFiveInLine(List<Point> pointArray) {
        for (Point p : pointArray) {
            int x = p.x;
            int y = p.y;
            boolean win = checkDirection(HORIZONTAL, x, y, pointArray);
            if (win) {
                return true;
            }
            win = checkDirection(VERTICAL, x, y, pointArray);
            if (win) {
                return true;
            }
            win = checkDirection(LEFTSLASH, x, y, pointArray);
            if (win) {
                return true;
            }
            win = checkDirection(RIGHTSLASH, x, y, pointArray);
            if (win) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否有五子连成
     *
     * @param direction   方向 横 竖 左斜 右斜
     * @param x
     * @param y
     * @param mPointArray
     * @return
     */

    private boolean checkDirection(int direction, int x, int y, List<Point> mPointArray) {
        int count = 1;
        //左 或者 上 或者 左下斜 或者 左上斜
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            Point point = getLeftPoint(direction, x, y, i);
            if (mPointArray.contains(point)) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        //右 或者 下 或者 右下斜 或者 右上斜
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            Point point = getRightPoint(direction, x, y, i);
            if (mPointArray.contains(point)) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    /**
     * 遍历左 右 或者 下 或者 右下斜 或者 右上斜 的点是否是白棋或者黑棋
     *
     * @param direction
     * @param x
     * @param y
     * @param i
     * @return
     */
    private Point getRightPoint(int direction, int x, int y, int i) {
        Point point;
        if (direction == HORIZONTAL) {
            point = new Point(x + i, y);
        } else if (direction == VERTICAL) {
            point = new Point(x, y + i);
        } else if (direction == LEFTSLASH) {
            point = new Point(x + i, y - i);
        } else {
            point = new Point(x + i, y + i);
        }
        return point;
    }


    /**
     * 遍历左 或者 上 或者 左下斜 或者 左上斜 的点是否是白棋或者黑棋
     *
     * @param direction
     * @param x
     * @param y
     * @param i
     * @return
     */
    private Point getLeftPoint(int direction, int x, int y, int i) {
        Point point;
        if (direction == HORIZONTAL) {
            point = new Point(x - i, y);
        } else if (direction == VERTICAL) {
            point = new Point(x, y - i);
        } else if (direction == LEFTSLASH) {
            point = new Point(x - i, y + i);
        } else {
            point = new Point(x - i, y - i);
        }
        return point;
    }

    /**
     * 绘制棋子
     *
     * @param canvas
     */
    private void drawPiece(Canvas canvas) {
        //白子
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            Point point = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (point.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (point.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }
        //黑子
        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            Point point = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (point.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (point.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }
    }

    /**
     * 绘制棋盘
     *
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;
        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);

            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);

            canvas.drawLine(y, startX, y, endX, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsOver) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                int x = (int) event.getX();
                int y = (int) event.getY();
                Point point = getValidPoint(x, y);
                if (mWhiteArray.contains(point) || mBlackArray.contains(point)) {
                    return false;
                }
                if (mIsWhite) {
                    mWhiteArray.add(point);
                } else {
                    mBlackArray.add(point);
                }
                mIsWhite = !mIsWhite;
                invalidate();
                return true;
            default:
                break;
        }
        return true;
    }

    private static final String INSTANCE = "INSTANCE";
    private static final String GAME_OVER = "GAME_OVER";
    private static final String WHITE_ARRAY = "WHITE_ARRAY";
    private static final String BLICK_ARRAY = "BLICK_ARRAY";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(GAME_OVER, mIsOver);
        bundle.putParcelableArrayList(WHITE_ARRAY, mWhiteArray);
        bundle.putParcelableArrayList(BLICK_ARRAY, mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mIsOver = bundle.getBoolean(GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(BLICK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * 重新开始
     */
    public void restart() {
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsOver = false;
        mIsWhiteWinner = false;
        invalidate();
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    private void toast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
}
