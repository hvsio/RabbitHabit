package aau.itcom.rabbithabit.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;
import java.util.Set;

import aau.itcom.rabbithabit.R;


public class SettingsFragment extends Fragment {
    public static final String SETTINGS = "settings";
    public static final String USERNAME = "username";
    public static final String WELCOME_SCREEN = "welcome_screen";
    public static final String DOWNLOAD_PHOTO = "download_photo";
    public static final String TAKE_PHOTO = "take_photo";
    public static final String NOTIFICATION_FREQUENCY = "notification_frequency";
    public static final String SNOOZE_TIME = "snooze_time";
    public static final String FEEDBACK = "feedback";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.settingsFrameLayout, new SettingsPreferenceFragment())
                .commit();
    }

    public static class SettingsPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        SharedPreferences sharedPreferences;

        EditTextPreference username;
        SwitchPreference welcomeScreen;
        SwitchPreference photoDownload;
        SwitchPreference photoTake;
        MultiSelectListPreference frequencyOfNotifications;
        //ListPreference snooze;
        Preference feedback;
        private FirebaseAuth mAuth;
        private FirebaseAnalytics mFirebaseAnalytics;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_screen);
            mAuth = FirebaseAuth.getInstance();
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

            sharedPreferences = getActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();

            username = (EditTextPreference) findPreference(USERNAME);
            welcomeScreen = (SwitchPreference) findPreference(WELCOME_SCREEN);
            photoDownload = (SwitchPreference) findPreference(DOWNLOAD_PHOTO);
            photoTake = (SwitchPreference) findPreference(TAKE_PHOTO);
            frequencyOfNotifications = (MultiSelectListPreference) findPreference(NOTIFICATION_FREQUENCY);
            //snooze = (ListPreference) findPreference(SNOOZE_TIME);
            feedback = findPreference(FEEDBACK);


            username.setDefaultValue(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
            changeUsername();

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

            frequencyOfNotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Set<String> selections = (Set<String>) newValue;
                    updateNotifications(selections);
                    Toast.makeText(getActivity(), selections.toString(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(getActivity(), newValue.toString(), Toast.LENGTH_LONG).show();
                    return true;
                }
            });

            //feedback.setOnPreferenceClickListener(new On);
        }

        private void updateNotifications(Set<String> selections) {
            Log.i("UPDATE NOTIFICATIONS", "Inside");

            if (selections.contains("Morning - 9:00am (GMT+2)")) {
                Log.i("UPDATE NOTIFICATIONS", "Morning is checked");
                mFirebaseAnalytics.setUserProperty("notification_frequency", "M");
                if (selections.contains("Morning - 9:00am (GMT+2)") && selections.contains("Afternoon - 2:00pm (GMT+2)")) {
                    Log.i("UPDATE NOTIFICATIONS", "Morning & Afternoon is checked");
                    mFirebaseAnalytics.setUserProperty("notification_frequency", "MA");
                    if (selections.contains("Morning - 9:00am (GMT+2)") && selections.contains("Afternoon - 2:00pm (GMT+2)") && selections.contains("Evening - 7:00pm (GMT+2)"))
                        mFirebaseAnalytics.setUserProperty("notification_frequency", "MAE");
                }
                if (selections.contains("Morning - 9:00am (GMT+2)") && selections.contains("Evening - 7:00pm (GMT+2)") && !selections.contains("Evening - 7:00pm (GMT+2)")) {
                    Log.i("UPDATE NOTIFICATIONS", "Morning & Evening is checked");
                    mFirebaseAnalytics.setUserProperty("notification_frequency", "ME");
                }
            }

            if (!selections.contains("Morning - 9:00am (GMT+2)") && selections.contains("Afternoon - 2:00pm (GMT+2)")) {
                mFirebaseAnalytics.setUserProperty("notification_frequency", "A");
                if (selections.contains("Afternoon - 2:00pm (GMT+2)") && selections.contains("Evening - 7:00pm (GMT+2)"))
                    mFirebaseAnalytics.setUserProperty("notification_frequency", "AE");
            }

            if (!selections.contains("Morning - 9:00am (GMT+2)") && !selections.contains("Afternoon - 2:00pm (GMT+2)") && selections.contains("Evening - 7:00pm (GMT+2)"))
                mFirebaseAnalytics.setUserProperty("notification_frequency", "E");

            if (!selections.contains("Morning - 9:00am (GMT+2)") && !selections.contains("Afternoon - 2:00pm (GMT+2)") && !selections.contains("Evening - 7:00pm (GMT+2)"))
                mFirebaseAnalytics.setUserProperty("notification_frequency", "");
        }

        public void changeUsername() {
            String newName = username.getText();

            FirebaseUser user = mAuth.getCurrentUser();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName).build();
            assert user != null;
            user.updateProfile(profileUpdates);
            //db.createNewUser(mAuth.getCurrentUser());
            username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            //Toast.makeText(getApplicationContext(), "It may take a while.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

/*

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
*/
    }
}

