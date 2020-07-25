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

public class Choose extends AppCompatActivity implements UpdatingUI {
UserInfo randomUserInfo, mUserInfo;
ImageView photo;
CircleImageView icon;
ImageButton ok, no, info, back, message, left, right;
String mUserId, randomUserId;
int photoCount;
int photoNumber = 0;
TextView name, distance;
FirebaseDatabaseClient firebaseDatabaseClient;
int index = 0;
String formattedName = "", formattedDistance = "";

    private void findViews(){

        photo = findViewById(R.id.photo);
        icon = findViewById(R.id.icon);
        ok = findViewById(R.id.ok);
        no = findViewById(R.id.no);
        info = findViewById(R.id.info);
        back = findViewById(R.id.back);
        message = findViewById(R.id.message);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        name = findViewById(R.id.name);
        distance = findViewById(R.id.distance);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose);

        findViews();

        firebaseDatabaseClient = new FirebaseDatabaseClient();
        mUserId = firebaseDatabaseClient.getUserId();

        Gson gson = new Gson();
        SharedPreferencesEditor sharedPreferencesEditor = new SharedPreferencesEditor(getApplicationContext());
        if (sharedPreferencesEditor.checkPreferencesStorage(Constants.userInfo)){

            LogClass.log("sharedPreferences has saved userInfo");
            String json = sharedPreferencesEditor.getStringValue(Constants.userInfo);
            mUserInfo = gson.fromJson(json, UserInfo.class);


            firebaseDatabaseClient.filterDB(mUserInfo.getTarget(),this);

            LogClass.log("photoCount="+mUserInfo.getPhotosCount());
            if (mUserInfo.getPhotosCount()>0){
                downloadImage(icon,mUserId +0);
            }

        }



        loadNextUser();




    }

    @Override
    public void onBackPressed() {

    }






    private void showUser(int userIndex){
        randomUserInfo = firebaseDatabaseClient.getUser(userIndex);
        randomUserId = firebaseDatabaseClient.getUserId(userIndex);
        if (randomUserInfo!=null){
            LogClass.log("randomUser!=null");
            photoCount = randomUserInfo.getPhotosCount();

            formattedName = formatNameAndAge(randomUserInfo);
            formattedDistance = formatDistanceData(randomUserInfo);

            name.setText(formattedName);
            distance.setText(formattedDistance);

            if (photoCount>0){
                downloadImage(photo,randomUserId+0);
            }
        }
    }

     public void updateUI(){

        LogClass.log("updating choose ui");

        showUser(0);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextUser();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextUser();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToInfo();
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChat();
            }
        });


        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (photoNumber>0){
                    photoNumber--;
                    downloadImage(photo,randomUserId+photoNumber);
                }


            }
        });


        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (photoNumber<photoCount-1){
                    photoNumber++;
                    downloadImage(photo,randomUserId+photoNumber);
                }

            }
        });


    }

    private void loadNextUser(){

        LogClass.log("try to load next user, list size = " + firebaseDatabaseClient.getUsersCount());
        if (index<firebaseDatabaseClient.getUsersCount()-1) {
            index++;
            LogClass.log("loading next user, index = " + index);
            showUser(index);
        }


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
                LogClass.log("downloading image error: " +exception.getMessage());
            }
        });

    }



    private void goToInfo(){
        Intent intent = new Intent(this, Info.class);
        intent.putExtra(Constants.userInfo, mUserInfo);
        intent.putExtra(Constants.randomUserInfo, randomUserInfo);
        intent.putExtra(Constants.randomUserId, randomUserId);
        intent.putExtra(Constants.formattedName,formattedName);
        intent.putExtra(Constants.formattedNDistance,formattedDistance);
        startActivity(intent);
        finish();
    }

    private void goToChat(){
        Intent intent = new Intent(this, Chat.class);
        startActivity(intent);
        finish();
    }



    private String formatDistanceData(UserInfo user){
        return user.getDistance() + " " + getResources().getString(R.string.km);
    }


    private String formatNameAndAge(UserInfo user){
        String[] women = getResources().getStringArray(R.array.women);
        String[] men = getResources().getStringArray(R.array.men);

        Random random = new Random();
        String name = "";
        if (user!=null){
            if (user.getSex().equals(Constants.male)){
                int randomIndex = random.nextInt(men.length);
                name = men[randomIndex];
            }
            else{
                int randomIndex = random.nextInt(women.length);
                name = women[randomIndex];
            }
        }

        return name+ ", " + user.getAge() + " " + getResources().getString(R.string.years);
    }



}
