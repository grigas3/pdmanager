package com.pdmanager.views.patient.cognition.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class CustomSeekBar extends SeekBar
{

    private Paint paint;

    public CustomSeekBar(final Context ctx) { super(ctx); init(); }
    public CustomSeekBar(final Context ctx, final AttributeSet attrs) { super(ctx, attrs); init(); }
    public CustomSeekBar(final Context ctx, final AttributeSet attrs, final int defStyle) { super(ctx, attrs, defStyle); init(); }

    private void init()
    {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xffffffff);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        int
                dots = 5,
                regions = dots-1,
                steps = getMax()/regions,
                h = getMeasuredHeight()/2,
                radius = getThumb().getIntrinsicWidth()/2;
        int
                pLeft = Math.max(getPaddingLeft(), getPaddingStart()),
                pRight = Math.max(getPaddingRight(), getPaddingEnd());
        int available = getWidth() - pLeft - pRight;
        available -= getThumb().getIntrinsicWidth();
        available += 2*getThumbOffset();

        for (int i=0; i<dots; i++)
        {
            int dotProgress = i*steps;
            float scale = (float)dotProgress/(float)getMax();
            int dotPos = (int) (scale * available + radius + getThumbOffset() + 0.5f);
            canvas.drawCircle(dotPos, h, radius, paint);
        }
        super.onDraw(canvas);
    }
}
