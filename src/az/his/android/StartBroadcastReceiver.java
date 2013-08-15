package az.his.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        SyncService.checkState(context);
    }
}
