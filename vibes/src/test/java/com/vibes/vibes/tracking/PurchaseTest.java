package com.vibes.vibes.tracking;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.vibes.vibes.TestConfig;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PurchaseTest extends TestConfig {
    private static final List<Product> products = new ArrayList<>();
    private static final String TEST_ID = UUID.randomUUID().toString();
    private static final String TEST_AFFILIATION = "BVM";
    private static final Double TEST_REVENUE = 123.1;
    private static final Double TEST_TAX = 10.2;
    private static final Double TEST_SHIPPING = 21.2;
    private static final Double TEST_COUPON = 281.3;
    private static final String TEST_LIST = "Test list sample";
    private static final String TEST_STEP = "Stair step";
    private static final String TEST_OPTION = "purchase";

    @Before
    public void start() {
        products.add(ProductTest.createProduct());
    }

    private static final List<Product> TEST_PRODUCTS = products;

    @Test
    public void testPurchaseEncode() throws Exception {
        JSONObject content = new JSONObject();
        content.put("id", TEST_ID);
        content.put("affiliation", TEST_AFFILIATION);
        content.put("revenue", TEST_REVENUE);
        content.put("tax", TEST_TAX);
        content.put("shipping", TEST_SHIPPING);
        content.put("coupon", TEST_COUPON);
        content.put("list", TEST_LIST);
        content.put("step", TEST_STEP);
        content.put("option", TEST_OPTION);
        content.put("products", TEST_PRODUCTS);

        Purchase purchase = new Purchase(TEST_ID, TEST_AFFILIATION, TEST_REVENUE, TEST_TAX,
                TEST_SHIPPING, TEST_COUPON, TEST_LIST, TEST_STEP, TEST_OPTION, TEST_PRODUCTS);
        JSONObject output = purchase.encode();
        assertThat(content.toString(), is(output.toString()));
    }

    public static Purchase createPurchase() {
        return new Purchase(TEST_ID, TEST_AFFILIATION, TEST_REVENUE, TEST_TAX,
                TEST_SHIPPING, TEST_COUPON, TEST_LIST, TEST_STEP, TEST_OPTION, TEST_PRODUCTS);
    }
}
