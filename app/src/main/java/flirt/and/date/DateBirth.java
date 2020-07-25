package flirt.and.date;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateBirth extends AppCompatActivity {
    UserInfo userInfo;
    String dateBirth;
    EditText dateBirthTxt;
    Button next;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    static{
        DATE_FORMAT.setLenient(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_birth);

        dateBirthTxt = findViewById(R.id.date_birth);
        next = findViewById(R.id.next);

        userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.userInfo);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dateBirth = dateBirthTxt.getText().toString();
                if (isValid(dateBirth)) {
                    next();
                }
                else{
                    Toast.makeText(DateBirth.this, "Invalid date format!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    private static boolean isValid(String date){
        try {

            if (DATE_FORMAT.format(DATE_FORMAT.parse(date)).equals(date)){
                Date currentDate = Calendar.getInstance().getTime();

                if (currentDate.after(DATE_FORMAT.parse(date))){
                    return true;
                }
            }


        }catch (ParseException ex){
            return false;
        }
        return false;
    }



    private void next(){
        userInfo.setDateBirth(dateBirth);
        Intent intent = new Intent(this, Photos.class);
        intent.putExtra(Constants.userInfo, userInfo);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

    }

}
