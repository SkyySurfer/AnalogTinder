package flirt.and.date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    EditText emailTxt, passwordTxt;
    Button login;
    TextView notRegistered;



    private void findViews(){
        login = findViewById(R.id.login);
        emailTxt = findViewById(R.id.email);
        passwordTxt = findViewById(R.id.password);
        notRegistered = findViewById(R.id.notregistersignup);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        findViews();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                email = emailTxt.getText().toString();
                password = passwordTxt.getText().toString();

                if (checkField(password) && checkEmail(email)) {
                    loginUser();
                }
            }
        });

        notRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToView(SignUp.class);
            }
        });
    }


private void openLastDisplay(){
        SharedPreferencesEditor sharedPreferencesEditor = new SharedPreferencesEditor(getApplicationContext());
        if (sharedPreferencesEditor.checkPreferencesStorage(Constants.userId)){
            goToView(Choose.class);

        }
        else{
            goToView(Sex.class);
        }
}

    private void loginUser(){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            LogClass.log("logInWithEmail:success");
                            openLastDisplay();
                        } else {

                            LogClass.log("logInWithEmail:failure" + task.getException());
                            Toast.makeText(Login.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    private Boolean checkField(String field){
        if (field!=null){
            if (field.length()>=6){
                return true;
            }
        }

        Toast.makeText(Login.this, "Invalid password format!", Toast.LENGTH_SHORT).show();

        return false;
    }

    private Boolean checkEmail(String field){
        Boolean isValid;
        if (field!=null){
                isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
                if (isValid){
                    return true;
                }
            }
        Toast.makeText(Login.this, "Invalid email format!", Toast.LENGTH_SHORT).show();
        return false;
        }



    private void goToView(Class activity){
        Intent intent = new Intent(this,activity);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

    }

}
