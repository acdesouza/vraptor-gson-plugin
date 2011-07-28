/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource - guilherme.silveira@caelum.com.br
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.restfulie;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.restfulie.Restfulie;
import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;
import br.com.caelum.vraptor.restfulie.relation.Relation;
import br.com.caelum.vraptor.restfulie.relation.RelationBuilder;

import com.google.gson.Gson;

/**
 * Ensure that JSON serialization, of Restful resources, contains resources links. And not restful resources remains untouched.
 * 
 * @author ac de souza
 */
public class HipermediaResourceGsonJSONSerializerTest {

	private @Mock Restfulie restfulie;
	private @Mock RelationBuilder builder;
	private @Mock HypermediaResource resource;
	
	private ByteArrayOutputStream stream;
	private PrintWriter writer = null;

	private HypermediaResourceGsonJSONSerializer gson;

	@Before
	public void setup() throws IOException {
		MockitoAnnotations.initMocks(this);

		this.restfulie = mock(Restfulie.class);
		when(restfulie.newRelationBuilder()).thenReturn(builder);

		Configuration config = mock(Configuration.class);
		when(config.getApplicationPath()).thenReturn("http://www.caelum.com.br");

		stream = new ByteArrayOutputStream();
		writer = new PrintWriter(stream, true);
		gson = new HypermediaResourceGsonJSONSerializer(new Gson(), writer, restfulie, config);
	}
	@Test
	public void shouldSerializeNoLinksIfThereIsNoTransition() {
		gson.from(resource).serialize();
		writer.flush();
		assertThat(result(), not(containsString("links")));
	}

	@Test
	public void shouldSerializeOneLinkIfThereIsATransition() {
		Relation kill = mock(Relation.class);
		when(kill.getName()).thenReturn("kill");
		when(kill.getUri()).thenReturn("/kill");

		when(builder.getRelations()).thenReturn(Arrays.asList(kill));
		gson.from(resource).serialize();
		writer.flush();

		String expectedLinks = "\"links\":[{\"rel\":\"kill\",\"href\":\"http://www.caelum.com.br/kill\"}]";
		assertThat(result(), containsString(expectedLinks));
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
		gson.from(resource).serialize();
		writer.flush();

		String expectedLinks = "\"links\":[{\"rel\":\"kill\",\"href\":\"http://www.caelum.com.br/kill\"},{\"rel\":\"ressurect\",\"href\":\"http://www.caelum.com.br/ressurect\"}]";
		assertThat(result(), containsString(expectedLinks));
	}

	private String result() {
		return new String(stream.toByteArray());
	}
}
