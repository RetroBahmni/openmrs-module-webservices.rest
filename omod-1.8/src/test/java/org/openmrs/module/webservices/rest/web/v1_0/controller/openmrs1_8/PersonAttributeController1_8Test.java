/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;


import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest;

import javax.servlet.http.HttpServletResponse;

/**
 * Tests functionality of {@link PersonAttributeController}.
 */
public class PersonAttributeController1_8Test extends BaseCrudControllerTest {
	
	String personUuid = RestTestConstants1_8.PERSON_UUID;
	
	private PersonService service;
	
	@Before
	public void before() throws Exception {
		this.service = Context.getPersonService();
	}
	
	@Test
	public void shouldAddAttributeToPerson() throws Exception {
		int before = service.getPersonByUuid(personUuid).getAttributes().size();
		String json = "{ \"attributeType\":\"b3b6d540-a32e-44c7-91b3-292d97667518\", \"value\":\"testing\"}";
		handle(newPostRequest(getURI(), json));
		int after = service.getPersonByUuid(personUuid).getAttributes().size();
		assertThat(after, is(before + 1));
	}
	
	@Test
	public void shouldEditAttribute() throws Exception {
		String json = "{ \"attributeType\":\"54fc8400-1683-4d71-a1ac-98d40836ff7c\" }";
		
		PersonAttribute personAttribute = service.getPersonAttributeByUuid(getUuid());
		assertThat(personAttribute.getAttributeType().getName(), is("Race"));
		
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		
		personAttribute = service.getPersonAttributeByUuid(getUuid());
		assertThat(personAttribute.getAttributeType().getName(), is("Birthplace"));
	}
	
	@Test
	public void shouldVoidAttribute() throws Exception {
		PersonAttribute personAttribute = service.getPersonAttributeByUuid(getUuid());
		assertThat(personAttribute.isVoided(), is(false));
		
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("reason", "unit test")));
		
		personAttribute = service.getPersonAttributeByUuid(getUuid());
		assertThat(personAttribute.isVoided(), is(true));
		assertThat(personAttribute.getVoidReason(), is("unit test"));
	}
	
	@Test
	public void shouldPurgeAttribute() throws Exception {
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("purge", "")));
		
		assertThat(service.getPersonAttributeByUuid(getUuid()), nullValue());
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "person/" + RestTestConstants1_8.PERSON_UUID + "/attribute";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_8.PERSON_ATTRIBUTE_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return service.getPersonByUuid(RestTestConstants1_8.PERSON_UUID).getAttributes().size();
	}
}
