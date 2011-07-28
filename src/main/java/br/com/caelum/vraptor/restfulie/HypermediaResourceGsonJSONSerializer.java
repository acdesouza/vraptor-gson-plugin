package br.com.caelum.vraptor.restfulie;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;
import br.com.caelum.vraptor.restfulie.relation.Relation;
import br.com.caelum.vraptor.restfulie.relation.RelationBuilder;
import br.com.caelum.vraptor.serialization.gson.GsonJSONSerializer;

import com.google.gson.Gson;

public class HypermediaResourceGsonJSONSerializer extends GsonJSONSerializer {
	
	private final Restfulie restfulie;
	private final Configuration config;

	public HypermediaResourceGsonJSONSerializer(Gson gson, Writer writer, Restfulie restfulie, Configuration config) {
		super(gson, writer);
		this.restfulie = restfulie;
		this.config = config;
	}

	@Override
	protected String convertUsingGson(Object root) {
		String defaultConversion = getGson().toJson(root);

		HypermediaResource resource = (HypermediaResource) root;
		RelationBuilder builder = restfulie.newRelationBuilder();
		resource.configureRelations(builder);

		String linksConverted = null;
		if( !builder.getRelations().isEmpty() ) {
			linksConverted = ",\"links\":";
			List<Link> list = new ArrayList<Link>();
			for (Relation t : builder.getRelations()) {
				list.add( new Link(t.getName(), config.getApplicationPath() + t.getUri()) );
			}
			linksConverted += getGson().toJson(list);
		}

		String hypermediaResourceConverted = defaultConversion.substring(0, defaultConversion.length() - 1) + linksConverted + "}";
		return hypermediaResourceConverted;
	}
}