package com.css.kitchen.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.css.kitchen.Order;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class OrderUtilTest {
    private JSONObject ramenOrder;
    private JSONObject sushiOrder;
    private JSONObject missingTempOrder;
    private JSONArray orderArray;

    @Before
    public void init() {
        ramenOrder = new JSONObject();
        ramenOrder.put(Order.ORDER_NAME, "Ramen");
        ramenOrder.put(Order.ORDER_TEMP, "hot");
        ramenOrder.put(Order.ORDER_SHELFLIFE, 600L);
        ramenOrder.put(Order.ORDER_DECAYRATE, 0.45);
        sushiOrder = new JSONObject();
        sushiOrder.put(Order.ORDER_NAME, "Sushi dragonroll");
        sushiOrder.put(Order.ORDER_TEMP, "Cold");
        sushiOrder.put(Order.ORDER_SHELFLIFE, 900L);
        sushiOrder.put(Order.ORDER_DECAYRATE, 0.15);
        // bad order
        missingTempOrder = new JSONObject();
        missingTempOrder.put(Order.ORDER_NAME, "Mochi icecream");
        missingTempOrder.put(Order.ORDER_SHELFLIFE, 300L);
        missingTempOrder.put(Order.ORDER_DECAYRATE, 0.5);

        orderArray = new JSONArray();
        orderArray.add(ramenOrder);
        orderArray.add(sushiOrder);
        orderArray.add(missingTempOrder);
    }

    @Test
    public void testJsonObjectToOrder() {
        Optional<Order> orderOptional = OrderReader.readOrder(ramenOrder);
        assertTrue(orderOptional.isPresent());
        final Order order = orderOptional.get();
        assertEquals(order.getName(), "Ramen");
        assertEquals(order.getTemperature(), Order.Temperature.Hot);
        assertEquals(order.getShelfLife(), 600L);
        assertEquals(Double.valueOf(order.getDecayRate()), Double.valueOf(0.45));

        orderOptional = OrderReader.readOrder(sushiOrder);
        assertTrue(orderOptional.isPresent());
        final Order order2 = orderOptional.get();
        assertEquals(order2.getName(), "Sushi dragonroll");
        assertEquals(order2.getTemperature(), Order.Temperature.Cold);
        assertEquals(order2.getShelfLife(), 900L);
        assertEquals(Double.valueOf(order2.getDecayRate()), Double.valueOf(0.15));

        // should be empty due to missing temperature
        orderOptional = OrderReader.readOrder(missingTempOrder);
        assertFalse(orderOptional.isPresent());

        // should be empty due to missing shelfLife
        final JSONObject orderJson4 = new JSONObject();
        orderJson4.put(Order.ORDER_NAME, "Mochi icecream");
        orderJson4.put(Order.ORDER_TEMP, "FROZEN");
        orderJson4.put(Order.ORDER_DECAYRATE, 0.5);
        orderOptional = OrderReader.readOrder(orderJson4);
        assertFalse(orderOptional.isPresent());

        // should be empty due to wrong shelfLife value type
        final JSONObject orderJson5 = new JSONObject();
        orderJson5.put(Order.ORDER_NAME, "Mochi icecream");
        orderJson5.put(Order.ORDER_TEMP, "FROZEN");
        orderJson5.put(Order.ORDER_SHELFLIFE, "300");
        orderJson5.put(Order.ORDER_DECAYRATE, 0.5);
        orderOptional = OrderReader.readOrder(orderJson5);
        assertFalse(orderOptional.isPresent());
    }

    @Test
    public void testOrderJsonArray() {
        List<Order> orders = OrderReader.toOrderList(orderArray);
        assertEquals(orders.size(), 2); // skip one invalid order
        assertEquals(orders.get(0).getName(), "Ramen");
        assertEquals(orders.get(0).getTemperature(), Order.Temperature.Hot);
        assertEquals(orders.get(1).getName(), "Sushi dragonroll");
        assertEquals(orders.get(1).getTemperature(), Order.Temperature.Cold);
    }

    // read a local orders json file and create a list of Orders
    @Test
    public void testOrderJsonFile() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL ordersUrl = classLoader.getResource("test_orders.json");
        List<Order> orders = OrderReader.readOrdersJson(ordersUrl.getFile());
        assertEquals(orders.size(), 4);
        assertEquals(orders.get(0).getName(), "Banana Split");
        assertEquals(orders.get(0).getTemperature(), Order.Temperature.Frozen);
        assertEquals(orders.get(0).getShelfLife(), 20L);
        assertEquals(Double.valueOf(orders.get(0).getDecayRate()), Double.valueOf(0.63));
        assertEquals(orders.get(1).getName(), "McFlury");
        assertEquals(orders.get(1).getTemperature(), Order.Temperature.Frozen);
        assertEquals(orders.get(1).getShelfLife(), 375L);
        assertEquals(Double.valueOf(orders.get(1).getDecayRate()), Double.valueOf(0.4));
        assertEquals(orders.get(2).getName(), "Acai Bowl");
        assertEquals(orders.get(2).getTemperature(), Order.Temperature.Cold);
        assertEquals(orders.get(2).getShelfLife(), 249L);
        assertEquals(Double.valueOf(orders.get(2).getDecayRate()), Double.valueOf(0.3));
        assertEquals(orders.get(3).getName(), "Yogurt");
        assertEquals(orders.get(3).getTemperature(), Order.Temperature.Cold);
        assertEquals(orders.get(3).getShelfLife(), 263L);
        assertEquals(Double.valueOf(orders.get(3).getDecayRate()), Double.valueOf(0.37));
    }
}
