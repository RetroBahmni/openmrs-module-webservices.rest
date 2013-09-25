package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class PersonAttributeTypeResource1_8UnitTest {

    @Mock
    private ConceptService conceptService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockStatic(Context.class);
    }

    @Test
    public void shouldSetConceptForFormatConcept() throws Exception {
        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setFormat(Concept.class.getCanonicalName());

        Concept answer = new Concept();
        when(conceptService.getConcept(personAttributeType.getForeignKey())).thenReturn(answer);

        when(Context.getConceptService()).thenReturn(conceptService);

        assertEquals(answer, new PersonAttributeTypeResource1_8().getConcept(personAttributeType));
    }

    @Test
    public void shouldNotSetConceptForNonConceptFormats() throws Exception {
        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setFormat("not-a-concept");

        Concept answer = new Concept();
        when(conceptService.getConcept(personAttributeType.getForeignKey())).thenReturn(answer);

        when(Context.getConceptService()).thenReturn(conceptService);

        assertEquals(null, new PersonAttributeTypeResource1_8().getConcept(personAttributeType));
    }
}
