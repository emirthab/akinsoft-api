package API;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import static spark.Spark.*;

import Utils.Json;
import spark.Request;
import spark.Response;
import spark.Route;


public interface ItemCallable {
    public String mainQuery();
    public JSONObject getResponse(Request req) throws JSONException, SQLException;

    public default void handle(String path) {
        get("/"+path, new Route() {
            @Override
            public Object handle(Request request, Response response) {
                JSONObject resp = new JSONObject();
                try {
                    resp = getResponse(request);
                    if (resp.getJSONArray("items").length() > 0) {
                        resp.put("response", "ok");
                    } else {
                        resp.put("response", "Ürün bulunamadı.");
                    }
                } catch (JSONException | SQLException e) {
                    resp.put("response", e.toString());
                }
                response.type("application/json");
                return Json.getPretty(resp);
            }
        });
    }

    public default String getQuery(Request req){
        String query = mainQuery();
        int counter = 0;        
        for (var entry : req.queryMap().toMap().entrySet()) {
            if (counter != 0)
                query += " AND ";
            counter++;
            switch (entry.getKey()) {
                case "barcode":
                    query += paramParser(entry.getValue(), new fromBarcode());
                    break;

                case "sku":
                    query += paramParser(entry.getValue(), new fromSku());
                    break;
            }
        }
        return query;
    }
    interface Callable {
        public String call(String param);
    }

    class fromBarcode implements Callable {
        public String call(String barcode) {
            String query = String.format("""
                     (EXISTS (SELECT BARKODU FROM STOK_BARKOD WHERE BLSTKODU = s.BLKODU AND BARKODU = '%s')
                     OR s.BARKODU = '%s')
                    """, barcode, barcode);
            return query;
        }
    }

    class fromSku implements Callable {
        public String call(String sku) {
            String query = String.format("""
                         s.STOKKODU = '%s'
                    """, sku);
            return query;
        }
    }

    public default String paramParser(String[] values, Callable callback) {
        String query = " ( ";
        for (String val : values) {
            query += callback.call(val);
            if (val != values[values.length - 1])
                query += " OR ";
        }
        query += " ) ";
        return query;
    }
    
}
