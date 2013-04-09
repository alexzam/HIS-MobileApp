package az.his.android.hisapi;

import android.os.AsyncTask;

abstract class HisApiTask extends AsyncTask {

    protected ApiListener listener;

    @Override
    protected void onPostExecute(Object result) {
        listener.handleApiResult(result);
    }
}
