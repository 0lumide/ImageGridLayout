package co.mide.textimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * This is an imageView that automatically generates and sets its bitmap based on the text.
 * It functions almost identically to http://placehold.it and http://dummyimage.com
 * Created by Olumide on 6/18/2016.
 */
@SuppressWarnings("unused")
public class TextImageView extends ImageView {
    private String text;
    private float textSize = -1;
    private Paint paint, backgroundPaint;

    public TextImageView(Context context){
        super(context);
        init();
    }

    public TextImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttributes(context, attrs);
    }

    public TextImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        initAttributes(context, attrs);
    }

    private void init() {
        //default colors
        int textColor = 0xff777777;
        int backgroundColor = 0xffcccccc;

        //Initialize font
        paint = new Paint(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        backgroundPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(textColor);
        backgroundPaint.setColor(backgroundColor);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @SuppressWarnings("all")
    private void initAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TextImageView,
                0, 0);

        try {
            setTextColor(a.getColor(R.styleable.TextImageView_textColor, getTextColor()));
            setImageBackgroundColor(a.getColor(R.styleable.TextImageView_backgroundColor, getImageBackgroundColor()));
            if(a.hasValue(R.styleable.TextImageView_text))
                setText(a.getString(R.styleable.TextImageView_text));
            textSize = a.getFloat(R.styleable.TextImageView_textSize, textSize);
        } finally {
            a.recycle();
        }
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public TextImageView setTextColor(int color){
        paint.setColor(color);
        invalidate();
        requestLayout();
        return this;
    }

    public int getTextColor(){
        return paint.getColor();
    }

    public TextImageView setImageBackgroundColor(int color){
        backgroundPaint.setColor(color);
        invalidate();
        requestLayout();
        return this;
    }

    public int getImageBackgroundColor(){
        return backgroundPaint.getColor();
    }

    public TextImageView setImageWidth(int width){
        if(width <= 1)
            throw new IllegalArgumentException("width has to be greater than 1");
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        setLayoutParams(params);
        return this;
    }

    public TextImageView setImageHeight(int height){
        if(height <= 1)
            throw new IllegalArgumentException("height has to be greater than 1");
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = height;
        setLayoutParams(params);
        return this;
    }

    @SuppressWarnings("all")
    public TextImageView setText(@NonNull String text){
        if(text == null)
            throw new IllegalArgumentException("text cannot be null");
        this.text = text;
        invalidate();
        requestLayout();
        return this;
    }

    public TextImageView setTextSize(float textSize){
        if(textSize <= 0)
            throw new IllegalArgumentException("textSize has to be greater than 0");
        this.textSize = textSize;
        invalidate();
        requestLayout();
        return this;
    }

//    @Override
//    public void setImageDrawable(Drawable drawable){
//        //Do nothing
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("all")
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        char multiplication = '×';

        String text = this.text;
        if(text == null)
            text = String.format("%d %c %d", getWidth(), multiplication, getHeight());
        if(textSize == -1) {
            float newTextSize = (float)Math.max(Math.min(getWidth()/text.length()*1.15, getHeight()*0.5) ,5);
            paint.setTextSize(newTextSize);
        }else{
            paint.setTextSize(textSize);
        }
        if(text == null){
            text = "" + getWidth() + multiplication + getHeight();
        }

        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawColor(backgroundPaint.getColor());
        if(isInEditMode()) {
            int top, sides;
            top = (int)(0.02222*canvas.getClipBounds().bottom);
            sides = (int)(0.2222*canvas.getClipBounds().bottom);
            canvas.drawRect(sides, canvas.getClipBounds().bottom/2 - top, canvas.getClipBounds().right - sides
                    , canvas.getClipBounds().bottom/2 + top, paint);
        }
        canvas.drawText(text, xPos, yPos, paint);
    }
}

