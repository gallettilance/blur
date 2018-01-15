package gallettilance.blur;

import android.content.Intent;
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
import java.io.IOException;

public class capture extends AppCompatActivity {

    private Camera mCamera = null;
    private CameraView mCameraView = null;
    private TextView mTextView;
    private int IMAGE_SIZE = 28;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        Intent intent = getIntent();
        String mytext;

        try {
            mytext = intent.getExtras().getString("text");
        } catch(Exception e) {
            mytext = "Take Picture";
        }

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
            mTextView.setText(mytext);
        }

        findViewById(R.id.captureButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Take picture using the camera without preview.
                Camera.Parameters camParams = mCamera.getParameters();

                Camera.Size previewSize = camParams.getSupportedPreviewSizes().get(0);

                for (Camera.Size size : camParams.getSupportedPreviewSizes()) {
                    if (size.width <= IMAGE_SIZE && size.height <= IMAGE_SIZE) {
                        previewSize = size;
                        break;
                    }
                }

                camParams.setPreviewSize(previewSize.width, previewSize.height);

                // Try to find the closest picture size to match the preview size.
                Camera.Size pictureSize = camParams.getSupportedPictureSizes().get(0);
                for (Camera.Size size : camParams.getSupportedPictureSizes()) {
                    if (size.width == previewSize.width && size.height == previewSize.height) {
                        pictureSize = size;
                        break;
                    }
                }

                camParams.setPictureSize(pictureSize.width, pictureSize.height);

                mCamera.takePicture(null, null, mPictureCallback);
            }
        });
    }

    private Bitmap processImage(byte[] data) throws IOException {
        // Determine the width/height of the image
        int width = mCamera.getParameters().getPictureSize().width;
        int height = mCamera.getParameters().getPictureSize().height;

        // Load the bitmap from the byte array
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Rotate and crop the image into a square
        int croppedWidth = (width > height) ? height : width;
        int croppedHeight = (width > height) ? height : width;

        Bitmap cropped = Bitmap.createBitmap(bitmap, 0, 0, croppedWidth, croppedHeight);
        bitmap.recycle();

        // Scale down to the output size
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropped, IMAGE_SIZE, IMAGE_SIZE, true);
        cropped.recycle();

        return scaledBitmap;
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                final Bitmap bitmapPicture = processImage(data);
                Intent intent = new Intent(capture.this, view_capture.class).putExtra("BitmapImage", bitmapPicture);
                startActivity(intent);
            } catch (Exception e) {
                Log.d("Error", e.toString());
                FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
                camera_view.removeAllViews();
                camera_view.addView(mCameraView);
                mTextView.setText("Please try again...");
            }
        }
    };
}

/*
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

