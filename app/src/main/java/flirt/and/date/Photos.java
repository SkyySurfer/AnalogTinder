package flirt.and.date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Photos extends AppCompatActivity {
    UserInfo userInfo;
    Button finish;
    ArrayList<String> photoList = new ArrayList<>();
    String description;
    static final int GALLERY_REQUEST = 1;
    GridLayout gridLayout ;
    ImageButton[] imgs ;
    int photosCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos);

        finish = findViewById(R.id.finish);
        gridLayout = findViewById(R.id.grid);


        imgs = new ImageButton[gridLayout.getChildCount()];
        for (int i=0;i<=imgs.length-1;i++){
            imgs[i] = (ImageButton) gridLayout.getChildAt(i);
            imgs[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                }
            });
        }



        userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.userInfo);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

    }

    private void openGallery(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;
        ImageButton img = imgs[photosCount];

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImageUri = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);

                        img.setBackgroundResource(0);
                        img.setImageBitmap(bitmap);

                    if (photosCount<imgs.length){
                        photosCount++;
                    }
                    imgs[photosCount-1].setClickable(false);
                    uploadImageToCloudStore(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }


    private void next(){
        userInfo.setDescription(description);
        userInfo.setDistance(Integer.toString(getRandomDistance()));
        userInfo.setPhotosCount(photosCount);


        FirebaseDatabaseClient firebaseDatabaseClient = new FirebaseDatabaseClient();
        firebaseDatabaseClient.saveToDB(userInfo);

        Intent intent = new Intent(this, Choose.class);

        SharedPreferencesEditor sharedPreferencesEditor = new SharedPreferencesEditor(getApplicationContext());

        Gson gson = new Gson();
        String json = gson.toJson(userInfo);

        sharedPreferencesEditor.putIntoStorage(Constants.userInfo,json);
        startActivity(intent);
        finish();
    }

    private int getRandomDistance(){
        Random random = new Random();
        return random.nextInt(5)+1;
    }



    private void uploadImageToCloudStore(Bitmap bitmap){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user =  mAuth.getCurrentUser();
        String userId = user.getUid();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference(userId+(photosCount-1));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                LogClass.log("failure to upload image to storage");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                LogClass.log("image was succesfully uploaded to storage");
            }
        });



    }

    @Override
    public void onBackPressed() {

    }



}
