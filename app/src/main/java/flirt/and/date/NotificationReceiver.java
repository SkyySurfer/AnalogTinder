package flirt.and.date;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationReceiver extends BroadcastReceiver {
    String[] texts;
    @Override
    public void onReceive(Context context, Intent intent) {

        formatPushTexts(context);
        Random random = new Random();
        int randId = random.nextInt(texts.length);
        sendPush(context,texts[randId]);

    }

    private void sendPush(Context context, String text) {

        String channelId = "channel_id";
        Intent notifyIntent = new Intent(context, MainActivity.class);
        notifyIntent.putExtra("popup", "popup2");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.icon_512)
                        .setContentTitle(text)
                        .setContentText(text)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

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

        LogClass.log("sending push ");

        notificationManager.notify(1, notification);
    }

    private void formatPushTexts(Context context) {
        texts = context.getResources().getStringArray(R.array.push_notifications);

        Random random = new Random();
        int randomNumber = random.nextInt(61) + 20;

        for (int i = 0; i <= texts.length - 1; i++) {
            if (texts[i].contains("XX")) {
                texts[i] = texts[i].replace("XX", Integer.toString(randomNumber));
            }
        }
    }

}
