package co.mide.imagecoordinatorlayout;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * This is a GridLayout designed specifically for images.
 * It automatically arranges the images in the layout.
 * Created by Olumide on 6/14/2016.
 */
public class ImageGridLayout extends GridLayout {
    LinkedList<GridPosition> queue;
    LinkedList<ImageView> views;
    private int maxImages = 7;
    private int extra = 0;
    private TextView textView;

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
        views = new LinkedList<>();
    }

    private void initViews(){
        GridPosition lowerRightCorner = null;
        Log.e("init", "init");
        queue = new LinkedList<>();
        for(int i = 0; i < views.size() && i < maxImages; i++) {
            if(i == 0){
                GridPosition gridPosition = new GridPosition(getWidth(), getHeight());
                gridPosition.setIndex(i);
                queue.add(gridPosition);
                lowerRightCorner = gridPosition;
            }else{
                GridPosition gridPosition = queue.poll();
                GridPosition newGridPosition = gridPosition.splitPosition();
                newGridPosition.setIndex(i);
                queue.add(newGridPosition);
                queue.add(gridPosition);
            }
        }

        for(int i = 0; i < queue.size(); i++) {
            Log.d("queue", queue.get(i).toString());
        }

        if(queue.size() > 0){
            int columnCount = queue.getLast().getInverseWidth() > queue.getLast().getInverseHeight()?
                    queue.getLast().getInverseWidth(): +queue.getLast().getInverseHeight();
            Log.d("columnCount", ""+columnCount);
            setColumnCount(columnCount);
        }

        int size = queue.size();
        for(int i = 0; i < size && i < maxImages; i++){
            GridPosition gridPosition = queue.poll();
            ImageView child = views.get(gridPosition.getIndex());
            child.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LayoutParams params = new LayoutParams();
            params.height = getHeight()/gridPosition.getInverseHeight();
            params.width = getWidth()/gridPosition.getInverseWidth();
            params.columnSpec = GridLayout.spec((int)(gridPosition.getPositionX()*getColumnCount()),
                    getColumnCount()/gridPosition.getInverseWidth());
            params.rowSpec = GridLayout.spec((int)(gridPosition.getPositionY()*getColumnCount()),
                    getColumnCount()/gridPosition.getInverseHeight());
            params.setMargins(2, 2, 2, 2);
            Log.d("children", "child: "+ (i+1));
            child.setLayoutParams(params);

            if(lowerRightCorner == null || (gridPosition.getPositionX() >= lowerRightCorner.getPositionX()
                    && gridPosition.getPositionY() >= lowerRightCorner.getPositionY())){
                lowerRightCorner = gridPosition;
            }
            if(gridPosition.getIndex() == size - 1 && extra <= 0) {
                super.addView(child);
            }
        }
        if(extra > 0) {
            if(textView == null) {
                textView = new TextView(getContext());
                textView.setTextSize(0.08f * (getWidth() / lowerRightCorner.getInverseWidth()));
                textView.setBackgroundColor(Color.parseColor("#aaffffff"));
                textView.setGravity(Gravity.CENTER);

                LayoutParams params2 = new LayoutParams();
                params2.height = getHeight() / lowerRightCorner.getInverseHeight();
                params2.width = getWidth() / lowerRightCorner.getInverseWidth();
                params2.columnSpec = GridLayout.spec((int) (lowerRightCorner.getPositionX() * getColumnCount()),
                        getColumnCount() / lowerRightCorner.getInverseWidth());
                params2.rowSpec = GridLayout.spec((int) (lowerRightCorner.getPositionY() * getColumnCount()),
                        getColumnCount() / lowerRightCorner.getInverseHeight());
                params2.setMargins(2, 2, 2, 2);
                textView.setLayoutParams(params2);
                super.addView(textView);
            }
            textView.setText(String.format("+%d", extra));
        }
    }

    /**
     * This retrieves the number of images after which no more images would be added to the layout
     * @return the limiting number of images. It defaults to 10
     */
    public int getMaxImages(){
        return this.maxImages;
    }

    /**
     * This sets the number of more images that is shown
     * @param num the number of images to show
     */
    public void setMoreImages(int num){
        extra = num;
        initViews();
    }

    /**
     * This adds the ImageView to the layout
     * @param child the ImageView to be inserted into the layout
     */
    public void addView(ImageView child){
        if(views.size() < maxImages) {
            views.add(child);
        }else{
            extra++;
        }
        initViews();
    }

    /**
     * This is equivalent to addView(ImageView)
     * @param child the ImageView to be inserted into the layout
     * @param index the position where the ImageView should be inserted
     * @param params this value is ignored
     */
    public void addView(ImageView child, int index, ViewGroup.LayoutParams params){
        addView(child, index);
    }

    /**
     * This method is equivalent to addView(ImageView)
     * @param child the imageView to add to the layout
     * @param params this value is ignored
     */
    public void addView(ImageView child, ViewGroup.LayoutParams params){
        addView(child);
    }

    /**
     * This method inserts the ImageView at the specified index
     * @param child the ImageView to add to the layout
     * @param index the index at which to insert the ImageView
     */
    public void	addView(ImageView child, int index){
        if(views.size() < maxImages) {
            views.add(index, child);
        }else{
            extra++;
        }
        initViews();
    }

    /**
     * This is equivalent to addView(ImageView)
     * @param child the ImageView to be added to the layout
     * @param width this value is ignored
     * @param height this value is also ignored
     */
    public void	addView(ImageView child, int width, int height){
        addView(child);
    }

    @Override
    public int getChildCount(){
        return views.size();
    }
}