package flirt.and.date;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Target extends AppCompatActivity {
    UserInfo userInfo;
    String target="";
    Button girlfriend, boyfriend, next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.target);

        userInfo = getUserInfo();

        girlfriend = findViewById(R.id.girl);
        boyfriend = findViewById(R.id.boy);
        next = findViewById(R.id.next);

        girlfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                girlfriend.setBackgroundResource(R.drawable.girlfriend);
                boyfriend.setBackgroundResource(R.drawable.boy_grey);
                target = Constants.girlfriend;
            }
        });

        boyfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boyfriend.setBackgroundResource(R.drawable.boyfriend);
                girlfriend.setBackgroundResource(R.drawable.girl_grey);
                target = Constants.boyfriend;
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (target.length()>0){
                    next();
                }
                else {
                    Toast.makeText(Target.this, "You need to choose target!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private UserInfo getUserInfo() {
        return (UserInfo) getIntent().getSerializableExtra(Constants.userInfo);
    }


    private void next(){
        userInfo.setTarget(target);
        Intent intent = new Intent(this, DateBirth.class);
        intent.putExtra(Constants.userInfo, userInfo);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
