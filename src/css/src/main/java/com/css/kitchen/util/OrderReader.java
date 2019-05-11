package com.css.kitchen.util;

import com.css.kitchen.Order;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * An order util that convert json data to Order
 */
public class OrderReader {
    static String ORDER_NAME = "name";
    static String ORDER_TEMP = "temp";
    static String ORDER_SHELFLIFE = "shelfLife";
    static String ORDER_DECAYRATE = "decayRate";

    public static List<Order> toOrder(String orderJsonArray) {
        // JSONParser parser = new JSONParser();
        //JSONArray jsonArray = new JSONArray(orderJsonArray);
        return null;
    }

    // Validate order json data format, and create Order only if all parameters are valid
    public static Order toOrder(JSONObject orderJson) {
        final String name = (String) orderJson.get(ORDER_NAME);
        final String temp = ((String) orderJson.get(ORDER_TEMP)).toLowerCase();
        final String life = (String) orderJson.get(ORDER_SHELFLIFE);
        final String decay = (String) orderJson.get(ORDER_DECAYRATE);
        if (name == null || temp == null || life == null || decay == null) {
            // FIXME: log metrics
            return null;
        }
        // FIXME: convert to int and double
        Order.Temperature temperature = Order.temperatureMap.get(temp);
        if (temperature == null) {
            return null;
        }
        return Order.builder().name(name).temperature(temperature).build();
    }
}
