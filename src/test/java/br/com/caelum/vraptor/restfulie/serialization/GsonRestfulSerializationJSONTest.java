package br.com.caelum.vraptor.restfulie.serialization;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.restfulie.HypermediaResourceGsonJSONSerializer;
import br.com.caelum.vraptor.serialization.Serializer;

public class GsonRestfulSerializationJSONTest {
	private ByteArrayOutputStream stream;
	private PrintWriter writer = null;
	private HttpServletResponse response;
	
	@Before
	public void setup() throws IOException {
		this.response = mock(HttpServletResponse.class);

		stream = new ByteArrayOutputStream();

		this.writer = new PrintWriter(stream, true);
		when(response.getWriter()).thenReturn(writer);
	}

	@Test
	public void shouldReturnAnSerializerInstanceWithSupportToLinkConvertersBasedOnReflection() {
		GsonRestfulSerializationJSON serialization = new GsonRestfulSerializationJSON(response, null, null);
		Serializer serializer = serialization.getSerializer(null);
		assertThat(serializer.getClass(), is(typeCompatibleWith(HypermediaResourceGsonJSONSerializer.class)));
	}
}