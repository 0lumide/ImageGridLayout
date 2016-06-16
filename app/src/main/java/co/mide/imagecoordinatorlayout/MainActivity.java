package co.mide.imagecoordinatorlayout;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageGridLayout gridLayout;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        gridLayout = (ImageGridLayout)findViewById(R.id.grid);
    }

    public void addNewImageView(View v){
        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(ContextCompat.getDrawable(this, getImage(++i)));
        gridLayout.addView(imageView);
    }

    private int getImage(int num){
        switch(num){
            case 1:
                return R.drawable.a1;
            case 2:
                return R.drawable.a2;
            case 3:
                return R.drawable.a3;
            case 4:
                return R.drawable.a4;
            case 5:
                return R.drawable.a5;
            case 6:
                return R.drawable.a6;
            case 7:
                return R.drawable.a7;
            case 8:
                return R.drawable.a8;
            default:
                return R.drawable.a1;
        }
    }
}
