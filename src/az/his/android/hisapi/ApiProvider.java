package az.his.android.hisapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Map;

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

    @SuppressWarnings("unchecked")
    public static void checkServer(Context context, ApiListener listener) {
        if (isNetworkReady(context)) {
            CheckServerTask checkServerTask = new CheckServerTask();
            checkServerTask.execute(url, listener);
        } else {
            listener.handleApiResult(false);
        }
    }

    @SuppressWarnings("unchecked")
    public static void getUsers(Context context, ApiListener listener){
        if(!isNetworkReady(context)){
            throw new IllegalStateException("Network went down");
        }
        FetchUsersTask fetchUsersTask = new FetchUsersTask();
        fetchUsersTask.execute(url, listener);
    }
}
