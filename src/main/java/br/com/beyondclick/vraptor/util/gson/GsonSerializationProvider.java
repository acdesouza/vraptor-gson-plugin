package br.com.beyondclick.vraptor.util.gson;

import br.com.beyondclick.vraptor.restfulie.serialization.GsonRestfulSerializationJSON;
import br.com.beyondclick.vraptor.serialization.gson.GsonJSONSerialization;
import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;

public class GsonSerializationProvider extends SpringProvider {
	
	@Override
    protected void registerCustomComponents(ComponentRegistry registry) {
        registry.register(GsonJSONSerialization.class, GsonJSONSerialization.class);
        registry.register(GsonRestfulSerializationJSON.class, GsonRestfulSerializationJSON.class);
    }

}