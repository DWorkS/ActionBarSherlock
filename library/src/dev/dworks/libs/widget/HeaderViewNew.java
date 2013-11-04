package dev.dworks.libs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

/**
 * A view to hold the section header and measure the header row height
 * correctly.
 * 
 * @author Tonic Artos
 */
public class HeaderViewNew extends TextView {
    private int mHeaderWidth = 1;

    public HeaderViewNew(Context context) {
        super(context);
    }	

    public HeaderViewNew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderViewNew(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public void setHeaderWidth(int width) {
        mHeaderWidth = width;
    }
    

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(mHeaderWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
    }
    
    @Override
    public void layout(int l, int t, int r, int b) {
    	super.layout(0, t, r, b);
    }
}