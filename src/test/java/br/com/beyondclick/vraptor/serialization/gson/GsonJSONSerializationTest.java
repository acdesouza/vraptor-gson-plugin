package br.com.beyondclick.vraptor.serialization.gson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class GsonJSONSerializationTest {
	private GsonJSONSerialization serialization;
	private ByteArrayOutputStream stream;
	private PrintWriter writer = null;
	
	private Date defaultTestDate;

	@Before
	public void setup() throws Exception {
		this.stream = new ByteArrayOutputStream();

		HttpServletResponse response = mock(HttpServletResponse.class);
		writer = new PrintWriter(stream, true);
		when(response.getWriter()).thenReturn(writer);

		this.serialization = new GsonJSONSerialization(response);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(1982, 7, 28, 0, 0, 0);
		defaultTestDate = calendar.getTime();
	}

	@Test
	public void shouldSerializeAllNullFields() throws Exception {
		Order order = new Order(new Client("guilherme silveira"), 15.0, null, defaultTestDate);
		serialization.from(order).serialize();

		JSONObject jsonOrder = new JSONObject(result());
		assertEquals(15.0, jsonOrder.getDouble("price"), 0);
		assertEquals(JSONObject.NULL, jsonOrder.get("comments"));
		assertEquals("Sat Aug 28 00:00:00 BRT 1982", jsonOrder.get("date"));
		
		JSONObject jsonClient = jsonOrder.getJSONObject("client");
		assertEquals("guilherme silveira", jsonClient.get("name"));
		assertEquals(JSONObject.NULL, jsonClient.get("address"));
		
		JSONArray jsonItems = jsonOrder.getJSONArray("items");
		assertEquals(0, jsonItems.length());
	}

	@Test
	public void shouldSerializeAllBasicFieldsIdented() throws Exception {
		String expectedResult = "{\n  \"client\": {\n    \"name\": \"guilherme silveira\",\n    \"address\": null\n  },\n  \"price\": 15.0,\n  \"comments\": \"pack it nicely, please\",\n  \"date\": \"Sat Aug 28 00:00:00 BRT 1982\",\n  \"items\": []\n}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please", defaultTestDate);
		serialization.indented().from(order).serialize();

		String result = result();
		assertEquals(result, expectedResult);
		
		JSONObject jsonOrder = new JSONObject(result);
		assertNotSame(JSONObject.NULL, jsonOrder);
	}
	
	
	public static enum Type { basic, advanced }
	class BasicOrder extends Order {
		public BasicOrder(Client client, double price, String comments, Type type) {
			super(client, price, comments, null);
			this.type = type;
		}
		@SuppressWarnings("unused")
		private final Type type;
	}

	@Test
	public void shouldSerializeEnumFields() throws Exception {
		Order order = new BasicOrder(new Client("guilherme silveira"), 15.0, "pack it nicely, please", Type.basic);
		serialization.from(order).serialize();

		JSONObject jsonOrder = new JSONObject(result());
		assertEquals("basic", jsonOrder.get("type"));
	}

	@Test
	public void shouldSerializeCollection() throws Exception {
		String expectedResult = "{\"client\":{\"name\":\"guilherme silveira\",\"address\":null},\"price\":15.0,\"comments\":\"pack it nicely, please\",\"date\":\"Sat Aug 28 00:00:00 BRT 1982\",\"items\":[]}";
		expectedResult += "," + expectedResult;
		expectedResult = "[" + expectedResult + "]";

		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please", defaultTestDate);
		serialization.from(Arrays.asList(order, order)).serialize();

		String result = result();
		assertEquals(result, expectedResult);
		
		JSONArray jsonListOrders = new JSONArray(result);
		assertEquals(2, jsonListOrders.length());
		
		JSONObject jsonOrder_1 = jsonListOrders.getJSONObject(0);
		assertNotSame(JSONObject.NULL, jsonOrder_1);

		JSONObject jsonOrder_2 = jsonListOrders.getJSONObject(1);
		assertNotSame(JSONObject.NULL, jsonOrder_2);
	}

	public static class Address {
		String street;

		public Address(String street) {
			this.street = street;
		}
	}

	public static class Client {
		String name;
		Address address;

		public Client(String name) {
			this.name = name;
		}

		public Client(String name, Address address) {
			this.name = name;
			this.address = address;
		}
	}

	public static class Item {
		String name;
		double price;

		public Item(String name, double price) {
			this.name = name;
			this.price = price;
		}
	}

	public static class Order {
		Client client;
		double price;
		String comments;
		Date date;
		List<Item> items;

		public Order(Client client, double price, String comments, Date date, Item... items) {
			this.client = client;
			this.price = price;
			this.comments = comments;
			this.date = date;
			this.items = new ArrayList<Item>(Arrays.asList(items));
		}

		public String nice() {
			return "nice output";
		}

	}

	public static class AdvancedOrder extends Order {

		@SuppressWarnings("unused")
		private final String notes;

		public AdvancedOrder(Client client, double price, String comments, String notes) {
			super(client, price, comments, null);
			this.notes = notes;
		}

	}

	private String result() {
		writer.flush();
		return new String(stream.toByteArray());
	}
}