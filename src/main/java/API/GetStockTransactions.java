package API;

import static spark.Spark.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Utils.Json;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetStockTransactions {
    public GetStockTransactions(Statement stat) {
        this.stat = stat;
    }

    public Statement stat;

    public void handle() {
        get("/stocktransactions", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                response.type("application/json");
                try {
                    JSONArray resp = getResponse(request.queryParams("datestart"), request.queryParams("dateend"));
                    return Json.getPretty(resp);
                } catch (JSONException | SQLException e) {
                    return new JSONObject(String.format("{'response':'%s'}", e.toString()));
                }
            }
        });
    }

    public JSONArray getResponse(String dateStart, String dateEnd) throws SQLException {
        String query = String.format(
                """
                        SELECT STOKHR.TARIHI,STOKHR.BLSTKODU,STOKHR.TUTAR_TURU,STOKHR.EVRAK_NO,STOKHR.KPB_TUTARI,STOKHR.KPB_FIYATI,STOKHR.MIKTARI,STOK.BIRIMI,STOK.STOK_ADI,STOK.BLKODU,STOK.BARKODU,STOK.KDV_ORANI
                        FROM STOKHR,STOK
                        WHERE STOK.BLKODU = STOKHR.BLSTKODU AND STOKHR.TARIHI >= '%s' AND STOKHR.TARIHI <= '%s'
                        """,
                dateStart, dateEnd);
        return parse(query);
        
    }

    public JSONArray parse(String query) throws SQLException {
        ResultSet res = stat.executeQuery(query);
        JSONArray items = new JSONArray();
        while (res.next()) {
            JSONObject item = new JSONObject();
            item.put("date", res.getString("TARIHI"));
            item.put("blstcode", res.getInt("BLSTKODU"));
            item.put("round", res.getInt("TUTAR_TURU"));
            item.put("doc_id", res.getString("EVRAK_NO"));
            item.put("price_total", res.getDouble("KPB_TUTARI"));
            item.put("price", res.getDouble("KPB_FIYATI"));
            item.put("quantity", res.getDouble("MIKTARI"));
            item.put("unit", res.getString("BIRIMI"));
            item.put("name", res.getString("STOK_ADI"));
            item.put("barcode", res.getString("BARKODU"));
            item.put("tax", res.getInt("KDV_ORANI"));
            items.put(item);
        }
        return items;
    }
}
