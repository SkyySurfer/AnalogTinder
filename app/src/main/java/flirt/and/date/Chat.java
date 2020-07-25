package flirt.and.date;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Chat extends AppCompatActivity {
ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChoose();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }


    private void goToChoose(){
        Intent intent = new Intent(this, Choose.class);
        startActivity(intent);
        finish();
    }
}
