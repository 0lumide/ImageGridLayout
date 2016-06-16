package co.mide.imagecoordinatorlayout;

import android.annotation.SuppressLint;
import android.graphics.PointF;

/**
 * object that holds information on how to size and position image
 * Created by Olumide on 6/14/2016.
 */
public class GridPosition {
    private int width, height;
    private int inverseWidth = 1;
    private int inverseHeight = 1;
    private PointF position;
    private int index;

    /**
     * Constructor for GridPosition
     * @param width this is the width of the GridLayout
     * @param height this is the height of the GridLayout
     */
    public GridPosition(int width, int height){
        this.width = width;
        this.height = height;
        this.position = new PointF(0, 0);
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
        GridPosition newPosition = new GridPosition(width, height);
        if(height/inverseHeight >= width/inverseWidth){
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
        return String.format("{\n  width: %d,\n  height: %d\n  inverseWidth: %d\n  inverseHeight: %d\n" +
                "  position {\n" +
                "    x: %.2f\n" +
                "    y: %,2f\n" +
                "  }\n" +
                "}\n", width, height, inverseWidth, inverseHeight, position.x, position.y);
    }
}
