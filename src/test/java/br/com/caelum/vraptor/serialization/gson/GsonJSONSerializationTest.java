package br.com.caelum.vraptor.serialization.gson;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

public class GsonJSONSerializationTest {
	private GsonJSONSerialization serialization;
	private ByteArrayOutputStream stream;
	private PrintWriter writer = null;

	@Before
	public void setup() throws Exception {
		this.stream = new ByteArrayOutputStream();

		HttpServletResponse response = mock(HttpServletResponse.class);
		writer = new PrintWriter(stream, true);
		when(response.getWriter()).thenReturn(writer);

		this.serialization = new GsonJSONSerialization(response);
	}

	@Test
	public void shouldSerializeAllNullFields() throws IOException {
		String expectedResult = "{\"client\":{\"name\":\"guilherme silveira\",\"address\":null},\"price\":15.0,\"comments\":null,\"items\":[]}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, null);
		serialization.from(order).serialize();
		writer.flush();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeAllBasicFieldsIdented() {
		String expectedResult = "{\n  \"client\": {\n    \"name\": \"guilherme silveira\",\n    \"address\": null\n  },\n  \"price\": 15.0,\n  \"comments\": \"pack it nicely, please\",\n  \"items\": []\n}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.indented().from(order).serialize();
		writer.flush();
		assertEquals(result(), expectedResult);
	}
	
	
	public static enum Type { basic, advanced }
	class BasicOrder extends Order {
		public BasicOrder(Client client, double price, String comments, Type type) {
			super(client, price, comments);
			this.type = type;
		}
		@SuppressWarnings("unused")
		private final Type type;
	}

	@Test
	public void shouldSerializeEnumFields() {
		Order order = new BasicOrder(new Client("guilherme silveira"), 15.0, "pack it nicely, please", Type.basic);
		serialization.from(order).serialize();
		writer.flush();
		String result = result();
		assertThat(result, containsString("\"type\":\"basic\""));
	}
	
	@Test
	public void shouldSerializeCollection() {
		String expectedResult = "{\"client\":{\"name\":\"guilherme silveira\",\"address\":null},\"price\":15.0,\"comments\":\"pack it nicely, please\",\"items\":[]}";
		expectedResult += "," + expectedResult;
		expectedResult = "[" + expectedResult + "]";

		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(Arrays.asList(order, order)).serialize();
		writer.flush();
		assertEquals(result(), expectedResult);
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
		List<Item> items;

		public Order(Client client, double price, String comments, Item... items) {
			this.client = client;
			this.price = price;
			this.comments = comments;
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
			super(client, price, comments);
			this.notes = notes;
		}

	}

	private String result() {
		return new String(stream.toByteArray());
	}
}

/*
private static final Logger log = Logger.getLogger(GsonSerializationTest.class);

private @Mock HttpServletResponse response;
private @Mock HypermediaResource resource;
private @Mock RelationBuilder builder;


private GsonSerialization serialization;
private StringPrintWriter writer;
private @Mock OutputStream out;

@Before
public void setup() throws IOException {
	MockitoAnnotations.initMocks(this);

	writer = new StringPrintWriter(out);
	when(response.getWriter()).thenReturn(writer);
	serialization = new GsonSerialization(response);
}

class StringPrintWriter extends PrintWriter {
	public StringPrintWriter(OutputStream out) {
		super(out);
	}

	private StringBuffer out = new StringBuffer("");

	public String getOut() {
		return this.out.toString();
	}

	@Override
	public void write(String s) {
		this.out.append(s);
	}
}

@Test
public void shouldSerializeNoLinksIfThereIsNoTransition() {
	serialization.from(resource).serialize();
	String json = writer.getOut();
	log.debug(json);
	assertThat(json, not(containsString("links")));
}

@Test
public void shouldSerializeOneLinkIfThereIsATransition() {
	Relation kill = mock(Relation.class);
	when(kill.getName()).thenReturn("kill");
	when(kill.getUri()).thenReturn("/kill");

	when(builder.getRelations()).thenReturn(Arrays.asList(kill));
	serialization.from(resource).serialize();
	String json = writer.getOut();
	log.debug(json);
	String expectedLinks = "\"links\":[{\"rel\":\"kill\",\"href\":\"http://www.caelum.com.br/kill\"}]";
	assertThat(json, containsString(expectedLinks));
}

@Test
public void shouldSerializeAllLinksIfThereAreTransitions() {
	Relation kill = mock(Relation.class);
	when(kill.getName()).thenReturn("kill");
	when(kill.getUri()).thenReturn("/kill");

	Relation ressurect = mock(Relation.class);
	when(ressurect.getName()).thenReturn("ressurect");
	when(ressurect.getUri()).thenReturn("/ressurect");

	when(builder.getRelations()).thenReturn(Arrays.asList(kill, ressurect));
	String json = xstream.toXML(resource);
	String expectedLinks = "\"links\": [\n    {\n      \"rel\": \"kill\",\n      \"href\": \"http://www.caelum.com.br/kill\"\n    },\n    {\n      \"rel\": \"ressurect\",\n      \"href\": \"http://www.caelum.com.br/ressurect\"\n    }\n  ]";
	assertThat(json, containsString(expectedLinks));
}
*/