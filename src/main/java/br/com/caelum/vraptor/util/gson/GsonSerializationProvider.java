package br.com.caelum.vraptor.util.gson;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;
import br.com.caelum.vraptor.restfulie.serialization.GsonRestfulSerializationJSON;
import br.com.caelum.vraptor.serialization.gson.GsonJSONSerialization;

public class GsonSerializationProvider extends SpringProvider {
	
	@Override
    protected void registerCustomComponents(ComponentRegistry registry) {
        registry.register(GsonJSONSerialization.class, GsonJSONSerialization.class);
        registry.register(GsonRestfulSerializationJSON.class, GsonRestfulSerializationJSON.class);
    }

}