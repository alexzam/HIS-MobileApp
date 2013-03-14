package az.his.android.hisapi;

import java.io.IOException;

class PostTransactionsTask extends HisApiTask {
    @Override
    protected Object doInBackground(Object... params) {
        listener = (ApiListener) params[1];

        try {
            int code = getResponseCodeOnly(params[0] + "/api/trans", "POST", (String) params[2]);
            return code == 201;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }
}
