package co.mide.sample.imagegridlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;
import co.mide.imagegridlayout.ImageGridLayout;
import co.mide.textimageview.TextImageView;

public class MainActivity extends AppCompatActivity {
    ImageGridLayout gridLayout;
    int i = 0;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_activity);
        gridLayout = (ImageGridLayout)findViewById(R.id.grid);
        int limit = new Random().nextInt(12)+1;
        gridLayout.setMaxImagesCount(limit);
        ((TextView)findViewById(R.id.image_limit_text)).setText(getResources().getString(R.string.limit_text, limit));
        gridLayout.setOnMoreClickedCallback(new ImageGridLayout.OnMoreClicked() {
            @Override
            public void onMoreClicked(ImageGridLayout layout) {
                showToast("More images clicked");
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextImageView imageView = new TextImageView(MainActivity.this);
                imageView.setImageBackgroundColor(0xff000000|new Random().nextInt());
                imageView.setTextColor(0xff000000|new Random().nextInt());
                imageView.setText(""+(i++));
                int index = getIndex();
                gridLayout.addView(imageView, index);
                showToast("Image added at index: "+index);
            }
        });
        findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gridLayout.getChildCount() > 0) {
                    int index = getIndex();
                    gridLayout.removeViewAt(index);
                    showToast("Image removed from index: "+index);
                }
            }
        });
    }

    Toast toast;
    private void showToast(String message){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private int getIndex(){
        if(gridLayout.getImageCount() <= 0)
            return 0;
        Random random = new Random();
        return random.nextInt(gridLayout.getImageCount());
    }
}