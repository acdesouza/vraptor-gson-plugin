package br.com.caelum.vraptor.util.gson;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;
import br.com.caelum.vraptor.serialization.gson.GsonJSONSerialization;

public class GsonSerializationProvider extends SpringProvider {
	
	@Override
    protected void registerCustomComponents(ComponentRegistry registry) {
        registry.register(GsonJSONSerialization.class, GsonJSONSerialization.class);
//FIXME: O Spring não está conseguindo insanciar este componente, por falta de uma instância de objeto Restfulie. #comofaz?
//        registry.register(GsonRestfulSerializationJSON.class, GsonRestfulSerializationJSON.class);
    }

}