package co.mide.imagegridlayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * This is a GridLayout designed specifically for images.
 * It automatically arranges the images in the layout.
 * Created by Olumide on 6/14/2016.
 */
@SuppressWarnings("unused")
public class ImageGridLayout extends GridLayout {
    private LinkedList<GridPosition> queue;
    private HashMap<Integer, GridPosition> queuePosition;
    private OnMoreClicked onMoreClickedCallback;
    private MinimumImageSizeLimitReached minImageSizeLimitHandle;
    private int moreColor = 0xffcccccc;
    private int moreTextColor = 0xffffffff;
    private int maxImages = Integer.MAX_VALUE;
    private int minImageHeight = 150;
    private int minImageWidth = 150;
    //This coincidentally also happens to be the same size as the smallest GridPosition
    private GridPosition lowerRightCorner;
    private int MARGIN = 2;

    public ImageGridLayout(Context context){
        super(context);
        init();
    }

    public ImageGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ImageGridLayout,
                0, 0);

        try {
            setMoreImagesColor(a.getColor(R.styleable.ImageGridLayout_moreColor, moreColor));
            maxImages = a.getInt(R.styleable.ImageGridLayout_maxImageCount, maxImages);
            minImageHeight = a.getInt(R.styleable.ImageGridLayout_minImageHeight, minImageHeight);
            minImageWidth = a.getInt(R.styleable.ImageGridLayout_minImageHeight, minImageWidth);
        } finally {
            a.recycle();
        }
        init();
    }

    private void init(){
        setMinimumImageSizeLimitReachedHandler(new MinimumImageSizeLimitReached() {
            @Override
            public void onMinimumImageSizeLimitReached(ImageGridLayout layout, int width, int height) {
                if(layout.getLayoutParams() instanceof GridLayout.LayoutParams) {
                    LayoutParams params = (GridLayout.LayoutParams) layout.getLayoutParams();
                    params.height *= 1.5;
                    layout.setLayoutParams(params);
                }else{
                    ViewGroup.LayoutParams params = layout.getLayoutParams();
                    params.height *= 1.5;
                    layout.setLayoutParams(params);
                }
            }
        });
        queue = new LinkedList<>();
        queuePosition = new HashMap<>();
    }

    /**
     * This sets the maximum number of images to be contained in the layout.
     * If count is less than the current number of images in the layout,
     * a number of images that equals the difference are discarded
     * @param count the maximum number of images the layout will contain
     */
    public void setMaxImagesCount(int count){
        //TODO
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
        double whiteContrast = ColorUtils.calculateContrast(Color.WHITE, moreColor);
        double blackContrast = ColorUtils.calculateContrast(Color.BLACK, moreColor);
        if(whiteContrast >= blackContrast){
            moreTextColor = Color.WHITE;
        }else{
            moreTextColor = Color.BLACK;
        }
    }

    /**
     * This method returns the color of the more images image
     * @return the color of the more images image
     */
    public int getMoreImagesColor(){
        return moreColor;
    }

    /**
     * This sets the minimum size of the smallest image contained in the layout.
     * The default behaviour is to set the current number of child images as the maximum number of images
     * till the layout can accommodate more images without
     * resizing the images to be smaller than the minimum size.
     * Custom behaviour can be defined by setting minimumImageSizeLimitReached handler
     * <br>
     * Note that if the layout becomes smaller than this size, these values are discarded.
     * @param width the minimum layoutWidth of the smallest image in the layout
     * @param height the minimum layoutHeight of the smallest image in the layout
     */
    public void setMinimumImageSize(int width, int height){
        if(width > getWidth()) {//check if the layout would be able to contain the size
            throw new IllegalArgumentException("image minimum layoutWidth cannot be larger than the layout layoutWidth");
        }
        if(height > getHeight()) {//check if the layout would be able to contain the size
            throw new IllegalArgumentException("image minimum layoutHeight cannot be larger than the layout layoutHeight");
        }
        if(lowerRightCorner != null){
            //Check if the new limit is already been reached
            if((lowerRightCorner.getWidth() + MARGIN < width) || (lowerRightCorner.getHeight() + MARGIN < height)){
                if(minImageSizeLimitHandle != null) {
                    minImageSizeLimitHandle.onMinimumImageSizeLimitReached(this, getWidth(), getHeight());
                    updateLayoutRepresentation(getChildCount(), true);
                    updateViews();
                }
            }
        }
    }

    /**
     * This method returns the minimum image layoutHeight.
     * @return the minimum image layoutHeight
     */
    public int getMinimumImageHeight(){
        return minImageHeight;
    }

    /**
     * This method returns the minimum image layoutWidth.
     * @return the minimum image layoutWidth
     */
    public int getMinimumImageWidth(){
        return minImageWidth;
    }

    public void setMinimumImageSizeLimitReachedHandler(MinimumImageSizeLimitReached handler){
        this.minImageSizeLimitHandle = handler;
    }

    /**
     * This sets the callback that is called when the user touches the more images image
     * @param callback this is the callback that is called.
     */
    public void setOnMoreClickedCallback(OnMoreClicked callback){
        this.onMoreClickedCallback = callback;
    }

    private void updateViews(){
        //update all the children. Since the number of columns could have changed
        for(int i = 0; i < queue.size(); i++){
            GridPosition gridPosition = queue.get(i);

            LayoutParams params = layoutParamsFromGridPosition(gridPosition);

            View child = getChildAt(gridPosition.getIndex());
            if(child instanceof ImageView)
                ((ImageView)child).setScaleType(ImageView.ScaleType.CENTER_CROP);

            child.setLayoutParams(params);
        }
    }

    private void updateLayoutRepresentation(int newSize, boolean reset){
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
                GridPosition gridPosition = new GridPosition(getWidth(), getHeight());
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
    }

    private LayoutParams layoutParamsFromGridPosition(GridPosition gridPosition){
        LayoutParams params = new LayoutParams();
        params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
        params.height = gridPosition.getHeight() - params.topMargin - params.bottomMargin;
        params.width = gridPosition.getWidth() - params.leftMargin - params.rightMargin;
        params.columnSpec = GridLayout.spec((int)(gridPosition.getPositionX()*getColumnCount()),
                getColumnCount()/gridPosition.getInverseWidth());
        params.rowSpec = GridLayout.spec((int)(gridPosition.getPositionY()*getColumnCount()),
                getColumnCount()/gridPosition.getInverseHeight());
        return params;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom){
        super.onLayout(changed, left, top, right, bottom);
        updateLayoutRepresentation(getChildCount(), true);
        updateViews();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("size", "changed");
        if(lowerRightCorner != null && oldw != 0 && oldh != 0){
            //Check if the new limit is already been reached
            if((lowerRightCorner.getWidth() + MARGIN < getMinimumImageWidth()) || (lowerRightCorner.getHeight() + MARGIN < getMinimumImageHeight())){
                if(minImageSizeLimitHandle != null) {
                    minImageSizeLimitHandle.onMinimumImageSizeLimitReached(this, getWidth(), getHeight());
                    updateLayoutRepresentation(getChildCount(), true);
                    updateViews();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addView(View child){
        addView(child, getImageCount());
    }

    /**
     * This is equivalent to updateViews(ImageView)
     * @param child the ImageView to be inserted into the layout
     * @param index the position where the ImageView should be inserted
     * @param params this value is ignored
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params){
        addView(child, index);
    }

    /**
     * This method is equivalent to updateViews(ImageView)
     * @param child the imageView to add to the layout
     * @param params this value is ignored
     */
    @Override
    public void addView(View child, ViewGroup.LayoutParams params){
        addView(child, getImageCount());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void	addView(View child, int index){
        if(index <= getImageCount()) {
            updateLayoutRepresentation(getChildCount() + 1, false);
            //Set the column count
            int columnCount = queue.getLast().getInverseWidth() > queue.getLast().getInverseHeight() ?
                    queue.getLast().getInverseWidth() : +queue.getLast().getInverseHeight();
            setColumnCount(columnCount);

            //add the new view
            if (child instanceof ImageView)
                ((ImageView) child).setScaleType(ImageView.ScaleType.CENTER_CROP);
            GridPosition gridPosition;
            if (index == -1)
                gridPosition = queuePosition.get(queue.size() - 1);
            else
                gridPosition = queuePosition.get(index);
            LayoutParams params = layoutParamsFromGridPosition(gridPosition);

            super.addView(child, getImageCount(), params);

            updateViews();
        }else{
            throw new IndexOutOfBoundsException("index "+index+" cannot be more than size "+getImageCount());
        }
    }

    /**
     * This is equivalent to updateViews(ImageView)
     * @param child the ImageView to be added to the layout
     * @param width this value is ignored
     * @param height this value is also ignored
     */
    @Override
    public void	addView(View child, int width, int height){
        addView(child, getImageCount());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout){
//        if((index) <= getImageCount()){
            return super.addViewInLayout(child, index, params, preventRequestLayout);
//        }else{
//            throw new IndexOutOfBoundsException();
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean	addViewInLayout(View child, int index, ViewGroup.LayoutParams params){
//        if((index) <= getImageCount()){
            return super.addViewInLayout(child, index, params);
//        }else{
//            throw new IndexOutOfBoundsException();
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void	removeViewAt(int index){
        if((index) < getImageCount()){
            super.removeViewAt(index);
        }else{
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void	removeViews(int start, int count){
        if((start + count) <= getImageCount()){
            super.removeViews(start, count);
        }else{
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeViewsInLayout(int start, int count){
        if((start + count) <= getImageCount()){
            super.removeViewsInLayout(start, count);
        }else{
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void onViewRemoved(View view){
        super.onViewRemoved(view);
        //todo
    }

    /**
     * This method returns the number of images the layout contains.
     * Note that this method is different from getChildCount() as this method
     * doesn't include the more images in the count
     * @return the number of images in the layout
     */
    public int getImageCount(){
        if(getChildCount() <=  maxImages)
            return getChildCount();
        return getChildCount() - 1;
    }

    public interface OnMoreClicked{
        void onMoreClicked(ImageGridLayout layout);
        void onMoreLongClicked(ImageGridLayout layout);
    }

    public interface MinimumImageSizeLimitReached{
        /**
         * Handler to define layout behaviour when the smallest image siz is less than the minimum size
         * @param layout the ImageGridLayout containing the image that is about to be smaller than the minimum size
         * @param width the tentative layoutWidth of the smallest imageView
         * @param height the tentative layoutHeight of the smallest imageView
         */
        void onMinimumImageSizeLimitReached(ImageGridLayout layout, int width, int height);
    }

    /**
     * object that holds information on how to size and position image
     * Created by Olumide on 6/14/2016.
     */
    class GridPosition {
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
        @Override
        public String toString(){
            return String.format("{\n  layoutWidth: %d,\n  layoutHeight: %d\n  inverseWidth: %d\n  inverseHeight: %d\n" +
                    "  position {\n" +
                    "    x: %.2f\n" +
                    "    y: %.2f\n" +
                    "  }\n" +
                    "}\n", layoutWidth, layoutHeight, inverseWidth, inverseHeight, position.x, position.y);
        }
    }
}