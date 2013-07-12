package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptNumeric;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.test.annotation.ExpectedException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ConceptResource1_9Test extends BaseModuleWebContextSensitiveTest {

    @Test
    public void shouldReturnAtleast4LevelsOfConceptSets() {
        Concept concept = createConcept("mainConceptUuid");
        List<Concept> level1Concepts = createSingleConceptSet(concept, "level1");
        List<Concept> level2Concepts = createConceptsSets(level1Concepts, "level2");
        List<Concept> level3Concepts = createConceptsSets(level2Concepts, "level3");
        createConceptsSets(level3Concepts, "level4");

        SimpleObject simpleObject = new ConceptResource1_9().asFull(concept);

        assertEquals(simpleObject.get("uuid"), "mainConceptUuid");

        SimpleObject firstLevel1Member = assertLevel(simpleObject, 1);
        SimpleObject firstLevel2Member = assertLevel(firstLevel1Member, 2);
        assertLevel(firstLevel2Member, 3);
    }

    @Test
    @Ignore ("Never run unless you want an OutOfMemoryError")
    public void shouldFailForCircularReferencesInConcepts() {
        Concept concept = createConcept("mainConceptUuid");
        concept.addSetMember(concept);
        SimpleObject simpleObject = new ConceptResource1_9().asFull(concept);
        System.out.println(simpleObject);
    }

    private List<Concept> createSingleConceptSet(Concept concept, String uuid) {
        for (int i = 0; i < 2; i++) {
            addSetMember(concept, uuid);
        }
        return concept.getSetMembers();
    }

    private List<Concept> createConceptsSets(List<Concept> concepts, String uuid) {
        List<Concept> membersList = new ArrayList<Concept>();
        for (Concept concept : concepts) {
            List<Concept> mainConceptSet = createSingleConceptSet(concept, uuid);
            membersList.addAll(mainConceptSet);
        }
        return membersList;
    }

    private Concept createConcept(String uuid) {
        Concept concept = new ConceptNumeric();
        ConceptDatatype conceptDatatype = new ConceptDatatype();
        conceptDatatype.setName("Numeric");
        concept.setDatatype(conceptDatatype);
        concept.setDateChanged(new Date());
        concept.setUuid(uuid);
        concept.setSet(true);
        return concept;
    }

    private SimpleObject assertLevel(SimpleObject simpleObject, int level) {
        List levelMembers = (List) simpleObject.get("setMembers");
        assertEquals(levelMembers.size(), 2);
        SimpleObject firstLevelMember = (SimpleObject) levelMembers.get(0);
        assertEquals(firstLevelMember.get("uuid"), "level" + level);
        return firstLevelMember;
    }

    private Concept addSetMember(Concept concept, String uuid) {
        Concept setMember = new Concept();
        setMember.setUuid(uuid);
        concept.addSetMember(setMember);
        return setMember;
    }
}
