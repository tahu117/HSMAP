package com.godjin.hsmap;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import glm.mat4x4.Mat4;

public class MapView extends GLSurfaceView implements View.OnTouchListener {

    private float nDist;
    private float nLastX;
    private float nLastY;
    private float nLastSubX;
    private float nLastSubY;
    private float nCurrentScale = 1f;
    private int nPointerCount = 0;
    private MapRenderer sRenderer;
    private Mat4 sTransformMatrix = new Mat4(1f);

    public MapView(Context sContext, AttributeSet sAttributeSet) {
        super(sContext, sAttributeSet);

        this.sRenderer = new MapRenderer(sContext);
        this.setEGLContextClientVersion(3);
        this.setRenderer(this.sRenderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        this.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                this.nPointerCount = 0;
                this.nLastX = motionEvent.getX(0);
                this.nLastY = motionEvent.getY(0);
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                ++this.nPointerCount;

                Log.e("x", String.valueOf(this.nLastX));
                Log.e("y", String.valueOf(this.nLastY));
                Log.e("x2", String.valueOf(this.nLastSubX));
                Log.e("y2", String.valueOf(this.nLastSubY));

                if(this.nPointerCount == 1) {
                    this.nLastSubX = motionEvent.getX(1);
                    this.nLastSubY = motionEvent.getY(1);
                    this.nDist = this.calcDist();
                }

                break;
            }
            case MotionEvent.ACTION_MOVE: { // a pointer was moved

                if(this.nPointerCount == 0) {
                    float nDiffX = motionEvent.getX(0) - this.nLastX;
                    float nDiffY = motionEvent.getY(0) - this.nLastY;

                    nDiffX /= this.sRenderer.width() * .5f;
                    nDiffY /= this.sRenderer.height() * .5f;

                    this.nLastX = motionEvent.getX(0);
                    this.nLastY = motionEvent.getY(0);

                    this.sTransformMatrix = this.sTransformMatrix.translate(nDiffX, -nDiffY, 0f);
                    this.requestRender();
                    this.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            MapView.this.sRenderer.updateTransform(MapView.this.sTransformMatrix);
                        }
                    });
                } else {
                    int nPointerID = motionEvent.getPointerId(motionEvent.getActionIndex());

                    if(nPointerID > 1)
                        break;

                    if(nPointerID == 0) {

                        float nDiffX = motionEvent.getX(0) - this.nLastX;
                        float nDiffY = motionEvent.getY(0) - this.nLastY;

                        nDiffX /= this.sRenderer.width();
                        nDiffY /= this.sRenderer.height();

                        this.nLastX = motionEvent.getX(0);
                        this.nLastY = motionEvent.getY(0);

                        this.nCurrentScale = this.calcDist() / this.nDist / this.nCurrentScale;

                        //this.sTransformMatrix = this.sTransformMatrix.translate(nDiffX, -nDiffY, 0f);
                        this.sTransformMatrix = this.sTransformMatrix.scale(this.nCurrentScale, this.nCurrentScale, 1f);
                        this.requestRender();
                        this.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                MapView.this.sRenderer.updateTransform(MapView.this.sTransformMatrix);
                            }
                        });
                    } else {

                        float nDiffX = motionEvent.getX(1) - this.nLastSubX;
                        float nDiffY = motionEvent.getY(1) - this.nLastSubY;

                        nDiffX /= this.sRenderer.width();
                        nDiffY /= this.sRenderer.height();

                        this.nLastSubX = motionEvent.getX(1);
                        this.nLastSubY = motionEvent.getY(1);

                        this.nCurrentScale = this.calcDist() / this.nDist / this.nCurrentScale;

                        //this.sTransformMatrix = this.sTransformMatrix.translate(nDiffX, -nDiffY, 0f);
                        this.sTransformMatrix = this.sTransformMatrix.scale(this.nCurrentScale, this.nCurrentScale, 1f);
                        this.requestRender();
                        this.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                MapView.this.sRenderer.updateTransform(MapView.this.sTransformMatrix);
                            }
                        });
                    }
                }

                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                --this.nPointerCount;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

        return true;
    }

    private float calcDist() {
        return (float)Math.sqrt(Math.pow(this.nLastX - this.nLastSubX, 2f) + Math.pow(this.nLastY - this.nLastSubY, 2f));
    }
}
