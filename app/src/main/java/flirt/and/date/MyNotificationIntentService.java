package flirt.and.date;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Random;


public class MyNotificationIntentService extends IntentService {
    String[] texts;

    public MyNotificationIntentService(){
        super("MyNotificationIntentService");

    }


    @Override
    protected void onHandleIntent(Intent intent) {

        formatPushTexts();
        Random random = new Random();
        int randId = random.nextInt(texts.length);

        SharedPreferencesEditor sharedPreferencesEditor = new SharedPreferencesEditor(this);
        if (sharedPreferencesEditor.checkPreferencesStorageInt(Constants.notificationId)){
            int id = sharedPreferencesEditor.getIntValue(Constants.notificationId) +1;
            sharedPreferencesEditor.putIntoStorage(Constants.notificationId, id);
            LogClass.log("shpref has not id, trying to send push with id "+id);

            sendPush(texts[randId],id);


        }
        else{
            int id = 1 ;
            sharedPreferencesEditor.putIntoStorage(Constants.notificationId, id);
            LogClass.log("shpref has id, trying to send push with id "+id);


            sendPush(texts[randId],id);
        }
        stopSelf();
    }


    private void sendPush(String text, int id) {

        String channelId = "channel_id";
        Intent notifyIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.icon_512)
                        .setContentTitle(text)
                        .setContentText(text)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }


        Notification notification = builder.build();

        LogClass.log("sending push with id "+id);

        notificationManager.notify(id, notification);
    }

    private void formatPushTexts() {
        texts = getResources().getStringArray(R.array.push_notifications);

        Random random = new Random();
        int randomNumber = random.nextInt(61) + 20;

        for (int i = 0; i <= texts.length - 1; i++) {
            if (texts[i].contains("XX")) {
                texts[i] = texts[i].replace("XX", Integer.toString(randomNumber));
            }
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
}
