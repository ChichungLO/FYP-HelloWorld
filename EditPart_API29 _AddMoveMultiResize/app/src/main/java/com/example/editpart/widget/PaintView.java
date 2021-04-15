package com.example.editpart.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.editpart.R;

import java.util.ArrayList;

public class PaintView extends View {

    private Bitmap btmBackground,btmView,image,captureImage,originalImage;
    private Paint mPaint = new Paint();
    private Path mPath = new Path();
    private int colorBackground,sizeBrush,sizeEraser;
    private float mX,mY;
    private Canvas mCanvas;
    private final int DEFFERENCE_SPACE = 4;
    private ArrayList<Bitmap> listAction = new ArrayList<>();
    private int leftImage = 0, topImage = 0;
    public static boolean toMove = false;
    private boolean toResize = false;
    private float refX,refY;
    private int xCenter,yCenter;


    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();

    }

    private void init() {

        sizeEraser = sizeBrush = 12;
        colorBackground = Color.WHITE;

        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(toPx(sizeBrush));

        Drawable vectorDrawable;
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            vectorDrawable = getResources().getDrawable(R.drawable.ic_baseline_photo_camera_back_24,null);
        }else {
            vectorDrawable = getResources().getDrawable(R.drawable.ic_baseline_photo_camera_back_24);
        }

        // Wrap the drawable so that future tinting calls work
        // on pre-v21 devices. Always use the returned drawable.
        Drawable wrapDrawable = DrawableCompat.wrap(vectorDrawable);
        //DrawableCompat.setTint(wrapDrawable.mutute(), getResources().getColor(color));

        int h = vectorDrawable.getIntrinsicHeight();
        int w = vectorDrawable.getIntrinsicWidth();
        //Setting a pixel default if intrinsic height or width is not found , eg a shape drawable
        h=h>0?h:96;
        w=w>0?w:96;

        wrapDrawable.setBounds(0, 0, w, h);
        captureImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        //captureImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_photo_camera_back_24);

        Log.e("captureImage is null?",(captureImage==null) +"");
    }

    private float toPx(int sizeBrush) {
        return sizeBrush*(getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        btmBackground = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        btmView = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(btmView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(colorBackground);
        canvas.drawBitmap(btmBackground, 0, 0,null);

        if(image != null && toMove) {
            canvas.drawBitmap(image, leftImage, topImage, null);
            xCenter = leftImage + image.getWidth()/2 - captureImage.getWidth()/2;
            yCenter = topImage + image.getHeight()/2 - captureImage.getHeight()/2;
            canvas.drawBitmap(captureImage,xCenter,yCenter,null);
        }

        canvas.drawBitmap(btmView,0,0,null);
    }

    public void setColorBackground(int color){
        colorBackground = color;
        invalidate();
    }

    public void setSizeBrush(int s){
        sizeBrush = s;
        mPaint.setStrokeWidth(toPx(sizeBrush));
    }

    public void setBrushColor(int color){
        mPaint.setColor(color);
    }

    public void setSizeEraser(int s){
        sizeEraser = s;
        mPaint.setStrokeWidth(toPx(sizeEraser));
    }

    public void enableEraser(){
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void desableEraser(){
        mPaint.setXfermode(null);
        mPaint.setShader(null);
        mPaint.setMaskFilter(null);
    }

    public void addLastAction(Bitmap bitmap){
        listAction.add(bitmap);
    }

    public void returnLastAction(){

        if(listAction.size() > 0){

            listAction.remove(listAction.size() - 1);

            if(listAction.size() > 0){

                btmView = listAction.get(listAction.size() - 1);

            }else {
                btmView = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
            }

            mCanvas = new Canvas(btmView);

            invalidate();

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                touchStart(x,y);
                refY = y;
                refX = x;

                if(toMove){

                    if(isToResize(refX,refY)){
                        toResize = true;
                    }else {
                        toResize = false;
                    }

                    if((refX >= xCenter && refX < xCenter + captureImage.getWidth())
                        &&(refY >= yCenter && refY < yCenter + captureImage.getHeight())) {

                            Canvas newCanvas = new Canvas(btmBackground);
                            newCanvas.drawBitmap(image, leftImage, topImage, null);
                            invalidate();
                    }

                }

                break;
            case MotionEvent.ACTION_MOVE:
                if(!toMove)
                    touchMove(x,y);
                else{
                    float nX = event.getX();
                    float nY = event.getY();

                    if(toResize){

                        int xScale = 0;
                        int yScale = 0;

                        if(nX > refX){
                            xScale = (int) (image.getWidth() + (nX - refX));
                        }else{
                            xScale = (int) (image.getWidth() - (refX - nX));
                        }

                        if(nY > refY){
                            yScale = (int) (image.getHeight() + (nY - refY));
                        }else {
                            yScale = (int) (image.getHeight() - (refY - nY));
                        }

                        if(xScale > 0 && yScale > 0)
                            image = Bitmap.createScaledBitmap(originalImage,xScale,yScale,false);
                    }else {

                        leftImage += nX - refX;
                        topImage += nY - refY;
                    }

                    refX = nX;
                    refY = nY;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:

                if(!toMove) {
                    touchUp();
                    addLastAction(getBitmap());
                }
                break;

        }


        return true;
    }

    private boolean isToResize(float refX, float refY) {

        if((refX >= leftImage && refX < leftImage + image.getWidth()
            &&((refY >= topImage && refY <= topImage + 20) || (refY >= topImage + image.getHeight() - 20 && refY <= topImage + image.getHeight())))){
                return true;
        }

        return false;
    }

    private void touchUp() {
        mPath.reset();
    }

    private void touchMove(float x, float y) {

        float dx = Math.abs(x-mX);
        float dy = Math.abs(y-mY);

        if(dx >= DEFFERENCE_SPACE || dy >= DEFFERENCE_SPACE){

            mPath.quadTo(x,y,(x+mX)/2,(y+mY)/2);

            mY = y;
            mX = x;

            mCanvas.drawPath(mPath,mPaint);
            invalidate();

        }

    }

    private void touchStart(float x, float y) {
        mPath.moveTo(x,y);
        mX = x;
        mY = y;
    }

    public Bitmap getBitmap(){

        this.setDrawingCacheEnabled(true);

        this.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(this.getDrawingCache());

        this.setDrawingCacheEnabled(false);

        return bitmap;

    }

    public void setImage(Bitmap bitmap) {
        toMove = true;
        Log.e("paint_height", getHeight()+"");
        Log.e("paint_width", getWidth()+"");
        image = Bitmap.createScaledBitmap(bitmap,getWidth()/2,getHeight()/2,true);
        originalImage = image;
        invalidate();
    }

    public void setImage(Bitmap bitmap, int height, int width) {
        toMove = true;
        image = Bitmap.createScaledBitmap(bitmap,width,height,true);
        originalImage = image;
        invalidate();
    }

}
