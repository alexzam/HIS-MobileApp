package az.his.android.hisapi;

import java.io.IOException;

class CheckServerTask extends HisApiTask {

    @Override
    protected Object doInBackground(Object... params) {
        try {
            listener = (ApiListener) params[1];
            int res = getResponseCodeOnly((String) params[0], "GET", null);

            return res == 200;
        } catch (IOException e) {
            return false;
        }
    }
}
