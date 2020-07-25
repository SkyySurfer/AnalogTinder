package flirt.and.date;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class PushNotifications  {
    String[] texts;
    private static String CHANNEL_ID = "channel_id";
    private static final int NOTIFY_ID = 100;

    private void init(Context context){
        texts = context.getResources().getStringArray(R.array.push_notifications);

        Random random = new Random();
        int randomNumber = random.nextInt(61)+20;

        for (int i = 0; i<= texts.length-1; i++){
            if (texts[i].contains("XX")) {
                texts[i] = texts[i].replace("XX",Integer.toString(randomNumber));
            }
        }
    }

    private void sendPush(Context context){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_512)
                        .setContentText("Пора покормить кота")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFY_ID, builder.build());
    }


    private void getCurrentTime(){
        Date currentDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);
        String[] array = timeText.split(":");
        if (array.length==3){
            int hours = Integer.parseInt(array[0]);
            int minutes = Integer.parseInt(array[1]);
        }
    }
}
