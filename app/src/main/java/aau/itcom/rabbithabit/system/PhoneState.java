package aau.itcom.rabbithabit.system;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;

public class PhoneState {
    private static final String TAG = "PhoneStateClass";
    public static float BATTERY_LIMIT = 10;

    public static float getBatteryLevelInPrc(Context context) {

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return (level / (float) scale) * 100;
    }

    public static String getConnectionType(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return "WIFI";
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return "MOBILE";
        } else {
            return "NO_CONNECTION";
        }

        return "ERROR";
    }
}