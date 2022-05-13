package API;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import spark.Request;

public class GetItemWithPrice implements ItemCallable {
    public GetItemWithPrice(Statement stat) {
        this.stat = stat;
    }

    public Statement stat;

    @Override
    public String mainQuery() {
        return """
                 SELECT s.BARKODU, s.BLKODU, sf.FIYATI ,s.STOK_ADI ,s.BIRIMI ,s.KDV_ORANI ,s.STOKKODU ,s.ARA_GRUBU ,s.ALT_GRUBU
                 FROM STOK as s, STOK_FIYAT as sf
                 WHERE s.BLKODU = sf.BLSTKODU
                 AND sf.ALIS_SATIS = 2 AND sf.FIYAT_NO = 1
                 AND
                """;
    }

    public JSONObject getResponse(Request req) throws JSONException, SQLException {
        String query = getQuery(req);
        System.out.println(query);
        ResultSet res = stat.executeQuery(query);
        JSONArray items = new JSONArray();
        while (res.next()) {
            // Firstly getting only item
            JSONObject item = new GetItem(stat).parse(res);
            // Adding price to item object
            item.put("price", res.getDouble("FIYATI"));
            items.put(item);
        }
        return new JSONObject().put("items", items);
    }

}
