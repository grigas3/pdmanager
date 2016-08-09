package com.telerik.primitives;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;

import com.pdmanager.core.R;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.decorations.annotations.custom.CustomAnnotationRenderer;

import java.lang.ref.WeakReference;

public abstract class PDAnnotationRenderer implements CustomAnnotationRenderer {
    Paint contentPaint = new Paint();
    LruCache<String, Bitmap> mMemoryCache;
    int size;

    public PDAnnotationRenderer(LruCache<String, Bitmap> mm, int size) {
        mMemoryCache = mm;
        contentPaint.setTextSize(36);
        this.size = size;
        contentPaint.setColor(Color.RED);
        contentPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
    }


    @Override
    public RadSize measureContent(Object content) {
        if (content == null) {
            return RadSize.getEmpty();
        }

        //String Value = content.toString();
        //Rect textBounds = new Rect();
        //contentPaint.getTextBounds(Value, 0, Value.length(), textBounds);

        return new RadSize(size, size);//textBounds.width(), textBounds.height());
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private void loadBitmap(int resId, Canvas canvas, RadRect rect, Paint paint) {

        final String imageKey = String.valueOf(resId);

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {

            canvas.drawBitmap(bitmap, (int) rect.getX(), (int) rect.getY(), paint);
            //imageView.setImageBitmap(bitmap);
        } else {

            BitmapWorkerTask task = new BitmapWorkerTask(canvas, rect, paint);
            task.execute(resId);
        }
    }

    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    protected abstract Resources getFragmentResources();

    @Override
    public void render(Object content, RadRect layoutSlot, Canvas canvas, Paint paint) {
        if (content == null) {
            return;
        }

        String text = content.toString();
        //String id=String.valueOf(R.drawable.person2);
        Drawable d = getFragmentResources().getDrawable(R.drawable.pill);
        if (d != null) {
            try {
                d.setBounds((int) (layoutSlot.getX() - (float) (layoutSlot.getWidth() / 2.0)), (int) (layoutSlot.getY() - (float) layoutSlot.getHeight() / 2.0), (int) layoutSlot.getRight(), (int) layoutSlot.getBottom());
                d.draw(canvas);
            } catch (Exception ex) {
            }
        }
        //loadBitmap(R.drawable.person2,canvas,layoutSlot,paint);
        //canvas.drawBitmap();
        //canvas.drawText(
        //      Value, (float) layoutSlot.getX() - (float) (layoutSlot.getWidth() / 2.0),
        //    (float) layoutSlot.getBottom() - (float)layoutSlot.getHeight() / 2, contentPaint);
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<Canvas> imageViewReference;
        private int data = 0;
        private int width;
        private int height;

        private int x;
        private int y;
        private Resources resources;
        private Paint paint;

        public BitmapWorkerTask(Canvas canvas, RadRect rect, Paint paint) {
            imageViewReference = new WeakReference<>(canvas);
            resources = getFragmentResources();
            this.width = (int) rect.getWidth();
            this.height = (int) rect.getHeight();
            this.paint = paint;
            this.x = (int) rect.getX();
            this.y = (int) rect.getY();
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            Bitmap bitmap = decodeSampledBitmapFromResource(resources, data, width, height);
            addBitmapToMemoryCache(String.valueOf(data), bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                final Canvas imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.drawBitmap(bitmap, x, y, paint);
                    //imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}

