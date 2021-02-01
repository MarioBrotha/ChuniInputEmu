package com.example.chunithmbuttons;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toolbar;

import androidx.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PianoView extends View {

    //need to take out black keys
    public static final int NB = 16;
    private final Paint black;
    private final Paint yellow;
    private final Paint white;
    private final ArrayList<Key> whites = new ArrayList<>();
    private final ArrayList<Key> blacks = new ArrayList<>();
    private int keyWidth, height;
    private final ButtonSender buttonSender;

    public PianoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        black = new Paint();
        black.setColor(Color.BLACK);
        white = new Paint();
        white.setColor(Color.WHITE);
        white.setStyle(Paint.Style.FILL);
        yellow = new Paint();
        yellow.setColor(Color.YELLOW);
        yellow.setStyle(Paint.Style.FILL);

        buttonSender = new ButtonSender();
        buttonSender.SetPreferencesAndStart();
        hideSystemUI();

    }




    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        keyWidth = w / NB;
        height = h;
        int count = 15;

        for (int i = 0; i < NB; i++) {
            int left = i * keyWidth;
            int right = left + keyWidth;

            if (i == NB - 1) {
                right = w;
            }

            RectF rect = new RectF(left, 0, right, h);
            whites.add(new Key(rect, i + 1));

            /*if (i == 14) {
                rect = new RectF((float) (0) , 0,
                        (float) 8f * keyWidth, 0.25f * height);
                blacks.add(new Key(rect, i + 1));
            }

            if (i == 15) {
                rect = new RectF((float) 8f * keyWidth , 0,
                        (float) 17f * keyWidth, 0.25f * height);
                blacks.add(new Key(rect, i + 1));
            }*/

        }

        /*RectF rect = new RectF(0,0,keyWidth * 8, 0.25f * height);
        blacks.add(new Key(rect, 17));

        rect = new RectF(8*keyWidth,0,keyWidth * 17, 0.25f * height);
        blacks.add(new  Key(rect, 18));*/

    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Key k : whites) {
            canvas.drawRect(k.rect, k.down ? yellow : white);
        }

        for (int i = 1; i < NB; i++) {
            canvas.drawLine(i * keyWidth, 0, i * keyWidth, height, black);
        }

        for (Key k : blacks) {
            canvas.drawRect(k.rect, k.down ? yellow : black);
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int actionMasked = event.getActionMasked();

        int pointerIndex = ((event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);

        //if (isDownAction)
        //{
            // Set key as pressed/down
            for (int touchIndex = 0; touchIndex < event.getPointerCount(); touchIndex++) {

                if (touchIndex == pointerIndex  && (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_POINTER_UP)) {
                    // the pointer with index 0 is no longer on screen,
                    // so the circle is not pressed by this pointer, even if
                    // it's coordinates are over the area of the circle
                    continue;
                }

                float x = event.getX(touchIndex);
                float y = event.getY(touchIndex);

                Key k = keyForCoords(x, y);
                if (k != null) {
                    if (!k.down) {
                        k.down = true;
                        /*if(event.getPointerCount() == 1)
                            break;*/
                    }
                }
            }
        //}

        for (Key k : whites) {
            if (k.down) {
                if (!buttonSender.isNotePressed(k.sound)) {
                    buttonSender.playNote(k.sound);
                    invalidate();
                } else {
                    releaseKey(k);
                }
            } else {
                if (buttonSender.isNotePressed(k.sound)) {
                    buttonSender.stopNote(k.sound);
                    releaseKey(k);
                }
            }
        }

        return true;
    }

    protected boolean checkPressed(@NotNull MotionEvent event)
    {
        int actionMasked = event.getActionMasked();
        int pointerIndex = ((event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);

        for (int i = 0; i < event.getPointerCount(); i++)
        {
            if (i == pointerIndex  && (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_POINTER_UP)) {
                // the pointer with index 0 is no longer on screen,
                // so the circle is not pressed by this pointer, even if
                // it's coordinates are over the area of the circle
                continue;
            }

            if (keyForCoords(event.getX(), event.getY()) != null)
            {
                return true;
            }
        }
        return false;
    }




    private Key keyForCoords(float x, float y) {
        for (Key k : blacks) {
            if (k.rect.contains(x,y)) {
                return k;
            }
        }

        for (Key k : whites) {
            if (k.rect.contains(x,y)) {
                return k;
            }
        }

        return null;
    }

    private void releaseKey(final Key k) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                k.down = false;
                handler.sendEmptyMessage(0);
            }
        }, 0);
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            invalidate();
        }
    };

    // This snippet hides the system bars.
    public void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        this.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        //| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}