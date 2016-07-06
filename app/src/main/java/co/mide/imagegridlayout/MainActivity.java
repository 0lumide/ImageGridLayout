package co.mide.imagegridlayout;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

import co.mide.textimageview.TextImageView;

public class MainActivity extends AppCompatActivity {
    ImageGridLayout gridLayout;
    EditText editText;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        gridLayout = (ImageGridLayout)findViewById(R.id.grid);
        editText = (EditText) findViewById(R.id.edittext);
        gridLayout.setOnMoreClickedCallback(new ImageGridLayout.OnMoreClicked() {
            @Override
            public void onMoreClicked(ImageGridLayout layout) {
                Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
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
                gridLayout.setMaxImagesCount(getIndex());
            }
        });
    }

    private int getIndex(){
        try {
            return Integer.parseInt(editText.getText().toString());
        }catch(Exception e){
            return 0;
        }
    }
    public void addNewImageView(View v){
        TextImageView imageView = new TextImageView(this);
        imageView.setImageBackgroundColor(0xff000000|new Random().nextInt());
        imageView.setTextColor(0xff000000|new Random().nextInt());
        imageView.setText(""+(i++));
        gridLayout.addView(imageView, getIndex());
    }
}
