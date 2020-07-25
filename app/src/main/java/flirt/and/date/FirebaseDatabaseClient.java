package flirt.and.date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseDatabaseClient {
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, mainRef;
    private UserInfo userInfo, currentUserInfo;
    private String userId;
    private ArrayList<UserInfo> users = new ArrayList<>();
    private ArrayList<String> userIds = new ArrayList<>();
    private Boolean isUpdated = false;


    public String getUserId(int index) {
        if (index<=userIds.size()-1) {
            return userIds.get(index);
        }
        else{
            return "";
        }
    }

    public int getUsersCount() {
        return users.size();
    }

    public UserInfo getUser(int index){
        if (index<=users.size()-1){
            return users.get(index);
        }
        return null;
    }

    public UserInfo getCurrentUserInfo() {
        return currentUserInfo;
    }

    public void setCurrentUserInfo(UserInfo currentUserInfo) {
        this.currentUserInfo = currentUserInfo;
    }

    public String getUserId() {
        return userId;
    }



    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public FirebaseDatabaseClient(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database =  FirebaseDatabase.getInstance();
        FirebaseUser user =  mAuth.getCurrentUser();
        userId = user.getUid();
        userRef =  database.getReference().child(Constants.database_users).child(userId);
        mainRef = database.getReference();
    }

    void saveToDB(UserInfo profile){

        userRef.setValue(profile);

    }



    void filterDB(String target, final UpdatingUI updatingUIActivity){

        final Query myQuery;

        if (target.equals(Constants.boyfriend)){
            myQuery = mainRef.child(Constants.database_users)
                    .orderByChild(Constants.sex_filter).equalTo(Constants.male);
        }
        else{
            myQuery = mainRef.child(Constants.database_users)
                    .orderByChild(Constants.sex_filter).equalTo(Constants.female);
        }



        myQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });




        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {


                    UserInfo randomUserInfo = postSnapshot.getValue(UserInfo.class);
                    String randomUserId = postSnapshot.getKey();


                    if (!userId.equals(randomUserId)){

                        userIds.add(randomUserId);
                        users.add(randomUserInfo);

                        if (!isUpdated) {
                            updatingUIActivity.updateUI();
                            isUpdated = true;
                        }

                    }

                    LogClass.log("filtered database has " + dataSnapshot.getChildrenCount() +" users");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
