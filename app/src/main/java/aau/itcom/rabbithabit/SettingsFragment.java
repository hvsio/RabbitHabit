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
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

import aau.itcom.rabbithabit.objects.Database;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class SettingsFragment extends Fragment {

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


        EditTextPreference username;
        SwitchPreference welcomeScreen;
        SwitchPreference photoDownload;
        SwitchPreference photoTake;
        ListPreference frequencyOfNotifications;
        ListPreference snooze;
        Preference feedback;
        private FirebaseAuth mAuth;
        private Database db;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_screen);

            db = Database.getInstance();
            mAuth = FirebaseAuth.getInstance();
            username = (EditTextPreference) findPreference("username");
            welcomeScreen = (SwitchPreference) findPreference("welcome_screen");
            photoDownload = (SwitchPreference) findPreference("download_photo");
            photoTake = (SwitchPreference) findPreference("take_photo");
            frequencyOfNotifications = (ListPreference) findPreference("frequency_of_notifications");
            snooze = (ListPreference) findPreference("snooze_time");
            feedback = findPreference("feedback");



            username.setDefaultValue(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
            changeUsername();


            welcomeScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    return false;
                }
            });


            photoTake.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {


                    return false;
                }
            });


            feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return false;
                }
            });

        }


public void changeUsername() {
    String newName = username.getText();

    FirebaseUser user = mAuth.getCurrentUser();
    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
            .setDisplayName(newName).build();
    assert user != null;
    user.updateProfile(profileUpdates);
    db.createNewUser(mAuth.getCurrentUser());
    username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
    //Toast.makeText(getApplicationContext(), "It may take a while.", Toast.LENGTH_SHORT).show();
}

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


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

