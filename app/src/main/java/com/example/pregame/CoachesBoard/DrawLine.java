package com.example.pregame.CoachesBoard;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawLine extends View {
    private Paint paint;
    private Path path;
    private boolean draw;

    public DrawLine(Context context, AttributeSet attrs, boolean draw) {
        super(context, attrs);
        this.draw = draw;
        init();
    }

    private void init() {
        paint = new Paint();
        path = new Path();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10f);
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        super.onDraw(canvas);

        if (draw) {
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xCord = event.getX();
        float yCord = event.getY();

        if (draw) {
            switch (event.getAction()) {
                case ACTION_DOWN:
                    path.moveTo(xCord, yCord);
                    return true;
                case ACTION_MOVE:
                    path.lineTo(xCord, yCord);
                    break;
                case ACTION_UP:
                    break;
                default:
                    return false;
            }
            invalidate();
            return true;
        }
        return false;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

}
