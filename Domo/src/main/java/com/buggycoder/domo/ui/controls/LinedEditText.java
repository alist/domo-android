package com.buggycoder.domo.ui.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

import com.buggycoder.domo.R;

/**
 * Created by shirish on 18/10/13.
 */
public class LinedEditText extends EditText {
    private Rect mRect;
    private Paint mPaint;

    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(context.getResources().getColor(R.color.domo_black_trans));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int height = getHeight();
        int line_height = getLineHeight();

        int count = height / line_height;

        if (getLineCount() > count) {
            count = getLineCount();
        }

        Rect r = mRect;
        Paint paint = mPaint;
        int baseline = getLineBounds(0, r) + 5;

        for (int i = 0; i < count; i++) {
            canvas.drawLine(r.left, baseline, r.right, baseline, paint);
            baseline += line_height;
        }

        super.onDraw(canvas);
    }
}
