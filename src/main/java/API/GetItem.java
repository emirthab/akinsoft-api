package API;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import spark.Request;

public class GetItem implements ItemCallable {
    public GetItem(Statement stat) {
        this.stat = stat;
    }

    public Statement stat;

    @Override
    public String mainQuery() {
        return """
                 SELECT BLKODU,BARKODU,STOK_ADI,BIRIMI,KDV_ORANI,STOKKODU,ARA_GRUBU,ALT_GRUBU
                 FROM STOK as s
                 WHERE
                """;
    }

    @Override
    public JSONObject getResponse(Request req) throws JSONException, SQLException {
        String query = getQuery(req);
        ResultSet res = stat.executeQuery(query);
        JSONArray items = new JSONArray();
        while (res.next()) {
            JSONObject item = parse(res);
            items.put(item);
        }
        return new JSONObject().put("items", items);
    }

    public JSONObject parse(ResultSet res) throws JSONException, SQLException {
        JSONObject object = new JSONObject();
        object.put("blstcode", res.getInt("BLKODU"));
        object.put("barcode", res.getString("BARKODU"));
        object.put("name", res.getString("STOK_ADI"));
        object.put("unit", res.getString("BIRIMI"));
        object.put("tax", res.getDouble("KDV_ORANI"));
        object.put("sku", res.getString("STOKKODU"));
        object.put("intermediate_group", res.getString("ARA_GRUBU"));
        object.put("alt_group", res.getString("ALT_GRUBU"));
        return object;
    }

}
