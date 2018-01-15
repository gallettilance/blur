package gallettilance.blur;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;

public class main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void cameraAccess_digit(View view) {
        Intent intent = new Intent(main.this, capture.class).putExtra("text", "Take picture of Digit");
        startActivity(intent);
    }

    public void cameraAccess_letter(View view) {
        Intent intent = new Intent(main.this, capture.class).putExtra("text", "Take picture of Letter");
        startActivity(intent);
    }

    public void cameraAccess_word(View view) {
        Intent intent = new Intent(main.this, capture.class).putExtra("text", "Take picture of Word");
        startActivity(intent);
    }
}
