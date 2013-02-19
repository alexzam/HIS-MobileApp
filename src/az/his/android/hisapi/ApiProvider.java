package az.his.android.hisapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ApiProvider {
    private static String url = null;

    public static void setUrl(String url) {
        ApiProvider.url = url;
    }

    public static void checkServer(Context context, ApiListener listener) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new CheckServerTask().execute(url, listener);
        } else {
            listener.handleApiResult(false);
        }
    }
}
