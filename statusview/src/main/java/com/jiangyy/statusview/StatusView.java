package com.jiangyy.statusview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.view.animation.LinearInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * TODO: document your custom view class.
 *
 * @author Administrator
 */
public class StatusView extends View {

    public interface OnRetryListener {
        void onRetryClick();
    }

    public interface OnEmptyListener {
        void onEmptyClick();
    }

    private OnRetryListener mOnRetryListener;
    private OnEmptyListener mOnEmptyListener;

    private Paint mPaint;
    private TextPaint mTextPaint;

    @ColorRes
    private int mTextColor = android.R.color.darker_gray;
    private String mTextString = "";
    private float mTextSize = getResources().getDimension(R.dimen.default_text_size);
    private Drawable mIconDrawable;
    private Drawable mEmptyDrawable, mErrorDrawable, mNoNetworkDrawable;

    private int mWidth = 0, mHeight = 0;

    private float mTextHeight, mTextWidth;
    private Rect mRect;
    private RectF mRectF;

    private float mStartAngle = 0f;

    public static final int DEFAULT = -1;
    public static final int LOADING = 0;
    public static final int FINISHED = 1;
    public static final int EMPTY = 2;
    public static final int ERROR = 3;
    public static final int NO_NETWORK = 4;

    @Status
    private int mStatusNo = DEFAULT;

    @IntDef({DEFAULT, LOADING, FINISHED, EMPTY, ERROR, NO_NETWORK})
    @Retention(RetentionPolicy.SOURCE)
    @interface Status {
    }

    public StatusView(Context context) {
        super(context);
        init(null, 0);
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        setClickable(true);
        setFocusable(true);
        setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.StatusView, defStyle, 0);

        if (a.hasValue(R.styleable.StatusView_textColor)) {
            mTextColor = a.getResourceId(R.styleable.StatusView_textColor, android.R.color.darker_gray);
        }
        if (a.hasValue(R.styleable.StatusView_emptyIcon)) {
            mEmptyDrawable = a.getDrawable(R.styleable.StatusView_emptyIcon);
        }
        if (a.hasValue(R.styleable.StatusView_errorIcon)) {
            mErrorDrawable = a.getDrawable(R.styleable.StatusView_errorIcon);
        }
        if (a.hasValue(R.styleable.StatusView_noNetWorkIcon)) {
            mNoNetworkDrawable = a.getDrawable(R.styleable.StatusView_noNetWorkIcon);
        }

        a.recycle();

        mTextPaint = new TextPaint();
        mPaint = new Paint();

        invalidatePaint();
        isLoading();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStatusNo == LOADING || mStatusNo == FINISHED) {
                    return;
                }
                if (mStatusNo == EMPTY) {
                    if (mOnEmptyListener != null) {
                        mOnEmptyListener.onEmptyClick();
                    } else {
                        if (mOnRetryListener != null) {
                            mOnRetryListener.onRetryClick();
                        }
                    }
                    return;
                }
                isLoading();
                if (mOnRetryListener != null) {
                    mOnRetryListener.onRetryClick();
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRect.set(
                3 * mWidth / 7,
                (int) ((mHeight - mWidth / 7 - getPaddingTop() - getPaddingBottom() - mTextHeight) / 2),
                4 * mWidth / 7,
                (int) ((mHeight + mWidth / 7 - getPaddingTop() - getPaddingBottom() - mTextHeight) / 2)
        );
        mRectF.set(mRect);
        if (mStatusNo != FINISHED) {
            if (mIconDrawable != null) {
                mIconDrawable.setBounds(mRect);
                mIconDrawable.draw(canvas);
            } else {
                mPaint.setColor(Color.LTGRAY);
                canvas.drawCircle(mRect.centerX(), mRect.centerY(), mRect.width() / 2, mPaint);
                mPaint.setColor(Color.GRAY);
                canvas.drawArc(mRectF, mStartAngle, 90, false, mPaint);
            }
            canvas.drawText(mTextString,
                    mRect.centerX() - mTextWidth / 2,
                    mRect.centerY() + mRect.height(),
                    mTextPaint);
        }
    }

    private void invalidatePaint() {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(ContextCompat.getColor(getContext(), mTextColor));

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);

        if (mRect == null) {
            mRect = new Rect();
        }
        if (mRectF == null) {
            mRectF = new RectF();
        }

        mTextWidth = mTextPaint.measureText(mTextString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    public void isLoading() {
        if (mStatusNo == LOADING) {
            return;
        }
        setVisibility(VISIBLE);
        mStatusNo = LOADING;
        mIconDrawable = null;
        mTextString = "正在加载...";
        startAnimator();
        invalidatePaint();
        invalidate();
    }

    public void isNoNetwork() {
        isNoNetwork("网络连接失败");
    }

    public void isNoNetwork(CharSequence message) {
        if (mStatusNo == NO_NETWORK) {
            return;
        }
        setVisibility(VISIBLE);
        mStatusNo = NO_NETWORK;
        if (mNoNetworkDrawable == null) {
            mIconDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_no_network);
        } else {
            mIconDrawable = mNoNetworkDrawable;
        }
        mTextString = message.toString();
        stopAnimator();
        invalidatePaint();
        invalidate();
    }

    public void isEmpty() {
        isEmpty("数据为空");
    }

    public void isEmpty(CharSequence message) {
        if (mStatusNo == EMPTY) {
            return;
        }
        setVisibility(VISIBLE);
        mStatusNo = EMPTY;
        mTextString = message.toString();
        if (mEmptyDrawable == null) {
            mIconDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_empty);
        } else {
            mIconDrawable = mEmptyDrawable;
        }
        stopAnimator();
        invalidatePaint();
        invalidate();
    }

    public void isError() {
        isError("数据加载失败");
    }

    public void isError(CharSequence message) {
        if (mStatusNo == ERROR) {
            return;
        }
        setVisibility(VISIBLE);
        mStatusNo = ERROR;
        mTextString = message.toString();
        if (mErrorDrawable == null) {
            mIconDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_error);
        } else {
            mIconDrawable = mErrorDrawable;
        }
        stopAnimator();
        invalidatePaint();
        invalidate();
    }

    public void isFinished() {
        if (mStatusNo == FINISHED) {
            return;
        }
        setVisibility(GONE);
        mStatusNo = FINISHED;
        stopAnimator();
        invalidatePaint();
        invalidate();
    }

    public void setOnRetryListener(OnRetryListener onRetryListener) {
        mOnRetryListener = onRetryListener;
    }

    public void setOnEmptyListener(OnEmptyListener onEmptyListener) {
        mOnEmptyListener = onEmptyListener;
    }

    private void startAnimator() {
        stopAnimator();
        startViewAnim(0f, 1f, 1000);
    }

    private void stopAnimator() {
        if (valueAnimator != null) {
            clearAnimation();
            valueAnimator.setRepeatCount(1);
            valueAnimator.cancel();
            valueAnimator.end();
        }
    }

    private ValueAnimator valueAnimator;

    private ValueAnimator startViewAnim(float startF, final float endF, long time) {
        valueAnimator = ValueAnimator.ofFloat(startF, endF);

        valueAnimator.setDuration(time);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                float value = (float) valueAnimator.getAnimatedValue();
                mStartAngle = 360 * value;

                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        if (!valueAnimator.isRunning()) {
            valueAnimator.start();
        }

        return valueAnimator;
    }


}