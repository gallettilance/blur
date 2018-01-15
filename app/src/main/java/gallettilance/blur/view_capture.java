package gallettilance.blur;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.util.Log;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.view.View;
import android.graphics.Color;

import java.text.DecimalFormat;
import java.lang.Integer;

public class view_capture extends AppCompatActivity {

    ImageView image_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_capture);
        Intent intent = getIntent();
        final Bitmap bitmapPicture = intent.getParcelableExtra("BitmapImage");
        image_view = findViewById(R.id.image_view);
        image_view.setImageBitmap(bitmapPicture);

        findViewById(R.id.yesButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StringBuilder img = new StringBuilder(bitmapPicture.getWidth() * bitmapPicture.getHeight());

                for(int i=0; i < bitmapPicture.getWidth(); i++)
                {
                    for(int j=0; j < bitmapPicture.getHeight(); j++)
                    {
                        int colour = bitmapPicture.getPixel(i, j);
                        int red = Color.red(colour);
                        int blue = Color.blue(colour);
                        int green = Color.green(colour);

                        DecimalFormat df = new DecimalFormat("#.##");
                        double myRGB = Double.valueOf(df.format((red + green + blue)/3.0));
                        img.append(Double.toString(Double.valueOf(df.format((.99 * myRGB / 255.0) + .01))));
                        img.append(',');
                    }
                }

                Log.d("Image width", Integer.toString(bitmapPicture.getWidth()));
                Log.d("Image height", Integer.toString(bitmapPicture.getHeight()));
                Log.d("Image", img.toString());

                String img_label = "0";
                String img_type = "digit";

                String myUrl = "https://rest-blur.herokuapp.com/images";
                HttpPOSTRequest postRequest = new HttpPOSTRequest();

                try {
                    postRequest.execute(myUrl, img.toString(), img_label, img_type);
                } catch(Exception e) {
                    Log.d("Error", e.toString());
                }

                Intent intent = new Intent(view_capture.this, capture.class).putExtra("text", "Sent to DB");
                startActivity(intent);

            }
        });


        findViewById(R.id.noButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view_capture.this, capture.class).putExtra("text", "Try again");
                startActivity(intent);
        }});

    }
}
