package com.nickole.earworld.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * @author shuzijie
 * @date 2019-05-15.
 */
public class CornerImageView extends AppCompatImageView {

    //圆角弧度
    private float[] rids = {25.0f, 25.0f, 25.0f, 25.0f, 0.0f, 0.0f, 0.0f, 0.0f,};

    public CornerImageView(Context context) {
        super(context);
    }

    public CornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        //绘制圆角imageview
        path.addRoundRect(new RectF(0, 0, w, h), rids, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
