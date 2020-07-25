package flirt.and.date;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sex extends AppCompatActivity {
UserInfo userInfo;
String sex = "";
Button male, female, next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sex);

        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        next = findViewById(R.id.next);

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male.setBackgroundResource(R.drawable.male);
                female.setBackgroundResource(R.drawable.female_grey);
                sex = Constants.male;
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                female.setBackgroundResource(R.drawable.female);
                male.setBackgroundResource(R.drawable.male_grey);
                sex = Constants.female;
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sex.length()>0){
                    next();
                }
                else {
                    Toast.makeText(Sex.this, "You need to choose sex!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void next(){
        userInfo = new UserInfo();
        userInfo.setSex(sex);

        Intent intent = new Intent(this, Target.class);
        intent.putExtra(Constants.userInfo, userInfo);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
