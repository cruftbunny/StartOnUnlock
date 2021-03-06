package cruft.startonunlock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;

public class BackgroundService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private BroadcastReceiver screenReceiver = null;

    @Override
    public void onCreate() {
        // create background handler to avoid CPU blocking
        HandlerThread thread = new HandlerThread("BackgroundService",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    public static class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                Log.w("CRUFT", "Screen on. Launch chosen app");

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                String appName = sp.getString("appName", null);

                if (appName != null) {
                    Intent LaunchAppIntent = context.getPackageManager().getLaunchIntentForPackage(appName);
                    context.startActivity(LaunchAppIntent);
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;
        mServiceHandler.sendMessage(message);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(this.screenReceiver != null) {
            unregisterReceiver(this.screenReceiver);
            Log.w("CRUFT", "Unregistering broadcast receiver");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final class ServiceHandler extends Handler {

        private ServiceHandler(Looper looper) {
            super(looper);
        }

        // CPU-blocking activity
        @Override
        public void handleMessage(Message msg) {

            // start BroadcastReceiver
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            screenReceiver = new ScreenReceiver();

            try {
                registerReceiver(screenReceiver, filter);
                Log.w("CRUFT", "Registering broadcast receiver");
            }
            catch (IllegalArgumentException failedToRegister) {
                Log.w("CRUFT", "Failed to register broadcast receiver");
            }
        }
    }
}
