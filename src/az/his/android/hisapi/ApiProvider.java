package az.his.android.hisapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ApiProvider {
    private static String url = null;

    public static void setUrl(String url) {
        ApiProvider.url = url;
    }

    public static boolean isNetworkReady(Context context) {
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
    public static void getUsers(Context context, ApiListener listener) {
        if (!isNetworkReady(context)) {
            throw new IllegalStateException("Network went down");
        }
        FetchUsersTask fetchUsersTask = new FetchUsersTask();
        fetchUsersTask.execute(url, listener);
    }

    @SuppressWarnings("unchecked")
    public static void getCategories(Context context, ApiListener listener, Integer uid) {
        if (!isNetworkReady(context)) {
            throw new IllegalStateException("Network went down");
        }
        (new FetchCatsTask()).execute(url, listener, uid);
    }

    private static boolean syncActive = false;

    private static boolean checkServerSync(Context context) {
        syncActive = true;
        final Boolean[] ret = new Boolean[1];
        checkServer(context, new ApiListener() {
            @Override
            public void handleApiResult(Object result) {
                ret[0] = (Boolean) result;
                syncActive = false;
            }
        });
        while (syncActive) try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
        return ret[0];
    }
}
