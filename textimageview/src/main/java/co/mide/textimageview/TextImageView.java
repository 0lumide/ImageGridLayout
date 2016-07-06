package co.mide.textimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

/**
 * This is an imageView that automatically generates and sets its bitmap based on the text.
 * It functions almost identically to http://placehold.it and http://dummyimage.com
 * Created by Olumide on 6/18/2016.
 */
@SuppressWarnings("unused")
public class TextImageView extends ForegroundImageView {
    private String text;
    private int textSize = -1;
    private Paint textPaint, backgroundPaint;

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
        textPaint = new Paint();//Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        backgroundPaint = new Paint();//Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setColor(textColor);
        backgroundPaint.setColor(backgroundColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @SuppressWarnings("all")
    private void initAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TextImageView,
                0, 0);

        try {
            textPaint.setColor(a.getColor(R.styleable.TextImageView_textColor, getTextColor()));
            backgroundPaint.setColor(a.getColor(R.styleable.TextImageView_backgroundColor, getImageBackgroundColor()));
            if(a.hasValue(R.styleable.TextImageView_text))
                text = a.getString(R.styleable.TextImageView_text);
            textSize = a.getDimensionPixelSize(R.styleable.TextImageView_textSize, textSize);
        } finally {
            a.recycle();
        }
    }

    /**
     * Set the color of the text. It defaults to black
     * @param color the color of the text
     * @return the TextImageView for method chaining
     */
    public TextImageView setTextColor(int color){
        textPaint.setColor(color);
        invalidate();
        return this;
    }

    /**
     * @return the text color
     */
    public int getTextColor(){
        return textPaint.getColor();
    }

    /**
     * Sets the background color of the image
     * @param color the color to set teh background to
     * @return the TextImageView for method chaining
     */
    public TextImageView setImageBackgroundColor(int color){
        backgroundPaint.setColor(color);
        invalidate();
        return this;
    }

    /**
     * @return the background color
     */
    public int getImageBackgroundColor(){
        return backgroundPaint.getColor();
    }

    /**
     * Sets the text of the image.
     * Currently does not support multi line text
     * @param text the text to be displayed in the image
     * @return the TextImageView for method chaining
     */
    @SuppressWarnings("all")
    public TextImageView setText(@NonNull String text){
        if(text == null)
            throw new IllegalArgumentException("text cannot be null");
        this.text = text;
        invalidate();
        return this;
    }

    /**
     * returns the text rendered on the image.
     * @return the text rendered on the image or null if none is set
     */
    public String getText(){
        return text;
    }

    /**
     * This sets the text size of the text in the imageView
     * @param textSize this is the size of the text in pixels
     * @return the TextImageView for method chaining
     */
    public TextImageView setTextSize(int textSize){
        if(textSize <= 0)
            throw new IllegalArgumentException("textSize has to be greater than 0");
        this.textSize = textSize;
        invalidate();
        return this;
    }

    /**
     * Sets the width of the image.
     * If the view has no LayoutParams set, it
     * creates a new LayoutParams with the width and height equal to width
     * @param width the width to set the image to
     * @return the TextImageView for method chaining
     */
    public TextImageView setImageWidth(int width){
        if(width <= 1)
            throw new IllegalArgumentException("width has to be greater than 1");
        ViewGroup.LayoutParams params = getLayoutParams();
        if(params == null) {
            int height = width;//because of lint
            params = new ViewGroup.LayoutParams(width, height);
        }
        params.width = width;
        setLayoutParams(params);
        return this;
    }

    /**
     * Sets the height of the image
     * If the view has no LayoutParams set, it
     * creates a new LayoutParams with the width and height equal to height
     * @param height the height to set the image to
     * @return the TextImageView for method chaining
     */
    public TextImageView setImageHeight(int height){
        if(height <= 1)
            throw new IllegalArgumentException("height has to be greater than 1");
        ViewGroup.LayoutParams params = getLayoutParams();
        if(params == null) {
            int width = height;
            params = new ViewGroup.LayoutParams(width, height);
        }
        params.height = height;
        setLayoutParams(params);
        return this;
    }

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
            int newTextSize = (int)Math.min(Math.max(Math.min(getWidth()/text.length()*1.15, getHeight()*0.5) ,5), 512);
            textPaint.setTextSize(newTextSize);
        }else{
            textPaint.setTextSize(textSize);
        }
        if(text == null){
            text = "" + getWidth() + multiplication + getHeight();
        }

        float[] widths = new float[text.length()];
        textPaint.getTextWidths(text, widths);

        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;

        canvas.drawColor(backgroundPaint.getColor());
        if(isInEditMode()) {
            int top, sides;
            top = (int)(0.02222*canvas.getClipBounds().bottom);
            sides = (int)(0.2222*canvas.getClipBounds().bottom);
            canvas.drawRect(sides, canvas.getClipBounds().bottom/2 - top, canvas.getClipBounds().right - sides
                    , canvas.getClipBounds().bottom/2 + top, textPaint);
        }
        canvas.drawText(text, xPos, yPos, textPaint);
    }
}