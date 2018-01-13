package gallettilance.blur;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Camera;
import android.util.Log;
import android.widget.FrameLayout;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.Integer;

import javax.net.ssl.HttpsURLConnection;

public class capture extends AppCompatActivity {

    private Camera mCamera = null;
    private CameraView mCameraView = null;
    private TextView mTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        try{
            mCamera = Camera.open();
        } catch (Exception e){
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        if(mCamera != null) {
            mCameraView = new CameraView(this, mCamera);
            FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);
            mTextView = findViewById(R.id.mTextView);
            mTextView.setText("Hello!");
        }

        findViewById(R.id.captureButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Take picture using the camera without preview.
                mCamera.takePicture(null, null, mPictureCallback);
            }
        });
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // decode the data obtained by the camera into a Bitmap
            Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);

            // get rgb values of bitmapPicture
            int[][] rgbValues;
            StringBuilder img = new StringBuilder(bitmapPicture.getWidth() * bitmapPicture.getHeight());
            rgbValues = new int[bitmapPicture.getWidth()][bitmapPicture.getHeight()];

            for(int i=0; i < bitmapPicture.getWidth(); i++)
            {
                for(int j=0; j < bitmapPicture.getHeight(); j++)
                {
                    rgbValues[i][j] = bitmapPicture.getPixel(i, j);
                    img.append(Integer.toString(bitmapPicture.getPixel(i, j)));
                    img.append(',');
                }
            }

            String img_label = "0";
            String img_type = "digit";


            //get model/weights from REST API
            String myUrl = "https://rest-blur.herokuapp.com/images";
            HttpPOSTRequest postRequest = new HttpPOSTRequest();
            try {
                postRequest.execute(myUrl, img.toString(), img_label, img_type);
            } catch(Exception e) {
                System.out.print(e);
            }

            JSONObject jsonRes;
            HttpGETRequest getRequest = new HttpGETRequest();
            String res;

            try {
                jsonRes = getRequest.execute(myUrl).get();
            } catch(Exception e) {
                jsonRes = null;
            }

            try {
                res = jsonRes.get("images").toString();
            } catch(Exception e) {
                res = e.toString();
            }

            /*
            if(json != null) {

                String wih;
                String who;
                int input_layer;
                int hidden_layer;
                int output_layer;
                double[][] model_wih;
                double[][] model_who;

                try {
                    wih = json.get("model_wih").toString();
                    who = json.get("model_who").toString();

                    input_layer = Integer.parseInt(json.get("model_input_layer").toString());
                    hidden_layer = Integer.parseInt(json.get("model_input_layer").toString());
                    output_layer = Integer.parseInt(json.get("model_output_layer").toString());

                    model_wih = new double[input_layer][hidden_layer];
                    model_who = new double[hidden_layer][output_layer];

                    for (int i = 0; i < wih.length(); i++){
                        char c = wih.charAt(i);
                        if(c != ','){
                            model_wih[i / hidden_layer][i % input_layer] = Character.getNumericValue(c);
                        }
                    }

                    for (int i = 0; i < who.length(); i++){
                        char c = who.charAt(i);
                        if(c != ','){
                            model_who[i / output_layer][i % hidden_layer] = Character.getNumericValue(c);
                        }
                    }

                } catch(Exception e) {
                    System.out.print(e);
                }
            }
            */

            //Apply model


            //Store Picture with a label in DB


            if(mCamera != null) {
                FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
                camera_view.removeAllViews();
                camera_view.addView(mCameraView);
                mTextView.setText(res);
                // "Picture Captured!");
            } else {
                mTextView.setText("Please try again...");
            }
        }
    };

}
