package com.yingnanwang.statuschecker.widget;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.yingnanwang.statuschecker.R;


public class BreathButton extends View {

    private static final String TAG = "BreathButton";

    private static final int RIPPLE_WIDTH_DIP_1 = 10;
    private static final int RIPPLE_WIDTH_DIP_2 = 10;
    private static final int ANIMATION_TIME = 2000;
    private static final int RIPPLE_ALPHA = 75;
    private static final int PRESSED_COLOR_CHANGE = 255 / 25;

    public static final int NOSTATUS_STATUS = 0;
    public static final int NORMAL_STATUS = 1;
    public static final int ERROR_STATUS = 2;

    private int status = 0;

    private int colorNormal;
    private int colorError;
    private int colorNoStatus;

    private Paint circlePaint;
    private Paint rectPaint;
    private Paint ripplePaint1;
    private Paint ripplePaint2;
    private Paint bitmapPaint;

    private int rippleWidth1;
    private int rippleWidth2;

    private int centerY;
    private int centerX;
    private float centerCircleRadius;
    private int widget_width;
    private int widget_height;
    private float maxReturnRipple1;
    private float maxReturnRipple2;
    private float maxShakeRipple2;

    private int outerRadius;
    private int rippleRadius1;
    private int rippleRadius2;

    private Drawable normal_drawable;
    private Drawable error_drawable;
    private Drawable nostatus_drawable;

    private Bitmap normal_img;
    private Bitmap error_img;
    private Bitmap nostatus_img;

    private float bitmapScaleFactor;
    private float bitmapDrawLeft;
    private float bitmapDrawTop;

    private int currentColor;

    private ObjectAnimator pressedAnimator;
    private float animationProgress;

    public BreathButton(Context context) {
        super(context);
        init(context, null);
    }

    public BreathButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BreathButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public float getAnimationProgress() {
        return animationProgress;
    }

    public void setAnimationProgress(float animationProgress) {
        this.animationProgress = animationProgress;
        this.invalidate();
    }

    public int getStatus() {
        return status;
    }

    public void noStatusAnim(){
        if(pressedAnimator.isStarted()){
            pressedAnimator.end();
        }

        status = NOSTATUS_STATUS;
        setColor(colorNoStatus);
        currentColor = colorNoStatus;
    }

    public void normalAnim(){
        if(pressedAnimator.isStarted()){
            pressedAnimator.end();
        }

        status = NORMAL_STATUS;
        setColor(colorNormal);
        currentColor = colorNormal;

        pressedAnimator.setFloatValues(0f, 10f);
        pressedAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        pressedAnimator.setRepeatMode(ObjectAnimator.INFINITE);
        pressedAnimator.start();
    }

    public void errorAnim(){
        if(pressedAnimator.isStarted()){
            pressedAnimator.end();
        }

        status = ERROR_STATUS;
        setColor(colorError);
        currentColor = colorError;

        pressedAnimator.setFloatValues(0f, 10f);
        pressedAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        pressedAnimator.setRepeatMode(ObjectAnimator.INFINITE);
        pressedAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        Log.d(TAG, "onMeasure: " + widthMeasureSpec + ", " + heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        Log.d(TAG, "onSizeChanged: "+w+", "+h);
        // call only once at beginning
        // w and h is for all (circle+ripple)
        super.onSizeChanged(w, h, oldw, oldh);
        widget_width = w;
        widget_height = h;
        centerX = w / 2;
        centerY = h / 2;
        outerRadius = Math.min(w, h) / 2;
        // init radius
        rippleRadius1 = outerRadius - rippleWidth2 - rippleWidth1 / 2;
        rippleRadius2 = rippleRadius1 + rippleWidth2 / 2;

        centerCircleRadius = outerRadius - rippleWidth1 - rippleWidth2;

        maxReturnRipple1 = rippleWidth1 / 2f;
        maxReturnRipple2 = rippleWidth1 / 2f + rippleWidth2 / 2f;
        maxShakeRipple2 = maxReturnRipple2 / 10f;

        bitmapScaleFactor = 1;

        normal_img = resizeDrawable(normal_drawable);
        error_img = resizeDrawable(error_drawable);
        nostatus_img = resizeDrawable(nostatus_drawable);
    }

    private float animationRipple1(float prog){

        if(prog < 5){
            return maxReturnRipple1 / 5f * prog;
        }else{
            return  - maxReturnRipple1 / 5f * prog + 2f * maxReturnRipple1;
        }
    }

    private float animationRipple2(float prog){
        if(status == NORMAL_STATUS){
            if(prog < 2){
                return 0;
            } else if(2 <= prog && prog < 6){
                return maxReturnRipple2 / 4f * prog - maxReturnRipple2 / 2f;
            }else if(6 <= prog && prog <= 10){
                return - maxReturnRipple2 / 4f * prog + 5f / 2f * maxReturnRipple2;
            }else{
                return 0;
            }
        }else if(status == ERROR_STATUS){
            if(prog < 3){
                return 0;
            }else if(3 <= prog && prog < 4){
                return maxShakeRipple2 * prog - 3f * maxShakeRipple2;
            }else if(4 <= prog && prog < 5){
                return - maxShakeRipple2 * prog + 5f * maxShakeRipple2;
            }else if(5 <= prog && prog < 6){
                return maxShakeRipple2 * prog - 5f * maxShakeRipple2;
            }else if(6 <= prog && prog < 7){
                return - maxShakeRipple2 * prog + 7f * maxShakeRipple2;
            }
            else{
                return 0;
            }
        }else{
            return 0;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, widget_width, widget_height, rectPaint);

        canvas.drawCircle(centerX, centerY, rippleRadius2 + animationRipple2(animationProgress), ripplePaint2);
        canvas.drawCircle(centerX, centerY, rippleRadius1 + animationRipple1(animationProgress), ripplePaint1);

        canvas.drawCircle(centerX, centerY, centerCircleRadius, circlePaint);

        if(status == NOSTATUS_STATUS){
            bitmapDrawLeft = (canvas.getWidth() - nostatus_img.getWidth()) >> 1;
            bitmapDrawTop = (canvas.getHeight() - nostatus_img.getHeight()) >> 1;
            canvas.drawBitmap(nostatus_img, bitmapDrawLeft, bitmapDrawTop, bitmapPaint);
        }else if(status == 1){
            bitmapDrawLeft = (canvas.getWidth() - normal_img.getWidth()) >> 1;
            bitmapDrawTop = (canvas.getHeight() - normal_img.getHeight()) >> 1;
            canvas.drawBitmap(normal_img, bitmapDrawLeft, bitmapDrawTop, bitmapPaint);
        }else if(status == 2){
            bitmapDrawLeft = (canvas.getWidth() - error_img.getWidth()) >> 1;
            bitmapDrawTop = (canvas.getHeight() - error_img.getHeight()) >> 1;
            canvas.drawBitmap(error_img, bitmapDrawLeft, bitmapDrawTop, bitmapPaint);
        }

        super.onDraw(canvas);
    }

    private Bitmap resizeDrawable(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        return Bitmap.createScaledBitmap(b, (int)(centerCircleRadius * bitmapScaleFactor), (int)(b.getHeight() * centerCircleRadius * bitmapScaleFactor / (float)b.getWidth()), false);
    }

    private void init(Context context, AttributeSet attrs) {
        this.setFocusable(true);
//        this.setScaleType(ScaleType.CENTER_INSIDE);
        setClickable(true);

        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setColor(Color.BLACK);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(1);

        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        ripplePaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        ripplePaint1.setStyle(Paint.Style.FILL);
        ripplePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        ripplePaint2.setStyle(Paint.Style.FILL);

        rippleWidth1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, RIPPLE_WIDTH_DIP_1,
                getResources().getDisplayMetrics());
        rippleWidth2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, RIPPLE_WIDTH_DIP_2,
                getResources().getDisplayMetrics());

        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RippleButton);

            colorNormal = a.getColor(R.styleable.RippleButton_rb_color_good, Color.BLUE);
            colorError = a.getColor(R.styleable.RippleButton_rb_color_bad, Color.RED);
            colorNoStatus = a.getColor(R.styleable.RippleButton_rb_color_no, Color.GRAY);

            rippleWidth1 = (int) a.getDimension(R.styleable.RippleButton_rb_ripple_width_1, rippleWidth1);
            rippleWidth2 = (int) a.getDimension(R.styleable.RippleButton_rb_ripple_width_2, rippleWidth2);

            normal_drawable = a.getDrawable(R.styleable.RippleButton_normal_src);
            error_drawable = a.getDrawable(R.styleable.RippleButton_error_src);
            nostatus_drawable = a.getDrawable(R.styleable.RippleButton_nostatus_src);

            a.recycle();
        }

        // init the animationProgress = 0, so the ripple stroke is not drawn
        pressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 0f);
        pressedAnimator.setDuration(ANIMATION_TIME);

        noStatusAnim();
    }

    private void setColor(int color) {

        circlePaint.setColor(color);
        ripplePaint1.setColor(color);
        ripplePaint1.setAlpha(RIPPLE_ALPHA);
        ripplePaint2.setColor(color);
        ripplePaint2.setAlpha((int) (RIPPLE_ALPHA * 1.5));

        this.invalidate();
    }

}
