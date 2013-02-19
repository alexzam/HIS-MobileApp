package az.his.android.hisapi;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckServerTask extends AsyncTask {

    private ApiListener listener;

    @Override
    protected Object doInBackground(Object... params) {
        try {
            listener = (ApiListener) params[1];
            int res = getResponseCodeOnly((String) params[0]);

            return res == 200;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        listener.handleApiResult(result);
    }

    private int getResponseCodeOnly(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getResponseCode();
    }

//    private String downloadUrl(String myurl) throws IOException {
//        InputStream is = null;
//
//        try {
//            URL url = new URL(myurl);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(10000);
//            conn.setConnectTimeout(15000);
//            conn.setRequestMethod("GET");
//            conn.setDoInput(true);
//            // Starts the query
//            conn.connect();
//            int response = conn.getResponseCode();
//            is = conn.getInputStream();
//
//            // Convert the InputStream into a string
//            return contentAsString;
//
//            // Makes sure that the InputStream is closed after the app is
//            // finished using it.
//        } finally {
//            if (is != null) {
//                is.close();
//            }
//        }
//    }
}
