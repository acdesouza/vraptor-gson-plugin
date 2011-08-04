package br.com.beyondclick.vraptor.serialization.gson;

import java.io.IOException;
import java.io.Writer;

import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.view.ResultException;

import com.google.gson.Gson;

/**
 * https://sites.google.com/site/gson/gson-user-guide
 * 
 * @author ac de souza
 */
public class GsonJSONSerializer implements SerializerBuilder {
	
	private final Gson gson;
	private final Writer writer;
	private Object root;

	public GsonJSONSerializer(final Gson gson, final Writer writer) {
		this.gson = gson;
		this.writer = writer;
	}

	public Serializer exclude(String... names) {
		// https://sites.google.com/site/gson/gson-user-guide#TOC-Excluding-Fields-From-Serialization
		return this;
	}

	public Serializer include(String... names) {
		return this;
	}

	public Serializer recursive() {
		return this;
	}

	public void serialize() {
		try {
			getWriter().write(convertUsingGson(root));
		} catch (IOException e) {
			throw new ResultException("Unable to serialize data width Gson API", e);
		}
	}

	protected String convertUsingGson(Object root) {
		return getGson().toJson(root);
	}

	protected Gson getGson() {
		return gson;
	}

	protected Writer getWriter() {
		return writer;
	}

	public <T> Serializer from(T object) {
		return from(object, null);
	}

	public <T> Serializer from(T object, String alias) {
		this.root = object;
		return this;
	}
}