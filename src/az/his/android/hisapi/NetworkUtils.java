package az.his.android.hisapi;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

abstract class NetworkUtils {

    protected static int getResponseCodeOnly(String urlStr, String method, String body) throws IOException {
        HttpURLConnection conn = makeConnection(urlStr, method, body);
        return conn.getResponseCode();
    }

    protected static Document getXmlDocument(String url, String method, String body)
            throws IOException, ParserConfigurationException, SAXException {
        InputStream is = null;

        try {
            HttpURLConnection conn = makeConnection(url, method, body);
            int response = conn.getResponseCode();

            if (response != 200) throw new ProtocolException("Server returned: " + conn.getResponseMessage());
            is = conn.getInputStream();

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(is);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected static HttpURLConnection makeConnection(String url, String method, String body) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod(method);
        conn.setDoInput(true);

        if (body != null) {
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "text/xml");
            conn.getOutputStream().write(body.getBytes());
        }

        conn.connect();
        return conn;
    }
}
