import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import API.GetItem;
import API.GetItemWithPrice;
import API.GetPrice;
import API.GetStockTransactions;
import API.PostStockTransaction;

public class TestAPI {
    public TestUtils utils = new TestUtils();

    @Before
    public void test_generate_temp_database_file() throws SQLException {
        Statement stat = utils.createTempDatabaseStatement();
        stat.executeUpdate("restore from './src/test/resources/mock.db'");
        stat.close();
    }

    @Test
    public void test_get_item_from_barcode() throws SQLException, JSONException, IOException {
        JSONObject expected = new GetItem(utils.createTempDatabaseStatement()).getResponse(utils.customRequestBarcode);
        JSONObject actual = new JSONObject(utils.readFile("./src/test/resources/TestGetItemResponse.json"));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }

    @Test
    public void test_get_prices_from_barcode() throws SQLException, JSONException, IOException {
        JSONObject expected = new GetPrice(utils.createTempDatabaseStatement()).getResponse(utils.customRequestBarcode);
        JSONObject actual = new JSONObject(utils.readFile("./src/test/resources/TestGetPricesResponse.json"));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }

    @Test
    public void test_get_stock_transaction_from_date() throws SQLException, JSONException, IOException {
        JSONArray expected = new GetStockTransactions(utils.createTempDatabaseStatement()).getResponse("2022-04-04","2222-04-04");
        JSONArray actual = new JSONArray(utils.readFile("./src/test/resources/TestGetStockTransactionsResponse.json"));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }

    @Test
    public void test_add_stock_transaction() throws JSONException, SQLException, IOException {
        new PostStockTransaction(utils.createTempDatabaseStatement()).execute(new JSONArray(utils.readFile("./src/test/resources/TestPostStockTransactionRequest.json")),0);
        JSONArray expected = new GetStockTransactions(utils.createTempDatabaseStatement()).getResponse("2022-04-04","2222-04-04");
        // date maybe diffrent so deleting date key
        expected.forEach(item->{
            JSONObject i = (JSONObject)item;
            i.remove("date");
        });
        JSONArray actual = new JSONArray(utils.readFile("./src/test/resources/TestPostStockTransactionResponse.json"));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }

    @Test
    public void test_get_item_with_sale_price_from_barcode() throws JSONException, SQLException, IOException {
        JSONObject expected = new GetItemWithPrice(utils.createTempDatabaseStatement()).getResponse(utils.customRequestBarcode);
        JSONObject actual = new JSONObject(utils.readFile("./src/test/resources/TestGetItemWithSalePriceResponse.json"));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }

    @After
    public void test_remove_temp_database_file() {
        File tmpData = new File("./src/test/resources/.tmp.db");
        tmpData.delete();
    }
}
