package co.mide.textimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
    private int textColor = 0x000000;
    private int backgroundColor = 0xcccccc;
    private String text;
    private float textSize = -1;
    private Paint paint;

    public TextImageView(Context context){
        super(context);
        init();
    }

    public TextImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TextImageView,
                0, 0);

        try {
            textColor = a.getColor(R.styleable.TextImageView_textColor, textColor);
            backgroundColor = a.getColor(R.styleable.TextImageView_textColor, backgroundColor);
            text = a.getString(R.styleable.TextImageView_text);
            textSize = a.getFloat(R.styleable.TextImageView_textSize, textSize);
        } finally {
            a.recycle();
        }
        init();
    }

    public void setTextColor(int color){
        textColor = color;
    }

    public void setBackgroundColor(int color){
        backgroundColor = color;
    }

    public void setImageWidth(int width){
        if(width <= 1)
            throw new IllegalArgumentException("width has to be greater than 1");
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        setLayoutParams(params);
    }

    public void setImageHeight(int height){
        if(height <= 1)
            throw new IllegalArgumentException("height has to be greater than 1");
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = height;
        setLayoutParams(params);
    }

    @SuppressWarnings("all")
    public void setText(@NonNull String text){
        if(text == null)
            throw new IllegalArgumentException("text cannot be null");
        this.text = text;
    }

    public void setTextSize(float textSize){
        if(textSize <= 0)
            throw new IllegalArgumentException("textSize has to be greater than 0");
        this.textSize = textSize;
    }

    @Override
    public void setImageDrawable(Drawable drawable){
        //Do nothing
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        paint.setColor(textColor);
        char multiplication = '×';

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

        canvas.drawColor(backgroundColor, PorterDuff.Mode.CLEAR);
        canvas.drawText(text, xPos, yPos, paint);
    }

    private void init(){
        //Initialize font
        paint = new Paint();
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/mplus-1c-medium.ttf");
        paint.setTypeface(font);
        paint.setTextAlign(Paint.Align.CENTER);
    }
}

