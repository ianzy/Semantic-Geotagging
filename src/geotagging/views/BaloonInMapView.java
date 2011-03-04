package geotagging.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class BaloonInMapView extends LinearLayout {
	
	public BaloonInMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public BaloonInMapView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
    protected void dispatchDraw(Canvas canvas) {        
        Paint panelPaint  = new Paint();
        panelPaint.setARGB(0, 0, 0, 0);
        panelPaint.setAntiAlias(true);
        //Border
        Paint borderPaint = new Paint();
		borderPaint.setARGB(255, 255, 255, 255);//color for the border
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(4);
        
		float bottomForPanel = getMeasuredHeight()-20;
		float bottomForContent = 19*(getMeasuredHeight()/20)-20;
		
        RectF panelRect = new RectF();
        panelRect.set(0,0, getMeasuredWidth(), bottomForPanel);
        canvas.drawRoundRect(panelRect, 10, 10, panelPaint);
        //BaloonRect
        RectF baloonRect = new RectF();
        baloonRect.set(0,0, getMeasuredWidth(), bottomForContent);
        panelPaint.setARGB(100, 0, 0, 0);//color for background
        canvas.drawRoundRect(baloonRect, 20, 20, panelPaint);
        canvas.drawRoundRect(baloonRect, 20, 20, borderPaint);
        //Arrow below
        Path baloonTip = new Path();
        baloonTip.moveTo(39*(getMeasuredWidth()/100), bottomForContent);
        baloonTip.lineTo(getMeasuredWidth()/2, getMeasuredHeight()-20);
        baloonTip.lineTo(61*(getMeasuredWidth()/100), bottomForContent);
        
        canvas.drawPath(baloonTip, panelPaint);
                
        super.dispatchDraw(canvas);
    }
}
