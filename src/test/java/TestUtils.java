import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import spark.QueryParamsMap;
import spark.Request;

class request extends Request {
    Map<String, String[]> maps;

    public request(Map<String, String[]> map) {
        this.maps = map;
    }

    class customMap extends QueryParamsMap {
        public Map<String, String[]> maps;

        public customMap(Map<String, String[]> map) {
            this.maps = map;
        }

        @Override
        public Map<String, String[]> toMap() {
            return this.maps;
        }
    }

    @Override
    public QueryParamsMap queryMap() {
        return new customMap(this.maps);
    }
}

public class TestUtils {
    Request customRequestBarcode = new request(new HashMap<String, String[]>() {
        {
            String[] s = { "8413240602088" };
            put("barcode", s);
        }
    });
    CloseableHttpClient httpclient = HttpClients.createDefault();

    public Statement createTempDatabaseStatement() throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:sqlite:./src/test/resources/.tmp.db");
        Statement stat = con.createStatement();
        return stat;
    }

    public String readFile(String file) throws IOException {
        String line;
        BufferedReader Br = new BufferedReader(new FileReader(file));
        StringBuilder Sb = new StringBuilder();
        while ((line = Br.readLine()) != null) {
            Sb.append(line + '\n');
        }
        Br.close();
        return Sb.toString();
    }

    public String responseGet(String url) throws ClientProtocolException, IOException {
        String result = "";
        HttpGet request = new HttpGet(url);
        request.addHeader("content-type", "application/json");
        CloseableHttpResponse response = httpclient.execute(request);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            // return it as a String
            result = EntityUtils.toString(entity);
        }
        return result;
    }
}
