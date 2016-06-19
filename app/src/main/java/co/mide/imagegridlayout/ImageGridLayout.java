package co.mide.imagegridlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.util.LinkedList;

/**
 * This is a GridLayout designed specifically for images.
 * It automatically arranges the images in the layout.
 * Created by Olumide on 6/14/2016.
 */
public class ImageGridLayout extends GridLayout {
    LinkedList<GridPosition> queue;
    LinkedList<ImageView> views;

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
        Log.d("init", "start");
    }

    private void initViews(int index){
        GridPosition lowerRightCorner = null;
        queue = new LinkedList<>();
        for(int i = 0; i < views.size(); i++) {
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

//        for(int i = 0; i < queue.size(); i++) {
//            Log.d("queue", queue.get(i).toString());
//        }

        if(queue.size() > 0){
            int columnCount = queue.getLast().getInverseWidth() > queue.getLast().getInverseHeight()?
                    queue.getLast().getInverseWidth(): +queue.getLast().getInverseHeight();
            Log.d("columnCount", ""+columnCount);
            setColumnCount(columnCount);
        }

        int size = queue.size();
        for(int i = 0; i < size; i++){
            GridPosition gridPosition = queue.poll();
            ImageView child = views.get(gridPosition.getIndex());
            child.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LayoutParams params = new LayoutParams();
            params.setMargins(2, 2, 2, 2);
            params.height = getHeight()/gridPosition.getInverseHeight() - params.topMargin - params.bottomMargin;
            params.width = getWidth()/gridPosition.getInverseWidth() - params.leftMargin - params.rightMargin;
            params.columnSpec = GridLayout.spec((int)(gridPosition.getPositionX()*getColumnCount()),
                    getColumnCount()/gridPosition.getInverseWidth());
            params.rowSpec = GridLayout.spec((int)(gridPosition.getPositionY()*getColumnCount()),
                    getColumnCount()/gridPosition.getInverseHeight());
            Log.d("children", "child: "+ (i+1));

            if(lowerRightCorner == null || (gridPosition.getPositionX() >= lowerRightCorner.getPositionX()
                    && gridPosition.getPositionY() >= lowerRightCorner.getPositionY())){
                lowerRightCorner = gridPosition;
            }
            if(gridPosition.getIndex() == index) {
                Log.d("super", "super index "+index);
                super.addView(child, index, params);
            }else{
                child.setLayoutParams(params);
            }
        }
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom){
        if(changed) {
            initViews(views.size());
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * This adds the ImageView to the layout
     * @param child the ImageView to be inserted into the layout
     */
    @Override
    public void addView(View child){
        Log.d("addView", "null");
        addView(child, views.size());
    }

    /**
     * This is equivalent to addView(ImageView)
     * @param child the ImageView to be inserted into the layout
     * @param index the position where the ImageView should be inserted
     * @param params this value is ignored
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params){
        Log.d("addView", "index, params");
        addView(child, index);
    }

    /**
     * This method is equivalent to addView(ImageView)
     * @param child the imageView to add to the layout
     * @param params this value is ignored
     */
    @Override
    public void addView(View child, ViewGroup.LayoutParams params){
        Log.d("addView", "params");
        addView(child, views.size());
    }

    /**
     * This method inserts the ImageView at the specified index
     * @param child the ImageView to add to the layout
     * @param index the index at which to insert the ImageView
     */
    @Override
    public void	addView(View child, int index){
        Log.d("addView", "index");
        Log.d("index", index+"");
        if(child instanceof ImageView){
            if(index == -1 || index == views.size()){
                views.add((ImageView) child);
            }else{
                views.add(index, (ImageView) child);
            }
            initViews(index);
        }else{
            throw new IllegalArgumentException("child must be an ImageView or a subclass of ImageView");
        }
    }

    /**
     * This is equivalent to addView(ImageView)
     * @param child the ImageView to be added to the layout
     * @param width this value is ignored
     * @param height this value is also ignored
     */
    @Override
    public void	addView(View child, int width, int height){
        Log.d("addView", "width, height");
        addView(child, views.size());
    }

    @Override
    public int getChildCount(){
        if(views == null) {
            Log.d("size", "null");
        }else {
            Log.d("size", views.size() + "");
        }
        Log.d("super size", super.getChildCount()+"");
        return super.getChildCount();
    }
}