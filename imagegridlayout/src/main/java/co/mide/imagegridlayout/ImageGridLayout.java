package co.mide.imagegridlayout;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.LinkedList;

import co.mide.textimageview.TextImageView;

/**
 * This is a GridLayout designed specifically for images.
 * It automatically arranges the images in the layout.
 * Created by Olumide on 6/14/2016.
 */
@SuppressWarnings("unused")
public class ImageGridLayout extends GridLayout {
    private LinkedList<GridPosition> queue;
    private HashMap<Integer, GridPosition> queuePosition;
    private int MARGIN = (int)convertDpToPixel(1, getContext());
    private OnMoreClicked onMoreClickedCallback;
    private OnMoreLongClicked onMoreLongClickedCallback;
    private int moreColor = 0xff111111;
    private int moreTextColor = 0xffffffff;
    private int maxImages = 11;//Integer.MAX_VALUE;
    //This coincidentally also happens to be the same size as the smallest GridPosition
    private GridPosition lowerRightCorner;
    private TextImageView overflowView;
    private int extraImages = 0;
    private boolean viewRemovedFlag = false;

    public ImageGridLayout(Context context){
        super(context);
        init();
    }

    public ImageGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
        init();
    }

    public ImageGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributes(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageGridLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ImageGridLayout,
                0, 0);

        try {
            setMoreImagesColor(a.getColor(R.styleable.ImageGridLayout_moreColor, moreColor));
            maxImages = a.getInt(R.styleable.ImageGridLayout_maxImageCount, maxImages);
        } finally {
            a.recycle();
        }
    }

    private void init(){
        queue = new LinkedList<>();
        queuePosition = new HashMap<>();
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This sets the maximum number of images to be contained in the layout.
     * If count is less than the current number of images in the layout,
     * a number of images that equals the difference are discarded
     * @param count the maximum number of images the layout will contain
     */
    public void setMaxImagesCount(int count){
        maxImages = count;
        if(getImageCount() > maxImages){
            removeExtraViews();
            updateLayoutRepresentation(getWidth(), getHeight(), maxImages+1, true);
            updateViews();
            invalidate();
            requestLayout();
        }else if(getImageCount() < maxImages){
            if(overflowView != null && overflowView.getParent() == this) {
                updateLayoutRepresentation(getWidth(), getHeight(), getChildCount() - 1, true);
                removeView(overflowView);
                overflowView = null; //hopefully a temporary solution
                extraImages = 0;
            }
        }
    }

    /**
     * This retrieves the maximum number of images that the layout can contain.
     * There is no limit by default.
     * @return the maximum number of images the layout will accept
     */
    public int getMaxImagesCount(){
        return maxImages;
    }

    /**
     * Sets the background color of the more images image
     * @param color the color to set the "more images" image to.
     */
    public void setMoreImagesColor(int color){
        moreColor = color;
        double whiteContrast = ColorUtils.calculateContrast(Color.WHITE, moreColor);
        double blackContrast = ColorUtils.calculateContrast(Color.BLACK, moreColor);
        if(whiteContrast >= blackContrast){
            moreTextColor = Color.WHITE;
        }else{
            moreTextColor = Color.BLACK;
        }
        if(overflowView != null)
            overflowView.setTextColor(moreTextColor).setImageBackgroundColor(moreColor);
    }

    /**
     * This method returns the color of the more images image
     * @return the color of the more images image
     */
    public int getMoreImagesColor(){
        return moreColor;
    }

    /**
     * This sets the callback that is called when the user touches the more images image.
     * If callback is null, the callback is removed.
     * @param callback this is the callback that is called.
     */
    public void setOnMoreClickedCallback(OnMoreClicked callback){
        this.onMoreClickedCallback = callback;
        if(callback == null && overflowView != null)
            overflowView.setClickable(false);
    }

    /**
     * This sets the callback that is called when the user touches the more images image.
     * If callback is null, the callback is removed.
     * @param callback this is the callback that is called.
     */
    public void setOnMoreLongClickedCallback(OnMoreLongClicked callback){
        this.onMoreLongClickedCallback = callback;
        if(callback == null && overflowView != null)
            overflowView.setClickable(false);
    }

    private void updateViews(){
        if(queuePosition.size() != getChildCount())
            return;
        //update all the children. Since the number of columns could have changed
        int offset = 0;
        for(int i = 0; i < queuePosition.size(); i++){
            View child = getChildAt(i);
            GridPosition gridPosition;
            if (child == overflowView) {
                gridPosition = lowerRightCorner;
                if(queuePosition.get(i + offset) == null ||
                        queuePosition.get(i + offset).getIndex() != lowerRightCorner.getIndex()) {
                    offset -= 1;
                }
            }else {
                gridPosition = queuePosition.get(i + offset);
                if (overflowView != null && overflowView.getParent() == this) {
                    if (lowerRightCorner.getIndex() == gridPosition.getIndex()) {
                        offset += 1;
                        gridPosition = queuePosition.get(i + offset);
                    }
                }
            }
            LayoutParams params = (LayoutParams)child.getLayoutParams();
            layoutParamsFromGridPosition(gridPosition, params);
            child.setLayoutParams(params);
            if(child instanceof ImageView)
                ((ImageView)child).setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    /**
     * @return the number of images displayed in the overflow view
     */
    public int getExtraImagesCount(){
        return extraImages;
    }

    /**
     * This method manually sets the number that is shown in the overflow view.
     * Note that if this method is called before the number of images in the layout is less than the
     * limit, the limit is automatically set to the current image count.
     * @param num the number to show i  the overflow view.
     */
    public void setExtraImagesCount(int num) {
        if (extraImages < 0)
            throw new IllegalArgumentException("num cannot be a negative number");
        if (getImageCount() < getMaxImagesCount())
            setMaxImagesCount(getImageCount());
        extraImages = num;

        if (num == 0 && overflowView != null){
            removeViewInLayout(overflowView);
            overflowView = null;
            updateLayoutRepresentation(getWidth(), getHeight(), getChildCount(), true);
            updateViews();
        }else {
            handleOverflow();
        }
    }

    private void removeExtraViews(){
        viewRemovedFlag = true;
        while(getImageCount() > maxImages) {
            int removeIndex = getChildAt(getChildCount() - 1) != overflowView ? getChildCount() - 1 : getChildCount() - 2;
            extraImages++;
            removeViewAt(removeIndex);
        }
        viewRemovedFlag = false;
    }

    private void updateLayoutRepresentation(int width, int height, int newSize, boolean reset){
        int start;
        if(reset || queue.size() >= newSize){
            queue = new LinkedList<>();
            queuePosition.clear();
            start = 0;
        }else{
            start = queue.size();
        }

        for(int i = start; i < newSize; i++) {
            if(i == 0){
                GridPosition gridPosition = new GridPosition(width, height);
                gridPosition.setIndex(i);
                queuePosition.put(gridPosition.getIndex(), gridPosition);
                queue.add(gridPosition);
                lowerRightCorner = gridPosition;
            }else{
                GridPosition gridPosition = queue.poll();
                GridPosition newGridPosition = gridPosition.splitPosition();
                newGridPosition.setIndex(i);
                queue.add(newGridPosition);
                queue.add(gridPosition);
                queuePosition.put(newGridPosition.getIndex(), newGridPosition);
                if(newGridPosition.getPositionX() >= lowerRightCorner.getPositionX()
                        && newGridPosition.getPositionY() >= lowerRightCorner.getPositionY()){
                    lowerRightCorner = newGridPosition;
                }
            }
        }

        //Check one step into the future for lowerRightCorner
        if(queue.size() > 0 && queue.size() <= maxImages) {
            GridPosition cloned = queue.getFirst().clone();
            GridPosition potentialCorner = cloned.splitPosition();
            potentialCorner.setIndex(queue.size());
            if (potentialCorner.getPositionX() >= lowerRightCorner.getPositionX()
                    && potentialCorner.getPositionY() >= lowerRightCorner.getPositionY()) {
                lowerRightCorner = potentialCorner;
            }
        }
    }

    private void layoutParamsFromGridPosition(GridPosition gridPosition, LayoutParams params){
        int columnCount = getNewColumnCount();

        params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
        params.height = gridPosition.getHeight() - params.topMargin - params.bottomMargin;
        params.width = gridPosition.getWidth() - params.leftMargin - params.rightMargin;
        params.columnSpec = GridLayout.spec((int)(gridPosition.getPositionX()*columnCount),
                columnCount/gridPosition.getInverseWidth());
        params.rowSpec = GridLayout.spec((int)(gridPosition.getPositionY()*columnCount),
                columnCount/gridPosition.getInverseHeight());
    }

    private int getNewColumnCount(){
        if(!queue.isEmpty())
            return queue.getLast().getInverseWidth() > queue.getLast().getInverseHeight() ?
                queue.getLast().getInverseWidth() : queue.getLast().getInverseHeight();
        return 1;
    }

    /**
     * This is equivalent to addView(ImageView, index)
     * @param child the ImageView to be inserted into the layout
     * @param index the position where the ImageView should be inserted
     * @param ignoredParams this value is ignored
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams ignoredParams){
        if(index == -1)
            index = getImageCount();
        if(index >= 0 && index <= getImageCount()) {
            addView1(child, index);
        }else{
            throw new IndexOutOfBoundsException("index "+index+" cannot be less than -1 or more than size "+getImageCount());
        }
    }

    /**
     * Add view to layout without index checking
     */
    private void addView1(View child, int index){
        if(getChildCount() <= getMaxImagesCount()) {
            updateLayoutRepresentation(getWidth(), getHeight(), getChildCount() + 1, false);
        }

        //set the new view parameters
        if (child instanceof ImageView)
            ((ImageView) child).setScaleType(ImageView.ScaleType.CENTER_CROP);
        GridPosition gridPosition = queuePosition.get(index);
        LayoutParams params = new LayoutParams();
        layoutParamsFromGridPosition(gridPosition, params);

        //Set the column count
        int newColumnCount = getNewColumnCount();

        if (getColumnCount() < newColumnCount) {
            setColumnCount(newColumnCount);
            super.addView(child, index, params);
            updateViews();
        } else {
            if(index == getImageCount() && index != getChildCount()){
                super.addView(child, -1, params);
            }else {
                super.addView(child, index, params);
            }
            updateViews();
            setColumnCount(newColumnCount);
        }

        handleOverflow();
    }

    private void handleOverflow(){
        //If max images reached
        if(getImageCount() >= getMaxImagesCount()){
            if(overflowView == null) {
                overflowView = new TextImageView(getContext());
            }
            removeExtraViews();
            updateLayoutRepresentation(getWidth(), getHeight(), getChildCount(), true);
        }

        //setup overflow view
        if(extraImages > 0){
            overflowView.setText(getResources().getString(R.string.more_images, extraImages));
            int color = ColorUtils.blendARGB(moreTextColor, moreColor, 0.4f);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                RippleDrawable rippledImage = new RippleDrawable(
                        ColorStateList.valueOf(color), null, null);
                overflowView.setForeground(rippledImage);
            }
            overflowView.setTextColor(moreTextColor).setImageBackgroundColor(moreColor);

            if (onMoreClickedCallback != null) {
                overflowView.setClickable(true);
                overflowView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onMoreClickedCallback != null)
                            onMoreClickedCallback.onMoreClicked(ImageGridLayout.this);
                    }
                });
            }

            if (onMoreLongClickedCallback != null) {
                overflowView.setClickable(true);
                overflowView.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return onMoreLongClickedCallback != null &&
                                onMoreLongClickedCallback.onMoreLongClicked(ImageGridLayout.this);
                    }
                });
            }
            if(overflowView.getParent() != this) {
                addView1(overflowView, lowerRightCorner.getIndex());
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout){
        if((index) <= getImageCount()){
            return super.addViewInLayout(child, index, params, preventRequestLayout);
        }else{
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        updateLayoutRepresentation(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec), getChildCount(), true);

        int newColumnCount = getNewColumnCount();

        if (getColumnCount() < newColumnCount) {
            setColumnCount(newColumnCount);
            updateViews();
        } else{
            updateViews();
            setColumnCount(newColumnCount);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        if (view != overflowView){
            if (extraImages < 0 || getImageCount() < getMaxImagesCount()) {
                extraImages = 0;
            }if (overflowView != null && overflowView.getParent() == this && !viewRemovedFlag) {
                removeView(overflowView);
                overflowView = null;
                extraImages = 0;
            }
        }
    }

    /**
     * This method returns the number of images the layout contains.
     * Note that this method is different from getChildCount() as this method
     * doesn't include the more images in the count
     * @return the number of images in the layout
     */
    public int getImageCount(){
        if(overflowView == null || overflowView.getParent() != this)
            return getChildCount();
        else{// if(overflowView != null && overflowView.getParent() == this){
            return getChildCount() - 1;
        }
    }

    public interface OnMoreClicked{
        void onMoreClicked(ImageGridLayout layout);
    }

    public interface OnMoreLongClicked{
        boolean onMoreLongClicked(ImageGridLayout layout);
    }

    /**
     * object that holds information on how to size and position image
     * Created by Olumide on 6/14/2016.
     */
    class GridPosition implements Cloneable{
        private int layoutWidth, layoutHeight;
        private int inverseWidth = 1;
        private int inverseHeight = 1;
        private PointF position;
        private int index;

        /**
         * Constructor for GridPosition
         * @param layoutWidth this is the layoutWidth of the GridLayout
         * @param layoutHeight this is the layoutHeight of the GridLayout
         */
        public GridPosition(int layoutWidth, int layoutHeight){
            this.layoutWidth = layoutWidth;
            this.layoutHeight = layoutHeight;
            this.position = new PointF(0, 0);
        }

        public int getWidth(){
            return layoutWidth /inverseWidth;
        }

        public int getHeight(){
            return layoutHeight /inverseHeight;
        }

        public int getInverseWidth(){
            return inverseWidth;
        }

        public int getInverseHeight(){
            return inverseHeight;
        }

        public float getPositionX(){
            return position.x;
        }

        public float getPositionY(){
            return position.y;
        }

        public void setIndex(int index){
            this.index = index;
        }

        public int getIndex(){
            return this.index;
        }

        public GridPosition splitPosition(){
            GridPosition newPosition = new GridPosition(layoutWidth, layoutHeight);
            if(getHeight() >= getWidth()){
                inverseHeight *= 2;
                newPosition.position = new PointF(position.x, position.y+(1.0f/inverseHeight));
            }else{
                inverseWidth *= 2;
                newPosition.position = new PointF(position.x+(1.0f/inverseWidth), position.y);
            }
            newPosition.inverseWidth = this.inverseWidth;
            newPosition.inverseHeight = this.inverseHeight;
            return newPosition;
        }

        @SuppressLint("all")
        @SuppressWarnings("all")
        @Override
        public String toString(){
            StringBuilder result = new StringBuilder();
            result.append(this.getClass().getName());
            result.append(" Object {");
            result.append("\n  layoutWidth: ");
            result.append(layoutWidth);
            result.append("\n  layoutHeight: ");
            result.append(layoutHeight);
            result.append("\n  inverseWidth: ");
            result.append(inverseWidth);
            result.append("\n  inverseHeight: ");
            result.append(inverseHeight);
            result.append("\n  index: ");
            result.append(index);
            result.append("\n  position: ");
            result.append(position);
            result.append("\n}");
            return result.toString();
        }

        /**
         *@inheritDoc
         */
        @Override
        @SuppressWarnings("all")
        public GridPosition clone(){
            GridPosition clone = new GridPosition(getWidth(), getHeight());
            clone.index = index;
            clone.position = new PointF(position.x, position.y);
            clone.inverseWidth = inverseWidth;
            clone.inverseHeight = inverseHeight;
            return clone;
        }
    }
}