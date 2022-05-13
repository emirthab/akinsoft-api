import java.io.IOException;
import java.sql.SQLException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import API.GetItem;
import API.GetItemWithPrice;
import API.GetPrice;
import API.GetStockTransactions;
import API.PostStockTransaction;
import Utils.Connector;

public class App {
    public static Connector con = new Connector();
    public static CloseableHttpClient httpclient = HttpClients.createDefault();

    public static void main(String[] args) throws SQLException, ClientProtocolException, IOException {
        new GetItem(con.createFirebirdStatement()).handle("item");
        new GetPrice(con.createFirebirdStatement()).handle("prices");
        new GetItemWithPrice(con.createFirebirdStatement()).handle("itemwithsaleprice");
        new GetStockTransactions(con.createFirebirdStatement()).handle();
        new PostStockTransaction(con.createFirebirdStatement()).handle();
    }
}
