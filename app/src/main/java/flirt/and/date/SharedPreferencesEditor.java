package flirt.and.date;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesEditor {

   static final String SETTINGS = "settings";
   static SharedPreferences sharedPreferences;

    SharedPreferencesEditor(Context context){
        sharedPreferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
    }

    void putIntoStorage(String key, String value) {
        SharedPreferences.Editor mySharedPreferencesEditor = sharedPreferences.edit();
        mySharedPreferencesEditor.putString(key, value);
        mySharedPreferencesEditor.apply();
    }

    void putIntoStorage(String key, int value) {
        SharedPreferences.Editor mySharedPreferencesEditor = sharedPreferences.edit();
        mySharedPreferencesEditor.putInt(key, value);
        mySharedPreferencesEditor.apply();
    }

    Boolean checkPreferencesStorage(String key) {
        if (sharedPreferences.contains(key)
                && !sharedPreferences.getString(key, "").isEmpty()) {
            return true;

        } else {
            return false;
        }
    }


    Boolean checkPreferencesStorageInt(String key) {
        if (sharedPreferences.contains(key)) {
            return true;
        } else {
            return false;
        }
    }

    String getStringValue(String key){
        return sharedPreferences.getString(key, "");
    }


    int getIntValue(String key){
        return sharedPreferences.getInt(key,0);
    }


    void deleteValue(String key){
        sharedPreferences.edit().remove(key).apply();
    }

}
