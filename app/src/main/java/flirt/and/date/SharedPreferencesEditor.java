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

    Boolean checkPreferencesStorage(String key) {
        if (sharedPreferences.contains(key)
                && !sharedPreferences.getString(key, "").isEmpty()) {
            return true;

        } else {
            return false;
        }
    }

    String getStringValue(String key){
        return sharedPreferences.getString(key, "");
    }


}
