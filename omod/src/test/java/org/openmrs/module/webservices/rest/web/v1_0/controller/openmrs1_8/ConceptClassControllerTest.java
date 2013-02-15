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

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ResourceTestConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests functionality of {@link ConceptClassController}.
 */
public class ConceptClassControllerTest extends BaseCrudControllerTest {
	
	private ConceptService service;
	
	private static final String ACTIVE_LIST_INITIAL_XML = "customActiveListTest.xml";
	
	@Before
	public void init() throws Exception {
		executeDataSet(ACTIVE_LIST_INITIAL_XML);
		service = Context.getConceptService();
	}
	
	@Test
	public void getConceptClass_shouldGetADefaultRepresentationOfAConceptClass() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Util.log("ConceptClass fetched (default)", result);
		Assert.assertEquals(getUuid(), result.get("uuid"));
	}
	
	@Test
	public void getConceptClass_shouldGetAConceptClassByUuid() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		Util.log("ConceptClass fetched (default)", result);
		Assert.assertEquals(getUuid(), result.get("uuid"));
		
	}
	
	public void getConceptClass_shouldGetAConceptClassByName() throws Exception {
		String conceptClassName = "Drug";
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + conceptClassName);
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		Util.log("ConceptClassName fetched (default)", result);
		Assert.assertEquals(conceptClassName, result.get("name"));
	}
	
	@Test
	public void getConceptClass_shouldListAllConceptClasses() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		List<Object> results = (List<Object>) result.get("results");
		Assert.assertEquals(getAllCount(), results.size());
		Util.log("Found " + results.size() + " conceptClasse(s)", results);
	}
	
	@Test
	public void shouldRetireAConceptClass() throws Exception {
		String uuid = "4719138c-8553-4c81-bb92-753f41f8be16";
		
		Assert.assertEquals(false, service.getConceptClassByUuid(uuid).isRetired());
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + uuid);
		req.addParameter("!purge", "");
		final String reason = "none";
		req.addParameter("reason", reason);
		handle(req);
		Assert.assertEquals(true, service.getConceptClassByUuid(uuid).isRetired());
		Assert.assertEquals(reason, service.getConceptClassByUuid(uuid).getRetireReason());
	}
	
	public void shouldPurgeAConceptClass() throws Exception {
		String uuid = "77177ce7-1410-40ee-bbad-ff6905ee3095";
		
		Assert.assertNotNull(service.getConceptClassByUuid(uuid));
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + uuid);
		req.addParameter("purge", "");
		handle(req);
		Assert.assertNull(service.getConceptClassByUuid(uuid));
	}
	
	@Test
	public void shouldNotIncludeTheAuditInfoForTheDefaultRepresentation() throws Exception {
		String uuid = "97097dd9-b092-4b68-a2dc-e5e5be961d42";
		
		Object result = deserialize(handle(newGetRequest(getURI() + "/" + uuid)));
		Assert.assertNotNull(result);
		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	@Test
	public void shouldIncludeTheAuditInfoForTheFullRepresentation() throws Exception {
		String uuid = "97097dd9-b092-4b68-a2dc-e5e5be961d42";
		MockHttpServletRequest httpReq = newGetRequest(getURI() + "/" + uuid, new Parameter(
		        RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL));
		Object result = deserialize(handle(httpReq));
		Assert.assertNotNull(result);
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "conceptclass";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return ResourceTestConstants.CONCEPT_CLASS_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return Context.getConceptService().getAllConceptClasses().size();
	}
	
}
