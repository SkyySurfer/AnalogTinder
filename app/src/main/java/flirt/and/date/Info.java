package flirt.and.date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Info extends AppCompatActivity {
    ImageButton message;
    CircleImageView icon;
    TextView name, description, distance;
    String randomUserId;
    UserInfo randomUserInfo, mUserInfo;
    String formattedName = "", formattedDistance = "";

    private void findViews(){
        icon = findViewById(R.id.icon);
        message = findViewById(R.id.message);
        name = findViewById(R.id.name);
        distance = findViewById(R.id.distance);
        description = findViewById(R.id.description);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        findViews();


        Gson gson = new Gson();
        SharedPreferencesEditor sharedPreferencesEditor = new SharedPreferencesEditor(getApplicationContext());
        if (sharedPreferencesEditor.checkPreferencesStorage(Constants.userInfo)){
            String json = sharedPreferencesEditor.getStringValue(Constants.userInfo);
            mUserInfo = gson.fromJson(json, UserInfo.class);
        }

        randomUserId = getIntent().getStringExtra(Constants.randomUserId);
        randomUserInfo = (UserInfo) getIntent().getSerializableExtra(Constants.randomUserInfo);
        formattedName = getIntent().getStringExtra(Constants.formattedName);
        formattedDistance = getIntent().getStringExtra(Constants.formattedNDistance);

        if (randomUserInfo!=null){
            LogClass.log("randomUser!=null");

            name.setText(formattedName);
            distance.setText(formattedDistance);
            description.setText(randomUserInfo.getDescription());

            if (mUserInfo.getPhotosCount()>0){
                downloadImage(icon, randomUserId+0); //иконка
            }
        }


        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(Chat.class);
            }
        });

    }


    @Override
    public void onBackPressed() {
        goToActivity(Choose.class);
    }


    private void downloadImage(final ImageView imageView, String path){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference(path);
        LogClass.log("path: " + path);

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                LogClass.log("image was downloaded, uri:" + uri.toString());

                Picasso.with(getApplicationContext()).load(uri).into(imageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                LogClass.log("downloading image error");
            }
        });

    }


    private void goToActivity(Class activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        finish();
    }



}
