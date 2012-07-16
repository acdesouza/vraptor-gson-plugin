package br.com.beyondclick.vraptor.serialization.gson;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.NoRootSerialization;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.view.ResultException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Gson implementation for JSONSerialization
 * 
 * Know limitation: you can not serialize objects with circular references since
 * that will result in infinite recursion.
 * https://sites.google.com/site/gson/gson-user-guide#TOC-Object-Examples
 * 
 * @author ac de souza
 * @since 3.3.2
 * 
 */
@Component
public class GsonJSONSerialization implements JSONSerialization {

	private final HttpServletResponse response;

	public GsonJSONSerialization(HttpServletResponse response) {
		this.response = response;
	}

	public boolean accepts(String format) {
		return "json".equals(format);
	}

	public <T> Serializer from(T object) {
		return from(object, null);
	}

	public <T> Serializer from(T object, String alias) {
		try {
			response.setContentType("application/json");
			return getSerializer(response.getWriter()).from(object, alias);
		} catch (IOException e) {
			throw new ResultException("Unable to serialize data", e);
		}
	}

	public SerializerBuilder getSerializer(Writer writer) {
		return new GsonJSONSerializer(getGson(), writer);
	}

	private GsonBuilder gsonBuilder;

	protected GsonBuilder getGsonBuilder() {
		gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		gsonBuilder.registerTypeAdapter(Date.class, new DateSerializer());
		gsonBuilder.registerTypeAdapter(java.sql.Date.class, new SqlDateSerializer());
		if (indented)
			gsonBuilder.setPrettyPrinting();
		return gsonBuilder;
	}

	protected Gson getGson() {
		return this.getGsonBuilder().create();
	}

	private class DateSerializer implements JsonSerializer<Date> {
		public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}
	}

	private class SqlDateSerializer implements JsonSerializer<java.sql.Date> {
		public JsonElement serialize(java.sql.Date src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(new Date(src.getTime()).toString());
		}
	}

	public <T> NoRootSerialization withoutRoot() {
		return this;
	}

	private boolean indented = false;

	public JSONSerialization indented() {
		indented = true;
		return this;
	}
}