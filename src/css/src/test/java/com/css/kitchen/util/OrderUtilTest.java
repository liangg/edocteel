package com.css.kitchen.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.css.kitchen.Order;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
        ramenOrder.put(Order.ORDER_SHELFLIFE, 600);
        ramenOrder.put(Order.ORDER_DECAYRATE, 0.45);
        sushiOrder = new JSONObject();
        sushiOrder.put(Order.ORDER_NAME, "Sushi dragonroll");
        sushiOrder.put(Order.ORDER_TEMP, "Cold");
        sushiOrder.put(Order.ORDER_SHELFLIFE, 900);
        sushiOrder.put(Order.ORDER_DECAYRATE, 0.15);
        // bad order
        missingTempOrder = new JSONObject();
        missingTempOrder.put(Order.ORDER_NAME, "Mochi icecream");
        missingTempOrder.put(Order.ORDER_SHELFLIFE, 300);
        missingTempOrder.put(Order.ORDER_DECAYRATE, 0.5);

        orderArray = new JSONArray();
        orderArray.add(ramenOrder);
        orderArray.add(sushiOrder);
        orderArray.add(missingTempOrder);
    }

    @Test
    public void testJsonObjectToOrder() {
        Optional<Order> orderOptional = OrderReader.toOrder(ramenOrder);
        assertTrue(orderOptional.isPresent());
        final Order order = orderOptional.get();
        assertEquals(order.getName(), "Ramen");
        assertEquals(order.getTemperature(), Order.Temperature.Hot);
        assertEquals(order.getShelfLife(), 600);
        assertEquals(Double.valueOf(order.getDecayRate()), Double.valueOf(0.45));

        orderOptional = OrderReader.toOrder(sushiOrder);
        assertTrue(orderOptional.isPresent());
        final Order order2 = orderOptional.get();
        assertEquals(order2.getName(), "Sushi dragonroll");
        assertEquals(order2.getTemperature(), Order.Temperature.Cold);
        assertEquals(order2.getShelfLife(), 900);
        assertEquals(Double.valueOf(order2.getDecayRate()), Double.valueOf(0.15));

        // should be empty due to missing temperature
        orderOptional = OrderReader.toOrder(missingTempOrder);
        assertFalse(orderOptional.isPresent());

        // should be empty due to missing shelfLife
        final JSONObject orderJson4 = new JSONObject();
        orderJson4.put(Order.ORDER_NAME, "Mochi icecream");
        orderJson4.put(Order.ORDER_TEMP, "FROZEN");
        orderJson4.put(Order.ORDER_DECAYRATE, 0.5);
        orderOptional = OrderReader.toOrder(orderJson4);
        assertFalse(orderOptional.isPresent());
    }

    @Test
    public void testOrderJsonArray() {
        //String jsonOrders = "[{\"name\": \"Banana Split\", \"temp\": \"frozen\", \"shelfLife\": 20, \"decayRate\": 0.63}, {\"name\": \"McFlury\",\"temp\": \"frozen\",\"shelfLife\": 375,\"decayRate\": 0.4}]";

        List<Order> orders = OrderReader.toOrderList(orderArray);
        assertEquals(orders.size(), 2);
        assertEquals(orders.get(0).getName(), "Ramen");
        assertEquals(orders.get(0).getTemperature(), Order.Temperature.Hot);
        assertEquals(orders.get(1).getName(), "Sushi dragonroll");
        assertEquals(orders.get(1).getTemperature(), Order.Temperature.Cold);
    }

    // FIXME:
    @Test
    public void testOrderJsonFile() {

    }
}
