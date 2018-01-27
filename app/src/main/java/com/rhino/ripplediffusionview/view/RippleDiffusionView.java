package com.rhino.ripplediffusionview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>The ripple custom view.</p>
 *
 * @since Created by LuoLin on 2018/1/27.
 **/
public class RippleDiffusionView extends View {

    private static final int DEFAULT_RIPPLE_COUNT = 8;
    private static final int DEFAULT_DELAY_TIME = 1000; // ms
    private static final int DEFAULT_DURATION_TIME = 8000; // ms
    private static final int DEFAULT_RIPPLE_COLOR = 0x33000000;
    private static final int DEFAULT_STROKE_WIDTH_MIN = 20;
    private static final int DEFAULT_STROKE_WIDTH_MAX = 80;
    private static final int DEFAULT_RADIUS_MIN = 0;
    private static final int DEFAULT_RADIUS_MAX = 1000;

    public enum Style {
        STROKE,
        FILL
    }

    /**
     * The ripple style.
     */
    private Style mStyle = Style.STROKE;

    /**
     * The color of ripple.
     */
    private int mRippleColor = DEFAULT_RIPPLE_COLOR;
    /**
     * The min width of stroke.
     */
    private float mMinStrokeWidth = DEFAULT_STROKE_WIDTH_MIN;
    /**
     * The max width of stroke.
     */
    private float mMaxStrokeWidth = DEFAULT_STROKE_WIDTH_MAX;
    /**
     * The max radius of ripple.
     */
    private float mMinRadius = DEFAULT_RADIUS_MIN;
    /**
     * The min radius of ripple.
     */
    private float mMaxRadius = DEFAULT_RADIUS_MAX;

    /**
     * The width of view.
     */
    private int mViewWidth;
    /**
     * The height of view.
     */
    private int mViewHeight;
    /**
     * The list of ripple items.
     */
    private List<RippleItem> mRippleItemList = new ArrayList<>();
    /**
     * Whether anim started.
     */
    private boolean mIsAnimStarted;


    public RippleDiffusionView(Context context) {
        this(context, null);
    }

    public RippleDiffusionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleDiffusionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mViewWidth = widthSize;
        } else {
            mViewWidth = getWidth();
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mViewHeight = heightSize;
        } else {
            mViewHeight = getHeight();
        }
        initView(mViewWidth, mViewHeight);
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        if (mIsAnimStarted) {
            for (RippleItem ring : mRippleItemList) {
                ring.draw(canvas);
            }
        }
        canvas.restore();
    }

    /**
     * Do something init.
     */
    private void init() {
        for (int i = 0; i < DEFAULT_RIPPLE_COUNT; i++) {
            mRippleItemList.add(new RippleItem(i * DEFAULT_DELAY_TIME, DEFAULT_DURATION_TIME));
        }
    }

    /**
     * Init view size.
     **/
    private void initView(int width, int height) {
        if (0 >= width || 0 >= height) {
            return;
        }

        mMinRadius = 0;
        mMaxRadius = 0.6F * Math.max(width, height);
    }

    private class RippleItem {

        private ValueAnimator mValueAnimator;
        private float mAnimValue;
        private Paint mPaint;

        public RippleItem(long delay, long duration) {
            this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.mValueAnimator = ValueAnimator.ofFloat(0, 1F);
            this.mValueAnimator.setDuration(duration);
            this.mValueAnimator.setInterpolator(new DecelerateInterpolator(2));
            this.mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
            this.mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            this.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mAnimValue = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            this.mValueAnimator.setStartDelay(delay);
        }

        public void draw(Canvas canvas) {
            if (!mValueAnimator.isRunning()) {
                return;
            }
            mPaint.setColor(getDrawColor());
            if (Style.STROKE == mStyle) {
                this.mPaint.setStyle(Paint.Style.STROKE);
                this.mPaint.setStrokeWidth(getDrawStrokeWidth());
            } else if(Style.FILL == mStyle){
                this.mPaint.setStyle(Paint.Style.FILL);
            }
            canvas.drawCircle(0, 0, getDrawRadius(), mPaint);
        }

        private float getDrawRadius() {
            return this.mAnimValue * (mMaxRadius - mMinRadius) + mMinRadius;
        }

        private float getDrawStrokeWidth() {
            return this.mAnimValue * (mMaxStrokeWidth - mMinStrokeWidth) + mMinStrokeWidth;
        }

        private int getDrawColor() {
            return alphaColor((1 - this.mAnimValue) * 0.8F, mRippleColor);
        }

        private int alphaColor(float alpha, int color) {
            return (Math.round(alpha * (color >>> 24)) << 24) | (color & 0x00FFFFFF);
        }
    }

    /**
     * Set color of ripple.
     * @param color color
     */
    public void setRippleColor(int color) {
        this.mRippleColor = color;
        invalidate();
    }

    /**
     * Start float anim.
     */
    public void startAnim() {
        mIsAnimStarted = true;
        for (RippleItem ring : mRippleItemList) {
            ring.mValueAnimator.start();
        }
    }

    /**
     * Stop float anim.
     */
    public void stopAnim() {
        mIsAnimStarted = false;
        for (RippleItem ring : mRippleItemList) {
            ring.mValueAnimator.cancel();
        }
        invalidate();
    }

    /**
     * Return whether anim started.
     *
     * @return true tarted, false not started
     */
    public boolean isAnimStarted() {
        return mIsAnimStarted;
    }

    /**
     * Get the style of ripple.
     * @see Style
     */
    public Style getStyle() {
        return mStyle;
    }

    /**
     * Set the style of ripple.
     * @param style the style of ripple
     * @see Style
     */
    public void setStyle(Style style) {
        this.mStyle = style;
        invalidate();
    }

    /**
     * Set the min width of stroke.
     * @param width the min width of stroke
     */
    public void setMinStrokeWidth(float width) {
        this.mMinStrokeWidth = width;
        invalidate();
    }

    /**
     * Set the max width of stroke.
     * @param width the max width of stroke
     */
    public void setMaxStrokeWidth(float width) {
        this.mMaxStrokeWidth = width;
        invalidate();
    }

    /**
     * Set the min radius of ripple.
     * @param radius the min radius of ripple
     */
    public void setMaxRadius(float radius) {
        this.mMaxRadius = radius;
        invalidate();
    }

    /**
     * Set the max radius of ripple.
     * @param radius the max radius of ripple
     */
    public void setMinRadius(float radius) {
        this.mMinRadius = radius;
        invalidate();
    }

}
