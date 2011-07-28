package br.com.caelum.vraptor.serialization.gson;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.NoRootSerialization;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.view.ResultException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gson implementation for JSONSerialization
 * 
 * Know limitation: you can not serialize objects with circular references since that will result in infinite recursion.
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

	@Override
	public boolean accepts(String format) {
		return "json".equals(format);
	}

	@Override
	public <T> Serializer from(T object) {
		return from(object, null);
	}

	@Override
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
	protected Gson getGson() {
		gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		if( indented ) gsonBuilder.setPrettyPrinting();
		return gsonBuilder.create();
	}

	@Override
	public <T> NoRootSerialization withoutRoot() {
		return this;
	}

	private boolean indented;
	@Override
	public JSONSerialization indented() {
		indented = true;
		return this;
	}
}