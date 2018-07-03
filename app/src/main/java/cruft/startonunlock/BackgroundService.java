package cruft.startonunlock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

public class BackgroundService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

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
                Log.w("CRUFT", "Screen went on");
            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                Log.w("CRUFT","Screen went off");
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
            BroadcastReceiver mReceiver = new ScreenReceiver();
            registerReceiver(mReceiver, filter);

            Log.w("CRUFT", "Creating broadcast receiver");
        }
    }
}
