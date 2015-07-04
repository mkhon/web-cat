/**
Copyright (c) 2001-2006, CodeFab, Inc. and individual contributors
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the CodeFab, Inc. nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 
 
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/

package com.codefab.testwounittest;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eoaccess.*;
import com.codefab.wounittest.*;


public class WOUTMockEditingContextTestCase extends WOUTTestCase {

    public void testInstance() {
        EOEnterpriseObject entityA1 = EOUtilities.createAndInsertInstance(mockEditingContext(), "EntityA");
        entityA1.takeValueForKey("a", "name");
        
        assertEquals(1, mockEditingContext().insertedObjects().count());
        assertEquals(1, mockEditingContext().registeredObjects().count());
        mockEditingContext().saveChanges();
        assertEquals(0, mockEditingContext().insertedObjects().count());
        assertEquals(1, mockEditingContext().registeredObjects().count());
        
        EOEnterpriseObject entityA2 = EOUtilities.createAndInsertInstance(mockEditingContext(), "EntityA");
        entityA2.takeValueForKey("b", "name");
        assertEquals(2, mockEditingContext().registeredObjects().count());
        
        NSArray persons = mockEditingContext().objectsWithFetchSpecification(new EOFetchSpecification("EntityA", null, null));
        assertEquals(2, persons.count());
        
        persons = mockEditingContext().objectsWithFetchSpecification(new EOFetchSpecification("EntityA", new EOKeyValueQualifier("name", EOQualifier.QualifierOperatorEqual, "b"), null));
        assertEquals(1, persons.count());
        assertSame(entityA2, (EOEnterpriseObject)persons.lastObject());
        
        persons = mockEditingContext().objectsWithFetchSpecification(new EOFetchSpecification("EntityA", null, new NSArray(new EOSortOrdering("name", EOSortOrdering.CompareAscending))));
        assertEquals(2, persons.count());
        assertSame(entityA2, (EOEnterpriseObject)persons.lastObject());
        
        persons = mockEditingContext().objectsWithFetchSpecification(new EOFetchSpecification("EntityA", null, new NSArray(new EOSortOrdering("name", EOSortOrdering.CompareDescending))));
        assertEquals(2, persons.count());
        assertSame(entityA1, (EOEnterpriseObject)persons.lastObject());
        
        EOGlobalID globalID = mockEditingContext().globalIDForObject(entityA2);
        assertNotNull(globalID);
        assertSame(entityA2, mockEditingContext().objectForGlobalID(globalID));
        
        mockEditingContext().deleteObject(entityA1);
        mockEditingContext().saveChanges();
        assertEquals(1, mockEditingContext().registeredObjects().count());
        
        entityA2.takeValueForKey("c", "name");
        mockEditingContext().revert();
        assertEquals(1, mockEditingContext().registeredObjects().count());
        assertEquals("b", entityA2.valueForKey("name"));
    }

    public void testSavedObjects() {
        EOEnterpriseObject savedObject = mockEditingContext().createSavedObject("EntityA");
        assertNotNull(savedObject);
        assertFalse(mockEditingContext().insertedObjects().containsObject(savedObject));
        assertFalse(mockEditingContext().updatedObjects().containsObject(savedObject));
        assertTrue(mockEditingContext().registeredObjects().containsObject(savedObject));
        
        savedObject.takeValueForKey("a new value", "name");
        assertFalse(mockEditingContext().updatedObjects().containsObject(savedObject));
        assertEquals("a new value", savedObject.valueForKey("name"));

        EOGlobalID globalID = mockEditingContext().globalIDForObject(savedObject);
        assertNotNull(globalID);
        assertFalse(globalID.isTemporary());
        NSDictionary primaryKeyDictionary = EOUtilities.primaryKeyForObject(mockEditingContext(), savedObject);
        assertNotNull(primaryKeyDictionary);
        assertEquals(1, primaryKeyDictionary.count());
        
        EOEnterpriseObject savedObject2 = mockEditingContext().createSavedObject("EntityA");
        NSDictionary primaryKeyDictionary2 = EOUtilities.primaryKeyForObject(mockEditingContext(), savedObject2);
        assertTrue(primaryKeyDictionary.allKeys().equals(primaryKeyDictionary2.allKeys()));
        assertFalse(primaryKeyDictionary.allValues().equals(primaryKeyDictionary2.allValues()));
    }
    
    public void testObjectsFromDatabase() {
        EOEnterpriseObject entityA1 = EOUtilities.createAndInsertInstance(editingContext(), "EntityA");
        entityA1.takeValueForKey("objectInDB", "name");
        registerPersistentRootObjectForDeletion(entityA1);
        editingContext().saveChanges();
        
        EOFetchSpecification fetchSpecification = new EOFetchSpecification("EntityA", new EOKeyValueQualifier("name", EOQualifier.QualifierOperatorEqual, "objectInDB"), null);
        assertEquals(0, mockEditingContext().objectsWithFetchSpecification(fetchSpecification).count());
        mockEditingContext().setEntityNamesToGetFromDatabase(new NSArray("EntityA"));
        NSArray objects = mockEditingContext().objectsWithFetchSpecification(fetchSpecification);
        assertEquals(1, objects.count());
        assertEquals("objectInDB", ((EOEnterpriseObject)objects.lastObject()).valueForKey("name"));
    }

    public void testFetchesRawRows() {
        EOFetchSpecification fetchSpecification = new EOFetchSpecification("EntityA", null, null);
        fetchSpecification.setFetchesRawRows(true);
        try {
            mockEditingContext().objectsWithFetchSpecification(fetchSpecification);
            fail();
        } catch (UnsupportedOperationException exception) {
            assertEquals("EOFetcher doesn't support fetching of raw rows", exception.getMessage());
        }
    }
    
    public void testFetchLimit() {
        mockEditingContext().createSavedObject("EntityA");
        mockEditingContext().createSavedObject("EntityA");
        mockEditingContext().createSavedObject("EntityA");
        mockEditingContext().createSavedObject("EntityA");
        EOFetchSpecification fetchSpecification = new EOFetchSpecification("EntityA", null, null);
        fetchSpecification.setFetchLimit(0);
        assertEquals(4, mockEditingContext().objectsWithFetchSpecification(fetchSpecification).count());
        fetchSpecification.setFetchLimit(1);
        assertEquals(1, mockEditingContext().objectsWithFetchSpecification(fetchSpecification).count());
        fetchSpecification.setFetchLimit(4);
        assertEquals(4, mockEditingContext().objectsWithFetchSpecification(fetchSpecification).count());
        fetchSpecification.setFetchLimit(5);
        assertEquals(4, mockEditingContext().objectsWithFetchSpecification(fetchSpecification).count());
    }
    
    public void testSortingWithFetchLimit() {
        EOEnterpriseObject entityAa = mockEditingContext().createSavedObject("EntityA");
        entityAa.takeValueForKey("a", "name");
        EOEnterpriseObject entityAb = mockEditingContext().createSavedObject("EntityA");
        entityAb.takeValueForKey("b", "name");
        EOEnterpriseObject entityAc = mockEditingContext().createSavedObject("EntityA");
        entityAc.takeValueForKey("c", "name");

        EOFetchSpecification fetchSpecification = new EOFetchSpecification("EntityA", null,  new NSArray(new EOSortOrdering("name", EOSortOrdering.CompareAscending)));
        fetchSpecification.setFetchLimit(1);
        assertSame(entityAa, mockEditingContext().objectsWithFetchSpecification(fetchSpecification).lastObject());
        
        fetchSpecification = new EOFetchSpecification("EntityA", null,  new NSArray(new EOSortOrdering("name", EOSortOrdering.CompareDescending)));
        fetchSpecification.setFetchLimit(1);
        assertSame(entityAc, mockEditingContext().objectsWithFetchSpecification(fetchSpecification).lastObject());
    }
    
    public void testDeepFetching() {
        EOEnterpriseObject entityA = mockEditingContext().createSavedObject("EntityA");
        EOEnterpriseObject entityAChild = mockEditingContext().createSavedObject("EntityAChild");
        EOEnterpriseObject entityAGrandchild = mockEditingContext().createSavedObject("EntityAGrandchild");
        EOFetchSpecification fetchSpecification = new EOFetchSpecification("EntityA", null, null);
        fetchSpecification.setIsDeep(false);
        NSArray fetchedObjects = mockEditingContext().objectsWithFetchSpecification(fetchSpecification);
        assertEquals(1, fetchedObjects.count());
        assertTrue(fetchedObjects.containsObject(entityA));

        fetchSpecification.setIsDeep(true);
        fetchedObjects = mockEditingContext().objectsWithFetchSpecification(fetchSpecification);
        assertEquals(3, fetchedObjects.count());
        assertTrue(fetchedObjects.containsObject(entityA));
        assertTrue(fetchedObjects.containsObject(entityAChild));
        assertTrue(fetchedObjects.containsObject(entityAGrandchild));
        
        fetchSpecification = new EOFetchSpecification("EntityAGrandchild", null, null);
        assertTrue(fetchSpecification.isDeep());
        fetchedObjects = mockEditingContext().objectsWithFetchSpecification(fetchSpecification);
        assertEquals(1, fetchedObjects.count());
        assertTrue(fetchedObjects.containsObject(entityAGrandchild));
    }
    
    public void testInvalidEntity() {
        EOFetchSpecification fetchSpecification = new EOFetchSpecification("UnknownEntity", null, null);
        try {
            mockEditingContext().objectsWithFetchSpecification(fetchSpecification);
            fail();
        } catch (EOObjectNotAvailableException exception) {}
        
        fetchSpecification = new EOFetchSpecification(null, null, null);
        try {
            mockEditingContext().objectsWithFetchSpecification(fetchSpecification);
            fail();
        } catch (EOObjectNotAvailableException exception) {}
    }

}
