package flirt.and.date;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                next();
            }
        },1000);
    }


    private void next(){
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {

    }
}
