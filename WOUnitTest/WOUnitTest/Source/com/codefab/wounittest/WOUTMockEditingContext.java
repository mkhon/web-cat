/**
Copyright (c) 2001-2006, CodeFab, Inc. and individual contributors
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the CodeFab, Inc. nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 
 
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/

package com.codefab.wounittest;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eoaccess.*;
import java.util.*;
import org.pekeler.eof.*;


/**
* <code>WOUTMockEditingContext</code> is a subclass of <code>EOEditingContext</code>
* that can be used for fast in-memory testing of business objects.
* <p>
* Unit tests for logic which use fetch specifications or save to an editing context
* can be relative slow because full roundtrips to a database are being performed.
* Testing the same logic with a WOUTMockEditingContext ensures that nothing is being
* saved to or read from a database resulting in shorter execution time of the unit test.
* Also, you don't risk invalidating the persistent data with broken test cases.
* <p>
* Assuming there is an <code>EOCustomObject</code> called <code>Person</code> with the
* attributes <code>name</code>, <code>age</code>, <code>nationality</code> and
* <code>partner</code>. <code>nationality</code> is of entity <code>Country</code>
* for which there is a constant list of objects in the database. We want to write a test for the 
* static Person method <code>createFromDescription(String, EOEditingContext)</code>.
* <pre>
*  import com.codefab.wounittest.*;
*
*  public class PersonTest extends WOUTTestCase {
*      Person jane;
*
*      protected void setUp() throws Exception {
*          super.setUp();
*          mockEditingContext().setEntityNamesToGetFromDatabase(new NSArray("Country"));
*          jane = (Person)mockEditingContext().createSavedObject("Person");
*          jane.setName("Jane Doe");
*          jane.addObjectToBothSidesOfRelationshipWithKey("nationality", Country.withName("Canada", mockEditingContext()));
*     }
*
*      public void testCreationFromDescriptionWithExistingPartner() {
*          Person newPerson = Person.createFromDescription("John Doe, 34, Canada, Jane Doe", mockEditingContext());
*          assertEquals("John Doe", newPerson.name());
*          assertEquals(34, newPerson.age());
*          assertSame(Country.withName("Canada", mockEditingContext()), newPerson.nationality());
*          assertSame(jane, newPerson.partner());
*      }
*
*      public void testCreationFromDescriptionWithUnknownPartner() {
*          Person newPerson = Person.createFromDescription("Bla Fasel, 12, Germany, Jeniffer Doe", mockEditingContext());
*          assertEquals("Blah Fasel", newPerson.name());
*          assertEquals(12, newPerson.age());
*          assertSame(Country.withName("Germany", mockEditingContext()), newPerson.nationality());
*          assertNull(newPerson.partner());
*      }
*
*      public void testCreationFromDescriptionWithUnknownCountry() {
*          try {
*              Person.createFromDescription("Bla Fasel, 12, Wawaland, Jane Doe", mockEditingContext());
*              fail();
*          } catch(UnknownCountryException e) {}
*      }
*
*  }
* </pre>
* <p>
* Additional information can be found in
* <a href="http://cvs.sourceforge.net/viewcvs.py/wounittest/WOUnitTest/Test/WOUnitTestTest/WOUTMockEditingContextTestCase.java?rev=HEAD">WOUTMockEditingContextTestCase.java</a>.
*/

public class WOUTMockEditingContext extends EOEditingContext {
    protected static int fakePrimaryKeyCounter = 1;
    protected NSMutableArray ignoredObjects = new NSMutableArray();
    protected NSArray entityNamesToGetFromDatabase = new NSArray();

    /**
    * Constructs a WOUTMockEditingContext. Using a <code>MockObjectStore</code> as parent object store.
    */
    public WOUTMockEditingContext() {
        _initWithParentObjectStore(new MockObjectStore());
    }
    
    /**
    * Defines which entities should be fetched from the rootObjectStore.
    * This can be useful for tests which depend on some data being present in the database.
    * @param  theEntityNamesToGetFromDatabase  array of entity names which should be fetched from the database
    */
    public void setEntityNamesToGetFromDatabase(NSArray theEntityNamesToGetFromDatabase) {
        entityNamesToGetFromDatabase = theEntityNamesToGetFromDatabase;
    }
    
    /**
    * Overwritten to return the <code>defaultParentObjectStore</code>.
    * @see    com.webobjects.eocontrol.EOEditingContext EOEditingContext.defaultParentObjectStore() 
    */
    public EOObjectStore rootObjectStore() {
        return EOEditingContext.defaultParentObjectStore();
    }
    
    /**
    * Overrides the implementation inherited from EOEditingContext to fetch objects from the array of <code>registeredObjects</code> of the receiver instead of going to the database.
    * Only entities defined with <code>setEntityNamesToGetFromDatabase</code> are still being fetched from the database using the <code>rootObjectStore</code>.
    * Throws <code>UnsupportedOperationException</code> if <code>aFetchSpecification</code> is configured to return raw rows.
    * Hints are ignored.
    * @param  aFetchSpecification  the criteria specified for fetch
    * @param  anEditingContext  the destination EOEditingContext, needs to be the same as the receiver
    */
    public NSArray objectsWithFetchSpecification(EOFetchSpecification aFetchSpecification, EOEditingContext anEditingContext) {
        if (entityNamesToGetFromDatabase.containsObject(aFetchSpecification.entityName()))
            return rootObjectStore().objectsWithFetchSpecification(aFetchSpecification, anEditingContext);
        if (anEditingContext != this)
            throw new IllegalArgumentException("MockEditingContext doesn't support other editing contexts");
        return EOFetcher.objectsWithFetchSpecification(anEditingContext, aFetchSpecification, EOFetcher.EC);
    }
 
    /**
    * Convenience cover method for <code>insertSavedObject</code>.
    * Creates a new Custom Object for the specified entity, inserts it into the receiver using <code>insertSavedObject</code>, and returns the new object.
    * @param  anEntityName  the name of entity
    */
    public EOCustomObject createSavedObject(String anEntityName) {
        EOEntity entity = EOUtilities.entityNamed(this, anEntityName);
        EOEnterpriseObject object = entity.classDescriptionForInstances().createInstanceWithEditingContext(this, null);
        if (!(object instanceof EOCustomObject))
            throw new IllegalArgumentException("The entity is not an EOCustomObject and can't be used with createSavedObject().");
        insertSavedObject((EOCustomObject)object);
        return (EOCustomObject)object;
    }
    
    /**
    * Inserts a Custom Object into the receiver and makes it look as if it was fetched from the database.
    * The object will get a non-temporary global id.
    * The receiver will not observe the object as defined in EOObserving, which means after changing the object, the receiver will not validate or save it.
    * Doesn't works with EOCustomObject subclasses that have a compound primary key.
    * Note that awakeFromInsertion() will be called on the object because that method often contains useful initialization, and awakeFromFetch() will NOT be called because it might depend on data that we don't care to set up.
    * @param  anObject  the Custom Object
    */
    public void insertSavedObject(EOCustomObject anObject) {
        recordObject(anObject, assignFakeGlobalIDToObject(anObject));
        anObject.awakeFromInsertion(this);
        ignoredObjects.addObject(anObject);
    }
    
    /**
    * Internal helper method for <code>insertSavedObject</code>.
    */
    protected EOGlobalID assignFakeGlobalIDToObject(EOCustomObject anObject) {
        EOEntity entity = EOUtilities.entityNamed(this, anObject.entityName());
        NSArray primaryKeyAttributes = entity.primaryKeyAttributes();
        if (primaryKeyAttributes.count() != 1)
            throw new IllegalArgumentException(entity.name() + " has a compound primary key and can't be used with insertSavedObject().");
        NSDictionary primaryKeyDictionary = new NSDictionary(new Integer(fakePrimaryKeyCounter++), ((EOAttribute)primaryKeyAttributes.objectAtIndex(0)).name());
        EOGlobalID globalID = entity.globalIDForRow(primaryKeyDictionary);
        anObject.__setGlobalID(globalID);
        return globalID;
    }
    
    /**
    * Overrides the implementation inherited from EOEditingContext to ignore objects registered with <code>insertSavedObject</code>.
    * @param  anObject  the object whose state is to be recorded
    */
    public void objectWillChange(Object anObject) {
        if (!ignoredObjects.containsObject(anObject))
            super.objectWillChange(anObject);
    }

    /**
    * Extends the implementation inherited from EOEditingContext to delete ignoredObjects.
    */
    public void dispose() {
        ignoredObjects.removeAllObjects();
        ignoredObjects = null;
        super.dispose();
    }

}

class MockObjectStore extends EOObjectStore {
    
    public EOEnterpriseObject faultForGlobalID(EOGlobalID aGlobalid, EOEditingContext anEditingcontext) {
        return null;
    }
    
    public EOEnterpriseObject faultForRawRow(NSDictionary aRow, String anEntityName, EOEditingContext anEditingContext) {
        return null;
    }
    
    public NSArray arrayFaultWithSourceGlobalID(EOGlobalID aGlobalId, String aRelationshipName, EOEditingContext anEditingContext) {
        return NSArray.EmptyArray;
    }
    
    public NSArray objectsForSourceGlobalID(EOGlobalID aGlobalId, String aRelationshipName, EOEditingContext anEditingContext) {
        return NSArray.EmptyArray;
    }
    
    public NSArray objectsWithFetchSpecification(EOFetchSpecification aFetchSpecification, EOEditingContext anEditingContext) {
        return NSArray.EmptyArray;
    }
    
    public boolean isObjectLockedWithGlobalID(EOGlobalID aGlobalId, EOEditingContext anEditingContext) {
        return false;
    }
    
    public void initializeObject(EOEnterpriseObject anEnterpriseObject, EOGlobalID aGlobalId, EOEditingContext anEditingContext) {
        if (((WOUTMockEditingContext)anEditingContext).entityNamesToGetFromDatabase.containsObject(anEnterpriseObject.entityName())) {
            EOObjectStore rootObjectStore = anEditingContext.rootObjectStore();
            rootObjectStore.lock();
            try {
                rootObjectStore.initializeObject(anEnterpriseObject, aGlobalId, anEditingContext);
            } finally {
                rootObjectStore.unlock();
            }
        }
    }

    public void lockObjectWithGlobalID(EOGlobalID aGlobalId, EOEditingContext anEditingContext) {}
    public void lock() {}
    public void unlock() {}
    public void refaultObject(EOEnterpriseObject anEnterpriseObject, EOGlobalID aGlobalId, EOEditingContext anEditingContext) {}
    public void invalidateObjectsWithGlobalIDs(NSArray globalIds) {}
    public void invalidateAllObjects() {}
    public void saveChangesInEditingContext(EOEditingContext anEditingContext) {}
    public void editingContextDidForgetObjectWithGlobalID(EOEditingContext anEditingContext, EOGlobalID aGlobalId) {}
    
}
