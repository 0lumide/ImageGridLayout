package co.mide.imagegridlayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
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
    private int MARGIN = 2;

    public ImageGridLayout(Context context){
        super(context);
        init();
    }

    public ImageGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        queue = new LinkedList<>();
        queuePosition = new HashMap<>();
    }

    private void updateViews(){
        //update all the children. Since the number of columns could have changed
        for(int i = 0; i < queue.size(); i++){
            GridPosition gridPosition = queue.get(i);

            View child = getChildAt(gridPosition.getIndex());
            LayoutParams params = (LayoutParams)child.getLayoutParams();
            layoutParamsFromGridPosition(gridPosition, params);
            child.setLayoutParams(params);
            if(child instanceof ImageView)
                ((ImageView)child).setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
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
            }else{
                GridPosition gridPosition = queue.poll();
                GridPosition newGridPosition = gridPosition.splitPosition();
                newGridPosition.setIndex(i);
                queue.add(newGridPosition);
                queue.add(gridPosition);
                queuePosition.put(newGridPosition.getIndex(), newGridPosition);
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
            updateLayoutRepresentation(getWidth(), getHeight(), getChildCount() + 1, false);

//            int columnCount = getNewColumnCount();

            //add the new view
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
                super.addView(child, index, params);
                updateViews();
                setColumnCount(newColumnCount);
            }
        }else{
            throw new IndexOutOfBoundsException("index "+index+" cannot be less that -1 or more than size "+getImageCount());
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
    public void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("dbug", "size changed");
    }

    /**
     * This method returns the number of images the layout contains.
     * Note that this method is different from getChildCount() as this method
     * doesn't include the more images in the count
     * @return the number of images in the layout
     */
    public int getImageCount(){
        return getChildCount();
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