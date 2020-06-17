package com.javason.myleafloading;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentNavigableMap;

import androidx.annotation.Nullable;

public class LeafLoadingView extends View {
    private static final String TAG = "LeafLoading";
    //淡白色
    private static final int WHITE_COLOR = 0xfffde399;
    //橙色
    private static final int ORANGE_COLOR = 0xfffa800;
    //中等振幅大小
    private static final int MIDDLE_AMPLITUDE = 13;
    //不同类型之间振幅差距
    private static final int AMPLITUDE_DISPARITY = 5;
    //总进度
    private static final int TOTAL_PROGRESS = 100;
    //叶子飘动一个周期所花的时间
    private static final long LEAF_FLOAT_TIME = 3000;
    //叶子旋转一周所花的时间
    private static final long LEAF_ROTATE_TIME = 2000;
    //用于控制绘制进度条距离左/上/下的距离
    private static final int LEFT_MARGIN = 9;
    //用于控制绘制的进度条距离右的距离
    private static final int RIGHT_MARGIN = 25;
    private int mLeftMargin, mRightMargin;
    //中等振幅大小
    private int mMiddleAmplitude = MIDDLE_AMPLITUDE;
    // 振幅差
    private int mAmplitudeDisparity = AMPLITUDE_DISPARITY;

    // 叶子飘动一个周期所花的时间
    private long mLeafFloatTime;
    // 叶子旋转一周需要的时间
    private long mLeafRotateTime;
    private Resources mResources;
    private Bitmap mLeafBitmap;
    private int mLeafWidth, mLeafHeight;

    private Bitmap mOuterBitmap;
    private Rect mOuterSrcRect, mOuterDestRect;
    private int mOuterWidth, mOuterHeight;

    private int mTotalWidth, mTotalHeight;

    private Paint mBitmapPaint, mWhitePaint, mOrangePaint;
    private RectF mWhiteRectF, mOrangeRectF, mArcRectF;
    // 当前进度
    private int mProgress;
    // 所绘制的进度条部分的宽度
    private int mProgressWidth;
    // 当前所在的绘制的进度条的位置
    private int mCurrentProgressPosition;
    // 弧形的半径
    private int mArcRadius;

    // arc的右上角的x坐标，也是矩形x坐标的起始点
    private int mArcRightLocation;
    // 用于产生叶子信息
    private LeafFactory mLeafFactory;
    // 产生出的叶子信息
    private List<Leaf> mLeafInfos;
    // 用于控制随机增加的时间不抱团
    private int mAddTime;


    public LeafLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mResources = getResources();
        mLeftMargin = UiUtils.dp2px(context, LEFT_MARGIN);
        mRightMargin = UiUtils.dp2px(context, RIGHT_MARGIN);
        mLeafFloatTime = LEAF_FLOAT_TIME;
        mLeafRotateTime = LEAF_ROTATE_TIME;
        initBitmap();
        initPaint();
        mLeafFactory = new LeafFactory();
        mLeafInfos = mLeafFactory.generateLeafs();
    }

    private void initPaint() {
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setColor(WHITE_COLOR);

        mOrangePaint = new Paint();
        mOrangePaint.setAntiAlias(true);
        mOrangePaint.setColor(ORANGE_COLOR);
    }

    private void initBitmap() {
        mLeafBitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.leaf)).getBitmap();
        mLeafWidth = mLeafBitmap.getWidth();
        mLeafHeight = mLeafBitmap.getHeight();

        mOuterBitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.leaf_kuang)).getBitmap();
        mOuterWidth = mOuterBitmap.getWidth();
        mOuterHeight = mOuterBitmap.getHeight();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制进度条和叶子
        //之所以把叶子放在进度条里绘制，主要是层级原因
        drawProgressAndLeaf(canvas);
        canvas.drawBitmap(mOuterBitmap, mOuterSrcRect, mOuterDestRect, mBitmapPaint);
        postInvalidate();
    }

    private void drawProgressAndLeaf(Canvas canvas) {
        if (mProgress >= TOTAL_PROGRESS) {
            mProgress = 0;
        }
        //mProgressWidth为当前进度条的宽度，根据当前进度算出进度跳的位置
        mCurrentProgressPosition = mProgressWidth * mProgress / TOTAL_PROGRESS;
        //即当前位置在图中所示1的位置
        if (mCurrentProgressPosition < mArcRadius) {
            Log.i(TAG, "mProgress= " + mProgress + "----mCurrentProgressPosition= " +
                    mCurrentProgressPosition + "----mArcProgressWidth=" + mArcRadius);
            //1.绘制白色Arc,绘制orange Arc
            //2.绘制白色Rect
            //1.绘制白色Arc
            canvas.drawArc(mArcRectF, 90, 180, false, mWhitePaint);
            //2.绘制白色rec
            mWhiteRectF.left = mArcRightLocation;
            canvas.drawRect(mWhiteRectF, mWhitePaint);
            //绘制叶子
            drawLeafs(canvas);
            //3.绘制棕色Arc
            //单边角度
            int angle = (int) Math.toDegrees(Math.acos((mArcRadius - mCurrentProgressPosition) / (float) mArcRadius));
            //起始位置
            int startAngle = 180 - angle;
            //扫过的角度
            int sweepAngle = 2 * angle;
            Log.i(TAG, "startAngle = " + startAngle);
            canvas.drawArc(mArcRectF, startAngle, sweepAngle, false, mOrangePaint);
        } else {
            Log.i(TAG, "mProgress= " + mProgress + "----transfer------mCurrentProgressPosition=" +
                    mCurrentProgressPosition + "--mArcProgressWidth= " + mArcRadius);
            //1.绘制white rect
            //2.绘制OrangeArc
            //3.绘制orange rect
            //这个层级绘制能让人感觉叶子是融进棕色进度条中


            //1.绘制WhiteRect
            mWhiteRectF.left = mCurrentProgressPosition;
            canvas.drawRect(mWhiteRectF, mWhitePaint);
            //绘制叶子
            drawLeafs(canvas);
            //3.绘制orangeRect
            mOrangeRectF.left = mArcRightLocation;
            mOrangeRectF.right = mCurrentProgressPosition;
            canvas.drawRect(mOrangeRectF, mOrangePaint);

        }
    }

    /**
     * 绘制叶子
     *
     * @param canvas 传入画布
     */
    private void drawLeafs(Canvas canvas) {
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < mLeafInfos.size(); i++) {
            Leaf leaf = mLeafInfos.get(i);
            if (currentTime > leaf.startTime && leaf.startTime != 0) {
                //绘制叶子--根据叶子的类型和当前时间得出叶子的x ，y
                getLeafLocation(leaf, currentTime);
                //根据时间计算旋转角度
                canvas.save();
                //通过Matrix控制叶子旋转
                Matrix matrix = new Matrix();
                float transX = mLeftMargin + leaf.x;
                float transY = mLeftMargin + leaf.y;
                Log.i(TAG, "left.x= " + leaf.x + "--leaf.y: " + leaf.y);
                matrix.postTranslate(transX, transY);
//通过时间
            }
        }

    }

    private void getLeafLocation(Leaf leaf, long currentTime) {
        long intervalTime = currentTime - leaf.startTime;
        mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        if (intervalTime < 0) {
            return;
        } else if (intervalTime > mLeafFloatTime) {
            leaf.startTime = System.currentTimeMillis() + new Random().nextInt((int) mLeafFloatTime);
        }
        float fraction = (float) intervalTime / mLeafFloatTime;
        leaf.x = (int) (mProgressWidth - mProgressWidth * fraction);
        leaf.y = getLocationY(leaf);

    }

    private int getLocationY(Leaf leaf) {
        // y = A(wx+Q)+h
        float w = (float) ((float) 2 * Math.PI / mProgressWidth);
        float a = mMiddleAmplitude;
        switch (leaf.type) {
            case LITTLE:
                // 小振幅 ＝ 中等振幅 － 振幅差
                a = mMiddleAmplitude - mAmplitudeDisparity;
                break;
            case MIDDLE:
                a = mMiddleAmplitude;
                break;
            case BIG:
                // 小振幅 ＝ 中等振幅 + 振幅差
                a = mMiddleAmplitude + mAmplitudeDisparity;
                break;
            default:
                break;
        }
        Log.i(TAG, "---a = " + a + "---w = " + w + "--leaf.x = " + leaf.x);
        return (int) (a * Math.sin(w * leaf.x)) + mArcRadius * 2 / 3;
    }

    private enum StartType {
        LITTLE, MIDDLE, BIG
    }

    private class Leaf {
        //绘制部分的位置
        float x, y;
        //控制叶子飘动的幅度
        StartType type;
        //旋转角度
        int rotateAngle;
        //旋转方向--0代表顺时针，--1代表逆时针
        int rotateDirection;
        //起始时间
        long startTime;
    }

    private class LeafFactory {
        private static final int MAX_LEAFS = 8;
        Random random = new Random();

        //生成一个叶子的信息
        public Leaf generateLeaf() {
            int randomType = random.nextInt(3);
            Leaf leaf = new Leaf();
            //随机类型，随机振幅
            StartType type = StartType.MIDDLE;
            switch (randomType) {
                case 0:
                    break;
                case 1:
                    type = StartType.LITTLE;
                    break;
                case 2:
                    type = StartType.BIG;
                    break;
                default:
                    break;
            }
            leaf.type = type;
            // 随机起始的旋转角度
            leaf.rotateAngle = random.nextInt(360);
            leaf.rotateDirection = random.nextInt(2);
            //为了产生交错感，让开始的时间具有一定随机性
            mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
            mAddTime += random.nextInt((int) (mLeafFloatTime * 2));
            leaf.startTime = System.currentTimeMillis() + mAddTime;
            return leaf;
        }

        //根据最大叶子数量产生叶子信息
        public List<Leaf> generateLeafs() {
            return generateLeafs(MAX_LEAFS);
        }

        public List<Leaf> generateLeafs(int leafSize) {
            List<Leaf> leafs = new LinkedList<>();
            for (int i = 0; i < leafSize; i++) {
                leafs.add(generateLeaf());
            }
            return leafs;
        }
    }
}
