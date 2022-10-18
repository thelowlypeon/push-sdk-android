package com.vibes.vibes.tracking;

import com.vibes.vibes.TestConfig;

import org.json.JSONObject;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProductTest extends TestConfig {
    private static final String TEST_NAME = "Apple";
    private static final String TEST_ID = UUID.randomUUID().toString();
    private static final Double TEST_PRICE = 4.0;
    private static final String TEST_BRAND = "Farmer Zevon's";
    private static final String TEST_CATEGORY = "Groceries";
    private static final String TEST_VARIANT = "Fruits";
    private static final Integer TEST_QUANTITY = 6;
    private static final String TEST_COUPON = "WarewolvesOfLondon";
    private static final String TEST_POSITION = "10";


    @Test
    public void testProductEncode() throws Exception {
        JSONObject content = new JSONObject();
        content.put("id", TEST_ID);
        content.put("name", TEST_NAME);
        content.put("price", TEST_PRICE);
        content.put("brand", TEST_BRAND);
        content.put("category", TEST_CATEGORY);
        content.put("variant", TEST_VARIANT);
        content.put("quantity", TEST_QUANTITY);
        content.put("coupon", TEST_COUPON);
        content.put("position", TEST_POSITION);

        Product product = new Product(TEST_ID, TEST_PRICE, TEST_NAME, TEST_BRAND, TEST_CATEGORY, TEST_VARIANT, TEST_QUANTITY, TEST_COUPON, TEST_POSITION);
        JSONObject output = product.encode();
        assertThat(content.toString(), is(output.toString()));
    }

    public static Product createProduct() {
        return new Product(TEST_ID, TEST_PRICE, TEST_NAME, TEST_BRAND, TEST_CATEGORY, TEST_VARIANT, TEST_QUANTITY, TEST_COUPON, TEST_POSITION);
    }
}
