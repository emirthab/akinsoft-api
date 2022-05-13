package API;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import spark.Request;

public class GetPrice implements ItemCallable {
    public GetPrice(Statement stat) {
        this.stat = stat;
    }

    public Statement stat;

    @Override
    public String mainQuery() {
        return """
                  SELECT sf.BLKODU, sf.BLSTKODU, sf.TANIMI, sf.FIYATI, sf.ALIS_SATIS, sf.FIYAT_NO, s.BARKODU, s.BLKODU
                  FROM STOK as s, STOK_FIYAT as sf
                  WHERE s.BLKODU = sf.BLSTKODU AND
                """;
    };

    @Override
    public JSONObject getResponse(Request req) throws JSONException, SQLException {
        ResultSet res = stat.executeQuery(getQuery(req));
        JSONArray items = new JSONArray();
        while (res.next()) {
            System.out.println("1");
            JSONObject item = new JSONObject();
            for (int i = 0; i < items.length(); i++) {
                System.out.println("2");
                JSONObject o = items.getJSONObject(i);
                String barcode = o.getString("barcode");
                System.out.println(barcode);
                System.out.println(res.getString("BARKODU"));
                if (barcode.contains(res.getString("BARKODU"))) {
                    item = o;
                    System.out.println("3");
                }
            }
            if (!item.has("barcode")) {
                item.put("blstcode", res.getInt("BLSTKODU"));
                item.put("barcode", res.getString("BARKODU"));
                item.put("sell_prices", new JSONArray());
                item.put("buy_prices", new JSONArray());
                System.out.println("4");
            }
            JSONObject price = new JSONObject();
            price.put("defination", res.getString("TANIMI"));
            price.put("id", res.getInt("FIYAT_NO"));
            price.put("price", res.getDouble("FIYATI"));
            // 1 for buy 2 for sell
            if (res.getInt("ALIS_SATIS") == 1) {
                item.getJSONArray("buy_prices").put(price);
            } else if (res.getInt("ALIS_SATIS") == 2) {
                item.getJSONArray("sell_prices").put(price);
            }
            boolean found = false;
            for (int i = 0; i < items.length(); i++) {
                if(items.getJSONObject(i).getString("barcode") == item.getString("barcode")){
                    found = true;
                }
            }            
            if(found == false) items.put(item);
        }
        return new JSONObject().put("items", items);
    }

}
