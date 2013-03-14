package az.his.android.hisapi;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

class FetchUsersTask extends HisApiTask {
    @Override
    protected Object doInBackground(Object... params) {
        try {
            listener = (ApiListener) params[1];

            Document doc = getXmlDocument(params[0] + "/api/users", "GET", null);
            NodeList userNodes = doc.getElementsByTagName("user");
            Map<String, Integer> users = new HashMap<String, Integer>();

            for (int i = 0; i < userNodes.getLength(); i++) {
                NamedNodeMap attributes = userNodes.item(i).getAttributes();
                String name = attributes.getNamedItem("name").getNodeValue();
                int id = Integer.parseInt(attributes.getNamedItem("id").getNodeValue());
                users.put(name, id);
            }
            return users;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
