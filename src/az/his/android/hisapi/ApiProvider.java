package az.his.android.hisapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ApiProvider {
    private static String url = null;

    public static void setUrl(String url) {
        ApiProvider.url = url;
    }

    public static boolean isNetworkReady(Context context){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void checkServer(Context context, ApiListener listener) {
        if (isNetworkReady(context)) {
            new CheckServerTask().execute(url, listener);
        } else {
            listener.handleApiResult(false);
        }
    }
}
