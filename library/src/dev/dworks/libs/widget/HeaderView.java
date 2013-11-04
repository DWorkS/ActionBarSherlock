package dev.dworks.libs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class HeaderView extends FrameLayout {
    private int mHeaderWidth = 1;
	private View mView;
	private boolean set = false;

    public HeaderView(Context context) {
        super(context);
    }	

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public void setHeaderWidth(int width) {
        mHeaderWidth = width;
    }

    public void setView(View view) {
        mView = view;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	if(null == mView){
    		return;
    	}
        if (mView.getVisibility() != View.GONE) {
            mView.measure(MeasureSpec.makeMeasureSpec(mHeaderWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
        }
        setMeasuredDimension(mView.getMeasuredWidth(), mView.getMeasuredHeight());
    }

/*    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;
    	if(null == mView){
    		return;
    	}
        measureChildWithMargins(mView, widthMeasureSpec, 0, heightMeasureSpec, 0);
        final LayoutParams lp = (LayoutParams) mView.getLayoutParams();
        maxWidth = Math.max(maxWidth, mHeaderWidth + lp.leftMargin + lp.rightMargin);
        maxHeight = Math.max(maxHeight, mView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);

        childState = combineMeasuredStates(childState, mView.getMeasuredState());        int widthMeasureSpecNew = MeasureSpec.makeMeasureSpec(mHeaderWidth, MeasureSpec.getMode(widthMeasureSpec)); 
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpecNew, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
    }*/
    
/*    @Override
    protected void onLayout(boolean changed, int left, int top, int right,int bottom) {
    	if(!set){
    		set = true;
    		layout(mHeaderWidth == 0 ? left : 0, top, right, bottom);
    	}else{
    		set = false;
        	super.onLayout(changed, left, top, right, bottom);
    	}
    }*/
}