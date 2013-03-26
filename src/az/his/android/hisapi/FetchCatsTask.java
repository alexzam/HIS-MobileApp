package az.his.android.hisapi;

import android.util.SparseArray;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

class FetchCatsTask extends HisApiTask {
    @Override
    protected Object doInBackground(Object... params) {
        try {
            listener = (ApiListener) params[1];

            Document doc = getXmlDocument(params[0] + "/api/cats?uid=" + params[2], "GET", null);
            NodeList catNodes = doc.getElementsByTagName("category");
            SparseArray<String> cats = new SparseArray<String>();

            for (int i = 0; i < catNodes.getLength(); i++) {
                NamedNodeMap attributes = catNodes.item(i).getAttributes();
                String name = attributes.getNamedItem("name").getNodeValue();
                int id = Integer.parseInt(attributes.getNamedItem("id").getNodeValue());
                cats.put(id, name);
            }
            return cats;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
