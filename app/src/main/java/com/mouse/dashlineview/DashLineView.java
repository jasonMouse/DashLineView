package com.mouse.dashlineview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author by YiSong
 * @date 2018/12/26
 * 两边两个半圆，中间一条虚线
 */
public class DashLineView extends View {

    private static final int DEFAULT_DASH_LINE_WIDTH = 10;
    private static final int DEFAULT_SEMICIRCLE_RADIUS = 4;
    private static final int DEFAULT_SEMICIRCLE_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_DASH_LINE_HEIGHT = 1;
    private static final int DEFAULT_DASH_LINE_GAP = 5;
    private static final int DEFAULT_DASH_LINE_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_DASH_LINE_MARGIN = 0;

    private Context context;
    //半圆画笔
    private Paint semicirclePaint;
    //虚线画笔
    private Paint dashLinePaint;
    //半圆半径
    private float semicircleRadius = DEFAULT_SEMICIRCLE_RADIUS;
    //半圆颜色
    private int semicircleColor = DEFAULT_SEMICIRCLE_COLOR;
    //虚线的长度
    private float dashLineWidth = DEFAULT_DASH_LINE_WIDTH;
    //虚线的高度
    private float dashLineHeight = DEFAULT_DASH_LINE_HEIGHT;
    //虚线的间距
    private float dashLineGap = DEFAULT_DASH_LINE_GAP;
    //虚线的颜色
    private int dashLineColor = DEFAULT_DASH_LINE_COLOR;
    // 开启第一个半圆
    private boolean isSemicircleFirstShow = true;
    // 开启第二个半圆
    private boolean isSemicircleLastShow = true;
    // 第一个半圆距离虚线的margin
    private float dashLineFirstMargin = DEFAULT_DASH_LINE_MARGIN;
    // 第二个半圆距离虚线的margin
    private float dashLineLastMargin = DEFAULT_DASH_LINE_MARGIN;

    //绘制虚线后X轴剩余距离
    private int remindDashLineX;
    //绘制虚线后Y轴剩余距离
    private int remindDashLineY;
    //虚线数量X
    private int dashLineNumX;
    //虚线数量Y
    private int dashLineNumY;

    //view宽度
    private int viewWidth;
    //view的高度
    private int viewHeight;

    // 整体的方向
    private int orientation = LinearLayout.HORIZONTAL;

    public DashLineView(@NonNull Context context) {
        this(context, null);
    }

    public DashLineView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashLineView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DashLineView, defStyle, 0);
        semicircleRadius = typedArray.getDimensionPixelSize(R.styleable.DashLineView_dlv_semicircle_radius, dp2Px(DEFAULT_SEMICIRCLE_RADIUS));
        semicircleColor = typedArray.getColor(R.styleable.DashLineView_dlv_semicircle_color, DEFAULT_SEMICIRCLE_COLOR);

        dashLineGap = typedArray.getDimensionPixelSize(R.styleable.DashLineView_dlv_dash_line_gap, dp2Px(DEFAULT_DASH_LINE_GAP));
        dashLineHeight = typedArray.getDimensionPixelSize(R.styleable.DashLineView_dlv_dash_line_height, dp2Px(DEFAULT_DASH_LINE_HEIGHT));
        dashLineWidth = typedArray.getDimensionPixelSize(R.styleable.DashLineView_dlv_dash_line_width, dp2Px(DEFAULT_DASH_LINE_WIDTH));
        dashLineColor = typedArray.getColor(R.styleable.DashLineView_dlv_dash_line_color, DEFAULT_DASH_LINE_COLOR);

        isSemicircleFirstShow = typedArray.getBoolean(R.styleable.DashLineView_dlv_semicircle_first_show, isSemicircleFirstShow);
        isSemicircleLastShow = typedArray.getBoolean(R.styleable.DashLineView_dlv_semicircle_last_show, isSemicircleLastShow);

        dashLineFirstMargin = typedArray.getDimensionPixelSize(R.styleable.DashLineView_dlv_dash_line_first_margin, dp2Px(DEFAULT_DASH_LINE_MARGIN));
        dashLineLastMargin = typedArray.getDimensionPixelSize(R.styleable.DashLineView_dlv_dash_line_last_margin, dp2Px(DEFAULT_DASH_LINE_MARGIN));
        orientation = typedArray.getInt(R.styleable.DashLineView_dlv_dash_line_orientation, orientation);
        typedArray.recycle();
        init();
    }

    private void init() {
        semicirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        semicirclePaint.setDither(true);
        semicirclePaint.setColor(semicircleColor);
        semicirclePaint.setStyle(Paint.Style.FILL);

        dashLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashLinePaint.setDither(true);
        dashLinePaint.setColor(dashLineColor);
        dashLinePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            float exceptWidth = 0f;
            if (orientation == LinearLayout.HORIZONTAL) {
                float singleTap = dashLineWidth + dashLineGap;
                exceptWidth = getPaddingLeft() + getPaddingRight() + singleTap * 5 + dashLineFirstMargin + dashLineLastMargin;
                if (isSemicircleFirstShow) {
                    exceptWidth = exceptWidth + semicircleRadius;
                }
                if (isSemicircleLastShow) {
                    exceptWidth = exceptWidth + semicircleRadius;
                }
            } else {
                exceptWidth = getPaddingLeft() + getPaddingRight();
                if (isSemicircleFirstShow || isSemicircleLastShow) {
                    exceptWidth = exceptWidth + Math.max(dashLineWidth, semicircleRadius * 2);
                }
            }
            widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) exceptWidth, MeasureSpec.EXACTLY);
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            float exceptHeight = 0f;
            if (orientation == LinearLayout.HORIZONTAL) {
                exceptHeight = getPaddingTop() + getPaddingBottom();
                if (isSemicircleFirstShow || isSemicircleLastShow) {
                    exceptHeight = exceptHeight + Math.max(dashLineHeight, semicircleRadius * 2);
                }
            } else {
                float singleTap = dashLineHeight + dashLineGap;
                exceptHeight = getPaddingTop() + getPaddingBottom() + singleTap * 5 + dashLineFirstMargin + dashLineLastMargin;
                if (isSemicircleFirstShow) {
                    exceptHeight = exceptHeight + semicircleRadius;
                }
                if (isSemicircleLastShow) {
                    exceptHeight = exceptHeight + semicircleRadius;
                }
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) exceptHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);
        viewWidth = w;
        viewHeight = h;
        calculate();
    }

    private void calculate() {
        // 如果当前是水平方向
        if (orientation == LinearLayout.HORIZONTAL) {
            float dashLineCanDrawWidth = viewWidth + dashLineGap - dashLineFirstMargin - dashLineLastMargin;
            if (isSemicircleFirstShow) {
                dashLineCanDrawWidth = dashLineCanDrawWidth - semicircleRadius;
            }
            if (isSemicircleLastShow) {
                dashLineCanDrawWidth = dashLineCanDrawWidth - semicircleRadius;
            }
            remindDashLineX = (int) (dashLineCanDrawWidth % (dashLineWidth + dashLineGap));
            dashLineNumX = (int) (dashLineCanDrawWidth / (dashLineWidth + dashLineGap));
        } else {
            float dashLineCanDrawHeight = viewHeight + dashLineGap - dashLineFirstMargin - dashLineLastMargin;
            if (isSemicircleFirstShow) {
                dashLineCanDrawHeight = dashLineCanDrawHeight - semicircleRadius;
            }
            if (isSemicircleLastShow) {
                dashLineCanDrawHeight = dashLineCanDrawHeight - semicircleRadius;
            }
            remindDashLineY = (int) (dashLineCanDrawHeight % (dashLineWidth + dashLineGap));
            dashLineNumY = (int) (dashLineCanDrawHeight / (dashLineWidth + dashLineGap));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 如果当前是水平方向
        if (orientation == LinearLayout.HORIZONTAL) {
            // 绘制两个圆
            if (isSemicircleFirstShow) {
                canvas.drawCircle(0, viewHeight / 2, semicircleRadius, semicirclePaint);
            }
            if (isSemicircleLastShow) {
                canvas.drawCircle(viewWidth, viewHeight / 2, semicircleRadius, semicirclePaint);
            }
            // 绘制虚线
            for (int i = 0; i < dashLineNumX; i++) {
                float x = dashLineFirstMargin + remindDashLineX / 2 + (dashLineGap + dashLineWidth) * i;
                if (isSemicircleFirstShow) {
                    x = x + semicircleRadius;
                }
                canvas.drawRect(x, (viewHeight - dashLineHeight) / 2, x + dashLineWidth, (viewHeight + dashLineHeight) / 2, dashLinePaint);
            }
        } else {
            // 绘制两个圆
            if (isSemicircleFirstShow) {
                canvas.drawCircle(viewWidth / 2, 0, semicircleRadius, semicirclePaint);
            }
            if (isSemicircleLastShow) {
                canvas.drawCircle(viewWidth / 2, viewHeight, semicircleRadius, semicirclePaint);
            }
            for (int i = 0; i < dashLineNumY; i++) {
                float y = dashLineFirstMargin + remindDashLineY / 2 + (dashLineGap + dashLineWidth) * i;
                if (isSemicircleFirstShow) {
                    y = y + semicircleRadius;
                }
                canvas.drawRect((viewWidth - dashLineWidth) / 2, y, (viewWidth + dashLineWidth) / 2, y + dashLineHeight, dashLinePaint);
            }
        }
    }

    private int dp2Px(float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    private int px2Dp(float px) {
        return (int) (px / context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public float getSemicircleRadius() {
        return px2Dp(semicircleRadius);
    }

    // 半圆的半径
    public void setSemicircleRadius(float semicircleRadius) {
        if (this.semicircleRadius != semicircleRadius) {
            this.semicircleRadius = semicircleRadius;
            calculate();
            invalidate();
        }
    }

    public int getSemicircleColor() {
        return semicircleColor;
    }

    // 半圆的颜色
    public void setSemicircleColor(int semicircleColor) {
        if (this.semicircleColor != semicircleColor) {
            this.semicircleColor = semicircleColor;
            calculate();
            invalidate();
        }
    }

    public float getDashLineWidth() {
        return px2Dp(dashLineWidth);
    }

    // 虚线的宽度
    public void setDashLineWidth(float dashLineWidth) {
        if (this.dashLineWidth != dashLineWidth) {
            this.dashLineWidth = dashLineWidth;
            calculate();
            invalidate();
        }
    }

    public float getDashLineHeight() {
        return px2Dp(dashLineHeight);
    }

    // 虚线的高度
    public void setDashLineHeight(float dashLineHeight) {
        if (this.dashLineHeight != dashLineHeight) {
            this.dashLineHeight = dashLineHeight;
            calculate();
            invalidate();
        }
    }

    public float getDashLineGap() {
        return px2Dp(dashLineGap);
    }

    // 虚线间距
    public void setDashLineGap(float dashLineGap) {
        if (this.dashLineGap != dashLineGap) {
            this.dashLineGap = dashLineGap;
            calculate();
            invalidate();
        }
    }

    public int getDashLineColor() {
        return dashLineColor;
    }

    //  虚线的颜色
   public void setDashLineColor(int dashLineColor) {
        if (this.dashLineColor != dashLineColor) {
            this.dashLineColor = dashLineColor;
            calculate();
            invalidate();
        }
    }

    public boolean isSemicircleFirstShow() {
        return isSemicircleFirstShow;
    }

    // 开启第一个半圆
    public void setSemicircleFirstShow(boolean isSemicircleFirstShow) {
        if (this.isSemicircleFirstShow != isSemicircleFirstShow) {
            this.isSemicircleFirstShow = isSemicircleFirstShow;
            calculate();
            invalidate();
        }
    }

    public boolean isSemicircleLastShow() {
        return isSemicircleLastShow;
    }

    // 开启第二个半圆
    public void setSemicircleLastShow(boolean isSemicircleLastShow) {
        if (this.isSemicircleLastShow != isSemicircleLastShow) {
            this.isSemicircleLastShow = isSemicircleLastShow;
            calculate();
            invalidate();
        }
    }

    public float getDashLineFirstMargin() {
        return px2Dp(dashLineFirstMargin);
    }

    // 第一个半圆距离虚线的margin
    public void setDashLineMarginLeft(float dashLineFirstMargin) {
        if (this.dashLineFirstMargin != dashLineFirstMargin) {
            this.dashLineFirstMargin = dashLineFirstMargin;
            calculate();
            invalidate();
        }
    }

    public float getDashLineLastMargin() {
        return px2Dp(dashLineLastMargin);
    }

    // 第二个半圆距离虚线的margin
    public void setDashLineLastMargin(float dashLineLastMargin) {
        if (this.dashLineLastMargin != dashLineLastMargin) {
            this.dashLineLastMargin = dashLineLastMargin;
            calculate();
            invalidate();
        }
    }
}