package com.telerik.primitives;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.decorations.annotations.custom.CustomAnnotationRenderer;

/**
 * Created by George on 6/26/2016.
 */
public class CustomTextRenderer implements CustomAnnotationRenderer {
    Paint contentPaint = new Paint();

    public CustomTextRenderer() {
        contentPaint.setTextSize(14);
        contentPaint.setColor(Color.RED);
        contentPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
    }

    @Override
    public RadSize measureContent(Object content) {
        if (content == null) {
            return RadSize.getEmpty();
        }

        String text = content.toString();
        Rect textBounds = new Rect();
        contentPaint.getTextBounds(text, 0, text.length(), textBounds);

        return new RadSize(textBounds.width(), textBounds.height());
    }

    @Override
    public void render(Object content, RadRect layoutSlot, Canvas canvas, Paint paint) {
        if (content == null) {
            return;
        }

        String text = content.toString();
        canvas.drawText(
                text, (float) layoutSlot.getX() - (float) (layoutSlot.getWidth() / 2.0),
                (float) layoutSlot.getBottom() - (float) layoutSlot.getHeight() / 2, contentPaint);
    }
}