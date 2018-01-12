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
            System.out.print(bitmapPicture);

            if(mCamera != null) {
                FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
                camera_view.removeAllViews();
                camera_view.addView(mCameraView);
                mTextView.setText("Picture Captured!");
            } else {
                mTextView.setText("Please try again...");
            }
        }
    };

}
