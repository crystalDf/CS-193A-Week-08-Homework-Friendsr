package com.star.friendsr;


import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector mGestureDetector;

    private float mSwipeDistanceThreshold = 50;
    private float mSwipeVelocityThreshold = 100;

    private float mPrevX;
    private float mPrevY;

    private float mDx;
    private float mDy;

    public OnSwipeTouchListener(Context context) {
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float dx = e2.getRawX() - e1.getRawX();
            float dy = e2.getRawY() - e1.getRawY();

            if ((Math.abs(dx) > Math.abs(dy)) &&
                    (Math.abs(dx) > mSwipeDistanceThreshold) &&
                    (Math.abs(velocityX) > mSwipeVelocityThreshold)) {
                if (dx > 0) {
                    onSwipeRight(dx);
                } else {
                    onSwipeLeft(-dx);
                }

                return true;
            } else if ((Math.abs(dy) > Math.abs(dx)) &&
                    (Math.abs(dy) > mSwipeDistanceThreshold) &&
                    (Math.abs(velocityY) > mSwipeVelocityThreshold)) {
                if (dy > 0) {
                    onSwipeDown(dy);
                } else {
                    onSwipeUp(-dy);
                }

                return true;
            }

            return false;
        }
    }

    public void onSwipeRight(float dx) {
    }

    public void onSwipeLeft(float dx) {
    }

    public void onSwipeDown(float dy) {
    }

    public void onSwipeUp(float dy) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        boolean gesture = false;

        if (v.getId() == R.id.detailsImage) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPrevX = event.getRawX();
                    mPrevY = event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float currX = event.getRawX();
                    float currY = event.getRawY();

                    mDx = currX - mPrevX;
                    mDy = currY - mPrevY;
                    break;

                case MotionEvent.ACTION_UP:
                    if ((Math.abs(mDx) > Math.abs(mDy)) &&
                            (Math.abs(mDx) > mSwipeDistanceThreshold)) {
                        if (mDx > 0) {
                            onSwipeRight(mDx);
                        } else {
                            onSwipeLeft(-mDx);
                        }

                        return true;
                    } else if ((Math.abs(mDy) > Math.abs(mDx)) &&
                            (Math.abs(mDy) > mSwipeDistanceThreshold)) {
                        if (mDy > 0) {
                            onSwipeDown(mDy);
                        } else {
                            onSwipeUp(-mDy);
                        }

                        return true;
                    }
            }

            gesture = mGestureDetector.onTouchEvent(event);
        }

        return gesture;
    }
}
