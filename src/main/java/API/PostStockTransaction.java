package API;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import static spark.Spark.*;

import Utils.Json;
import spark.Request;
import spark.Response;
import spark.Route;

public class PostStockTransaction {
    public PostStockTransaction(Statement stat) {
        this.stat = stat;
    }

    public Statement stat;

    public void handle() {
        post("/stocktransaction", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                JSONArray items = new JSONArray(request.body());
                JSONObject resp = new JSONObject();
                try {
                    execute(items, Integer.parseInt(request.queryParams("round")));
                    resp.put("response", "ok");
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                    resp.put("response", e.toString());
                }
                return Json.getPretty(resp);
            }
        });
    }

    public void execute(JSONArray items, int round) throws SQLException {
        int stokHrGen = getStokHrGen() + 1;
        int docId = getDocIdGen() + 1;
        String docIdText = "SF" + docId;
        for (int index = 0; index < items.length(); index++) {
            JSONObject obj = items.getJSONObject(index);
            int quantity = obj.getInt("quantity");
            Double kpb = obj.getDouble("price") / ((100 + obj.getInt("tax")) / 100);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String date = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(timestamp);
            String query = String.format(
                    """
                            INSERT INTO STOKHR VALUES (%s, %s, %s, '%s', '%s', null, %s, %s, %s, null, null, null, 'GENEL', 0, 1, null, 0, null, null, null, %s, %s, %s,
                            %s, %s, null, null, null, null, null, 0, 0, 0, '%s', null, null, null, null, null, null, null, 1, '%s',
                            null, null, null, null, null, null, null, null, null, '%s', null);
                            """,
                    stokHrGen, obj.getInt("blstcode"), round, date, docIdText, quantity, kpb * quantity, kpb, quantity,
                    round == 1 * quantity, round == 0 * quantity, round == 1 * kpb, round == 0 * kpb, "SYSDBA",
                    obj.getString("unit"), date);
            stat.executeUpdate(query);
            if (index != items.length() - 1) {
                stokHrGen += 1;
            }
        }
        updateStokHrGen(stokHrGen);
        updateDocIdGen(docId);
    }

    public int getStokHrGen() throws SQLException {
        int maxBl = 0;
        ResultSet res = stat.executeQuery("SELECT GEN_VALUE FROM GEN_IDT WHERE GEN_NAME = 'STOKHR_GEN'");
        while (res.next()) {
            maxBl = res.getInt(1);
        }
        return maxBl;
    }

    public void updateStokHrGen(int blcode) throws SQLException {
        String Query = String.format("UPDATE GEN_IDT SET GEN_VALUE = %s WHERE GEN_NAME = 'STOKHR_GEN'", blcode);
        stat.executeUpdate(Query);
    }

    public int getDocIdGen() throws SQLException {
        int lastDocId = 0;
        ResultSet res = stat.executeQuery("SELECT GEN_VALUE FROM GEN_IDT WHERE GEN_NAME = 'SAYAC_SF_9_GEN'");
        while (res.next()) {
            lastDocId = res.getInt(1);
        }
        return lastDocId;
    }

    public void updateDocIdGen(int docId) throws SQLException {
        String Query = String.format("UPDATE GEN_IDT SET GEN_VALUE = %s WHERE GEN_NAME = 'SAYAC_SF_9_GEN'", docId);
        stat.executeUpdate(Query);
    }
}
