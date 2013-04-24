package az.his.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import az.his.android.hisapi.ApiListener;
import az.his.android.hisapi.ApiProvider;
import az.his.android.persist.DbHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SyncService extends Service implements ApiListener {
    public static final String LOGTAG = "HIS-Sync-Service";
    private Timer timer;
    private TimerTask task;
    private DbHelper dbHelper;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(LOGTAG, "Service created");
        setTimer();
    }

    private void setTimer() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (timer == null) {
            Log.d(LOGTAG, "Creating timer");
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    sync();
                }
            };
        }

        Calendar cal = Calendar.getInstance();
        int hour = 0;
        int minute = 31;

        Log.d(LOGTAG, "Setting timer to " + hour + ":" + minute);
        cal.set(Calendar.HOUR_OF_DAY, hour); // TODO Get from properties
        cal.set(Calendar.MINUTE, minute);

        if (cal.before(Calendar.getInstance())) {
            Log.d(LOGTAG, "  ... of tomorrow");
            cal.add(Calendar.DATE, 1);
        }

        Date time = cal.getTime();
        timer.schedule(task, time);
        Log.d(LOGTAG, "Timer set to " + time);
    }

    private void sync() {
        Log.i(LOGTAG, "Synchronization started!");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (dbHelper == null) dbHelper = new DbHelper(getApplicationContext());
        if (dbHelper.getTransactionNum() < 1) {
            setTimer();
            return;
        }
        ApiProvider.postTransactions(this, this, sharedPref.getInt("int_userid", -1), dbHelper.getTransactions(), false);
    }

    @Override
    public void handleApiResult(Object result) {
        // TODO Clever WiFi management
        if (result == Boolean.TRUE) {
            PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, EnterTransactionActivity.class), 0);
            // TODO correct intent

            Notification notification = new Notification(
                    android.R.drawable.stat_notify_sync,
                    "Синхронизация удалась",
                    System.currentTimeMillis());

            notification.setLatestEventInfo(this,
                    "Синхронизация удалась",        // TODO Strings to res
                    "Транзакции отправлены на сервер",
                    intent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);

            dbHelper.cleanTransactions();
        }
        setTimer();
        dbHelper = null;
    }
}
