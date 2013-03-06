package az.his.android.hisapi;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

class FetchCatsTask extends HisApiTask {
    @Override
    protected Object doInBackground(Object... params) {
        try {
            listener = (ApiListener) params[1];

            Document doc = getXmlDocument(params[0] + "/api/cats?uid=" + params[2]);
            NodeList catNodes = doc.getElementsByTagName("category");
            Map<Integer, String> cats = new HashMap<Integer, String>();

            for (int i = 0; i < catNodes.getLength(); i++) {
                NamedNodeMap attributes = catNodes.item(i).getAttributes();
                String name = attributes.getNamedItem("name").getNodeValue();
                int id = Integer.parseInt(attributes.getNamedItem("id").getNodeValue());
                cats.put(id, name);
            }
            return cats;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
