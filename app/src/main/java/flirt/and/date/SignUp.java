package flirt.and.date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private String retryPassword;
    EditText emailTxt, passwordTxt, retryPasswordTxt;
    Button signUp;
    TextView haveAccount;
    RadioButton tick;
    int tickClicks = 0;


    private void findViews(){
        emailTxt = findViewById(R.id.email);
        passwordTxt = findViewById(R.id.password);
        retryPasswordTxt = findViewById(R.id.retrypassword);
        haveAccount = findViewById(R.id.haveaccount);
        signUp = findViewById(R.id.signup);
        tick = findViewById(R.id.tick);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        findViews();



        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToView(Login.class);
            }
        });


        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tickClicks++;
                if (tickClicks%2==1){
                    tick.setButtonDrawable(R.drawable.notmarked);
                }
                else{
                    tick.setButtonDrawable(R.drawable.marked);
                }
            }
        });


                signUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        email = emailTxt.getText().toString();
                        password = passwordTxt.getText().toString();
                        retryPassword = retryPasswordTxt.getText().toString();

                        if (tickClicks%2==0) {
                            if (checkEmail(email)) {
                                if (checkField(password) && checkField(retryPassword)) {
                                    if (password.equals(retryPassword)) {
                                        signUpNewUser();
                                    }
                                    else{
                                        Toast.makeText(SignUp.this, "Passwords are not equal!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                        else{

                            Toast.makeText(SignUp.this, "You need to accept the terms and conditions!", Toast.LENGTH_SHORT).show();
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

        Toast.makeText(SignUp.this, "Invalid password format!", Toast.LENGTH_SHORT).show();

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
        Toast.makeText(SignUp.this, "Invalid email format!", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void signUpNewUser(){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            LogClass.log("createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToView(Sex.class);
                        } else {
                            // If sign in fails, display a message to the user.
                            LogClass.log("createUserWithEmail:failure" + task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
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
