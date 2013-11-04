package com.actionbarsherlock.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.actionbarsherlock.internal.nineoldandroids.widget.NineLinearLayout;

/**
 * A simple extension of a regular linear layout that supports the divider API
 * of Android 4.0+. The dividers are added adjacent to the children by changing
 * their layout params. If you need to rely on the margins which fall in the
 * same orientation as the layout you should wrap the child in a simple
 * {@link android.widget.FrameLayout} so it can receive the margin.
 */
public class IcsLinearLayout extends NineLinearLayout {
    private static final int[] R_styleable_LinearLayout = new int[] {
        /* 0 */ android.R.attr.divider,
        /* 1 */ android.R.attr.measureWithLargestChild,
        /* 2 */ android.R.attr.showDividers,
        /* 3 */ android.R.attr.dividerPadding,
    };
    private static final int LinearLayout_divider = 0;
    private static final int LinearLayout_measureWithLargestChild = 1;
    private static final int LinearLayout_showDividers = 2;
    private static final int LinearLayout_dividerPadding = 3;

    /**
     * Don't show any dividers.
     */
    public static final int SHOW_DIVIDER_NONE = 0;
    /**
     * Show a divider at the beginning of the group.
     */
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    /**
     * Show dividers between each item in the group.
     */
    public static final int SHOW_DIVIDER_MIDDLE = 2;
    /**
     * Show a divider at the end of the group.
     */
    public static final int SHOW_DIVIDER_END = 4;


    private Drawable mDivider;
    private int mDividerWidth;
    private int mDividerHeight;
    private int mShowDividers;
    private int mDividerPadding;

    private boolean mUseLargestChild;

    public IcsLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, /*com.android.internal.R.styleable.*/R_styleable_LinearLayout);

        setDividerDrawable(a.getDrawable(/*com.android.internal.R.styleable.*/LinearLayout_divider));
        mShowDividers = a.getInt(/*com.android.internal.R.styleable.*/LinearLayout_showDividers, SHOW_DIVIDER_NONE);
        mDividerPadding = a.getDimensionPixelSize(/*com.android.internal.R.styleable.*/LinearLayout_dividerPadding, 0);
        mUseLargestChild = a.getBoolean(/*com.android.internal.R.styleable.*/LinearLayout_measureWithLargestChild, false);

        a.recycle();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if (getOrientation() == VERTICAL) {
            drawDividersVertical(canvas);
        } else {
            drawDividersHorizontal(canvas);
        }
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
            int parentHeightMeasureSpec, int heightUsed) {

        if (mDivider != null) {
            final int childIndex = indexOfChild(child);
            final int count = getChildCount();
            final LayoutParams params = (LayoutParams) child.getLayoutParams();

            // To display the dividers in-between the child views, we modify their margins
            // to create space.
            if (getOrientation() == VERTICAL) {
                if (hasDividerBeforeChildAt(childIndex)) {
                    params.topMargin = mDividerHeight;
                } else if (childIndex == count - 1 && hasDividerBeforeChildAt(count)) {
                    params.bottomMargin = mDividerHeight;
                }
            } else {
                if (hasDividerBeforeChildAt(childIndex)) {
                    params.leftMargin = mDividerWidth;
                } else if (childIndex == count - 1 && hasDividerBeforeChildAt(count)) {
                    params.rightMargin = mDividerWidth;
                }
            }
        }

        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed,
                parentHeightMeasureSpec, heightUsed);
    }

    void drawDividersVertical(Canvas canvas) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE && hasDividerBeforeChildAt(i)) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                drawHorizontalDivider(canvas, child.getTop() - lp.topMargin);
            }
        }

        if (hasDividerBeforeChildAt(count)) {
            final View child = getChildAt(count - 1);
            int bottom = 0;
            if (child == null) {
                bottom = getHeight() - getPaddingBottom() - mDividerHeight;
            } else {
                bottom = child.getBottom();
            }
            drawHorizontalDivider(canvas, bottom);
        }
    }

    void drawDividersHorizontal(Canvas canvas) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE && hasDividerBeforeChildAt(i)) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                drawVerticalDivider(canvas, child.getLeft() - lp.leftMargin);
            }
        }

        if (hasDividerBeforeChildAt(count)) {
            final View child = getChildAt(count - 1);
            int right = 0;
            if (child == null) {
                right = getWidth() - getPaddingRight() - mDividerWidth;
            } else {
                right = child.getRight();
            }
            drawVerticalDivider(canvas, right);
        }
    }

    void drawHorizontalDivider(Canvas canvas, int top) {
        mDivider.setBounds(getPaddingLeft() + mDividerPadding, top,
                getWidth() - getPaddingRight() - mDividerPadding, top + mDividerHeight);
        mDivider.draw(canvas);
    }

    void drawVerticalDivider(Canvas canvas, int left) {
        mDivider.setBounds(left, getPaddingTop() + mDividerPadding,
                left + mDividerWidth, getHeight() - getPaddingBottom() - mDividerPadding);
        mDivider.draw(canvas);
    }

    /**
     * Determines where to position dividers between children.
     *
     * @param childIndex Index of child to check for preceding divider
     * @return true if there should be a divider before the child at childIndex
     * @hide Pending API consideration. Currently only used internally by the system.
     */
    protected boolean hasDividerBeforeChildAt(int childIndex) {
        if (childIndex == 0) {
            return (mShowDividers & SHOW_DIVIDER_BEGINNING) != 0;
        } else if (childIndex == getChildCount()) {
            return (mShowDividers & SHOW_DIVIDER_END) != 0;
        } else if ((mShowDividers & SHOW_DIVIDER_MIDDLE) != 0) {
            boolean hasVisibleViewBefore = false;
            for (int i = childIndex - 1; i >= 0; i--) {
                if (getChildAt(i).getVisibility() != GONE) {
                    hasVisibleViewBefore = true;
                    break;
                }
            }
            return hasVisibleViewBefore;
        }
        return false;
    }
}
