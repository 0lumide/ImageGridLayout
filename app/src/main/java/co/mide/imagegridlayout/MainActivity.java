package co.mide.imagegridlayout;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageGridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        gridLayout = (ImageGridLayout)findViewById(R.id.grid);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                gridLayout.upNumColumns();
//                gridLayout.setMinimumImageSize(300, 300);
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewImageView(v);
            }
        });
        findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridLayout.removeViewAt(0);
            }
        });
    }

    public void addNewImageView(View v){
        ImageView imageView = new ImageView(this);
//        imageView.setImageDrawable(ContextCompat.getDrawable(this, getImage(gridLayout.getChildCount() + 1)));
//        imageView.setImageDrawable(ContextCompat.getDrawable(this, ));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //TODO use ColorUtils.blendArgb to determine ripple color
            RippleDrawable rippledImage = new RippleDrawable(
                    ColorStateList.valueOf(Color.RED), ContextCompat.getDrawable(this, getImage(gridLayout.getChildCount() + 1)), null);
            imageView.setImageDrawable(rippledImage);
            imageView.setClickable(true);
        }else{
            imageView.setImageDrawable(ContextCompat.getDrawable(this, getImage(gridLayout.getChildCount() + 1)));
        }
        gridLayout.addView(imageView);
    }

    private int getImage(int num){
        switch(num){
            case 1:
                return R.drawable.image1;
            case 2:
                return R.drawable.image2;
            case 3:
                return R.drawable.image3;
            case 4:
                return R.drawable.image4;
            case 5:
                return R.drawable.image5;
            case 6:
                return R.drawable.image6;
            case 7:
                return R.drawable.image7;
            case 8:
                return R.drawable.image8;
            case 9:
                return R.drawable.image9;
            case 10:
                return R.drawable.image10;
            default:
                return R.drawable.more;
        }
    }
}
