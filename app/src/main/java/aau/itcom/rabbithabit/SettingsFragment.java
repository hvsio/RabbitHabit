package aau.itcom.rabbithabit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;


public class SettingsFragment extends Fragment {
    public static final String SETTINGS = "settings" ;

    public static final String USERNAME = "username";
    public static final String WELCOME_SCREEN = "welcome_screen" ;
    public static final String DOWNLOAD_PHOTO = "download_photo" ;
    public static final String TAKE_PHOTO = "take_photo" ;
    public static final String NOTIFIXATION_FREQUENCY = "frequency_of_notifications" ;
    public static final String SNOOZE_TIME = "snooze_time" ;
    public static final String FEEDBACK = "feedback" ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.settingsFrameLayout, new SettingsPreferenceFragment())
                .commit();
    }

    public static class SettingsPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

        SharedPreferences sharedPreferences;

        EditTextPreference username;
        SwitchPreference welcomeScreen;
        SwitchPreference photoDownload;
        SwitchPreference photoTake;
        ListPreference frequencyOfNotifications;
        ListPreference snooze;
        Preference feedback;
        private FirebaseAuth mAuth;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_screen);

            mAuth = FirebaseAuth.getInstance();
            sharedPreferences = getActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();

            username = (EditTextPreference) findPreference(USERNAME);
            welcomeScreen = (SwitchPreference) findPreference(WELCOME_SCREEN);
            photoDownload = (SwitchPreference) findPreference(DOWNLOAD_PHOTO);
            photoTake = (SwitchPreference) findPreference(TAKE_PHOTO);
            frequencyOfNotifications = (ListPreference) findPreference(NOTIFIXATION_FREQUENCY);
            snooze = (ListPreference) findPreference(SNOOZE_TIME);
            feedback = findPreference(FEEDBACK);


            username.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());

            photoDownload.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (photoDownload.isChecked()) {
                        editor.putBoolean(DOWNLOAD_PHOTO, true);
                        Log.i(DOWNLOAD_PHOTO, "Switch is checked");
                    } else {
                        editor.putBoolean(DOWNLOAD_PHOTO, false);
                        Log.i(DOWNLOAD_PHOTO, "Switch is NOT checked");
                    }
                    editor.apply();
                    return false;
                }
            });

            photoTake.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (photoTake.isChecked()) {
                        editor.putBoolean(TAKE_PHOTO, true);
                        Log.i(TAKE_PHOTO, "Switch is checked");
                    } else {
                        editor.putBoolean(TAKE_PHOTO, false);
                        Log.i(TAKE_PHOTO, "Switch is NOT checked");
                    }
                    editor.apply();
                    return false;
                }
            });

            welcomeScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (welcomeScreen.isChecked()) {
                        editor.putBoolean(WELCOME_SCREEN, true);
                        Log.i(WELCOME_SCREEN, "Switch is checked");
                    } else {
                        editor.putBoolean(WELCOME_SCREEN, false);
                        Log.i(WELCOME_SCREEN, "Switch is NOT checked");
                    }
                    editor.apply();
                    return false;
                }
            });

        }




        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("username")) {
                username.getText();
               // Preference pref = findPreference(key);
//                FirebaseUser user = mAuth.getCurrentUser();
//                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                        .setDisplayName(String.valueOf(username)).build();
//                if (user != null) {
//                    user.updateProfile(profileUpdates);
//                }
                username.setText(String.valueOf(username));
               // pref.setSummary(sharedPreferences.getString(key, String.valueOf(username)));

            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }


        public static void sendFeedback(Context context) {
            String body = null;
            try {
                body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
                body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                        Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                        "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
            } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@androidhive.info"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Query from android app");
            intent.putExtra(Intent.EXTRA_TEXT, body);
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.email_input)));
        }
    }

}

