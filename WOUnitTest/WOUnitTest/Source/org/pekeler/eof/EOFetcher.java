/**
Copyright (c) 2004-2006, Christian Pekeler
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package org.pekeler.eof;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eoaccess.*;
import java.util.*;


/**
* <code>EOFetcher</code> is a utility class for fetching EOs. It can be used instead of <code>EOEditingContext.objectsWithFetchSpecification()</code> and various methods in <code>EOUtilities</code> (in fact, <code>EOFetcher</code>'s api has been modeled after <code>EOUtilities</code>). <code>EOFetchSpecification</code>'s main purpose is to help retrieving EOs from the DB. <code>EOFetcher</code>'s main purpose is to also enable in-memory fetches. There are several practical applications:
* <p>
* <b>Unit Testing</b>. In order for unit tests to run fast, they should not access the DB. However, if the production code under test is using <code>EOFetchSpecification</code>s, this is hard to accomplish. That's why is has been very common to write unit tests for WO projects which set up test objects in a database and then test the production code against the DB. For example:
* <pre>
*      public NSArray static someObjects(EOEditingContext editingContext) {
*          return EOUtilities.objectsWithFetchSpecification(editingContext, "SomeEntity", "someObjects");
*      }
* ...
*      public void testSomeObjects() {
*          SomeEntity someEntity = new SomeEntity();
*          editingContext().insertObject(someEntity);
*          registerPersistentRootObjectForDeletion(someEntity);
*          // make someEntity disqualify
*          editingContext().saveChanges();
*          assertFalse(SomeEntity.someObjects(editingContext()).containsObject(someEntity));
*          // make someEntity qualify
*          editingContext().saveChanges();
*          assertTrue(SomeEntity.someObjects(editingContext()).containsObject(someEntity));
*      }
* </pre>
* Using <code>EOFetcher</code>, the test becomes simpler and faster, because it doesn't access the DB anymore:
* <pre>
*      public NSArray static someObjects(EOEditingContext editingContext) {
*          return EOFetcher.objectsWithFetchSpecificationName(editingContext, "SomeEntity", "someObjects", EOFetcher.DBEC);
*      }
* ...
*      public void testSomeObjects() {
*          SomeEntity someEntity = (SomeEntity)mockEditingContext().createSavedObject("SomeEntity");
*          // make someEntity disqualify
*          assertFalse(SomeEntity.someObjects(mockEditingContext()).containsObject(someEntity));
*          // make someEntity qualify
*          assertTrue(SomeEntity.someObjects(mockEditingContext()).containsObject(someEntity));
*      }
* </pre>
* <p>
* <b>Validation</b>. Let's say the entity Person has an attribute firstName and lastName, and each person needs to have a unique full name. A common implementation looks as follows:
* <pre>
*      public void validateForSave() throws NSValidation.ValidationException {
*          super.validateForSave();
*          NSDictionary keyValueDictionary = new NSDictionary(new Object[] {firstName(), lastName()}, new String[] {"firstName", "lastName"});
*          NSMutableArray objects = EOUtilities.objectsMatchingValues(editingContext(), "Person", keyValueDictionary).mutableClone();
*          objects.removeObject(this);
*          if (objects.count() > 0)
*              throw new NSValidation.ValidationException("name is not unique");
*      }
* </pre>
* There are two problems with this implementation. First, it's not easily unit-testable because it is working against the database. Second, it won't validate against other unsaved persons. This is a problem when more than one persons are being edited or created at the same time. The alternative implementation using <code>EOFetcher</code> solves these two issues and looks as follows:
* <pre>
*      public void validateForSave() throws NSValidation.ValidationException {
*          super.validateForSave();
*          NSDictionary keyValueDictionary = new NSDictionary(new Object[] {firstName(), lastName()}, new String[] {"firstName", "lastName"});
*          if (EOFetcher.objectNotUniqueForMatchingValues(this, keyValueDictionary))
*              throw new NSValidation.ValidationException("name is not unique");
*      }
* </pre>
* <p>
* If an EO exists in the editing context but not yet in the DB, <code>EOFetcher</code> will find that object when used with the <code>DBEC</code> or <code>EC</code> store. For the <code>DB</code> store, the object will be ignored just like EOUtilities would ignored it.
* <p>
* If an EO exists in the DB, and has been deleted by an editing context but not yet saved, <code>EOFetcher</code> will ignore that object when used with the <code>DBEC</code> or <code>EC</code> store. For the <code>DB</code> store, the object will be returned just like EOUtilities would return it.
*/

public class EOFetcher {
    /**
    * Store for fetching from the database.
    */
    public static Store DB = new DatabaseStore();
    /**
    * Store for fetching from the editing context.
    */
    public static Store EC = new EditingContextStore();
    /**
    * Store for fetching from the database and editing context.
    */
    public static Store DBEC = new DatabaseAndEditingContextStore();
    
    public abstract static class Store {
        public abstract NSArray objectsWithFetchSpecification(EOEditingContext anEditingContext, EOFetchSpecification aFetchSpecification);
    }
    
    protected static class DatabaseStore extends Store {
        public NSArray objectsWithFetchSpecification(EOEditingContext anEditingContext, EOFetchSpecification aFetchSpecification) {
            return anEditingContext.objectsWithFetchSpecification(aFetchSpecification, anEditingContext);
        }
    }

    protected static class EditingContextStore extends Store {
        public NSArray objectsWithFetchSpecification(EOEditingContext anEditingContext, EOFetchSpecification aFetchSpecification) {
            NSArray objects = withoutDeletedObjects(anEditingContext, rawObjectsWithFetchSpecification(anEditingContext, aFetchSpecification));
            NSArray sortedObjects = EOSortOrdering.sortedArrayUsingKeyOrderArray(objects, aFetchSpecification.sortOrderings());
            int fetchLimit = aFetchSpecification.fetchLimit();
            if (fetchLimit > 0 && fetchLimit < objects.count())
                return sortedObjects.subarrayWithRange(new NSRange(0, fetchLimit));
            else
                return sortedObjects;
        }

        protected void addEntityToQualifiers(EOEntity entity, NSMutableArray entityQualifiers, boolean isDeep) {
            entityQualifiers.addObject(new EOKeyValueQualifier("entityName", EOQualifier.QualifierOperatorEqual, entity.name()));
            if (isDeep) {
                Enumeration subEntityEnum = entity.subEntities().objectEnumerator();
                while (subEntityEnum.hasMoreElements())
                    addEntityToQualifiers((EOEntity)subEntityEnum.nextElement(), entityQualifiers, isDeep);
            }
        }
        
        protected NSArray registeredObjectsOfEntityNamed(EOEditingContext anEditingContext, String anEntityName, boolean isDeep) {
            NSMutableArray entityQualifiers = new NSMutableArray();
            addEntityToQualifiers(EOUtilities.entityNamed(anEditingContext, anEntityName), entityQualifiers, isDeep);
            Enumeration registeredObjectsEnum = EOQualifier.filteredArrayWithQualifier(anEditingContext.registeredObjects(), new EOOrQualifier(entityQualifiers)).objectEnumerator();
            NSMutableArray nonFaultObjects = new NSMutableArray();
            while (registeredObjectsEnum.hasMoreElements()) {
                EOCustomObject object = (EOCustomObject)registeredObjectsEnum.nextElement();
                if (!object.isFault())
                    nonFaultObjects.addObject(object);
            }
            return nonFaultObjects;		
        }

        protected NSArray withoutDeletedObjects(EOEditingContext anEditingContext, NSArray objects) {
            NSMutableArray filteredObjects = objects.mutableClone();
            filteredObjects.removeObjectsInArray(anEditingContext.deletedObjects());
            return filteredObjects;
        }
    
        protected NSArray rawObjectsWithFetchSpecification(EOEditingContext anEditingContext, EOFetchSpecification aFetchSpecification) {
            return EOQualifier.filteredArrayWithQualifier(registeredObjectsOfEntityNamed(anEditingContext, aFetchSpecification.entityName(), aFetchSpecification.isDeep()), aFetchSpecification.qualifier());
        }

    }

    protected static class DatabaseAndEditingContextStore extends EditingContextStore {
        public NSArray objectsWithFetchSpecification(EOEditingContext anEditingContext, EOFetchSpecification aFetchSpecification) {
            if (EOUtilities.entityNamed(anEditingContext, aFetchSpecification.entityName()).cachesObjects())
                return EOFetcher.DB.objectsWithFetchSpecification(anEditingContext, aFetchSpecification);
            return super.objectsWithFetchSpecification(anEditingContext, aFetchSpecification);
        }
        
        protected NSArray rawObjectsWithFetchSpecification(EOEditingContext anEditingContext, EOFetchSpecification aFetchSpecification) {
            NSArray qualifiedObjectsFromEC = super.rawObjectsWithFetchSpecification(anEditingContext, aFetchSpecification);
            NSMutableArray objects = EOFetcher.DB.objectsWithFetchSpecification(anEditingContext, aFetchSpecification).mutableClone();
            objects.removeObjectsInArray(anEditingContext.updatedObjects()); // updated objects might not qualify anymore, those which still do will be readded in the next line
            objects.addObjectsFromArray(qualifiedObjectsFromEC);
            return new NSSet(objects).allObjects();
        }
    }


    public static NSArray objectsWithFetchSpecification(EOEditingContext anEditingContext, EOFetchSpecification aFetchSpecification, Store aStore) {
        if (aFetchSpecification.fetchesRawRows())
            throw new UnsupportedOperationException("EOFetcher doesn't support fetching of raw rows");
        return aStore.objectsWithFetchSpecification(anEditingContext, aFetchSpecification);
    }
    
    public static NSArray objectsWithFetchSpecificationName(EOEditingContext anEditingContext, String anEntityName, String aFetchSpecificationName, Store aStore) {
        EOFetchSpecification fetchSpecification = EOUtilities.modelGroup(anEditingContext).fetchSpecificationNamed(aFetchSpecificationName, anEntityName);
        if (fetchSpecification == null)
            throw new EOObjectNotAvailableException("objectsWithFetchSpecificationNameAndBindings: Fetch specification '" + aFetchSpecificationName + "' not found in entity named '" + anEntityName + "'");
        return aStore.objectsWithFetchSpecification(anEditingContext, fetchSpecification);
    }
    
    public static NSArray objectsMatchingValues(EOEditingContext anEditingContext, String anEntityName, NSDictionary aValueDictionary, Store aStore) {
        EOQualifier qualifier = EOQualifier.qualifierToMatchAllValues(aValueDictionary);
        EOFetchSpecification fetchSpec = new EOFetchSpecification(anEntityName, qualifier, null);
        return objectsWithFetchSpecification(anEditingContext, fetchSpec, aStore);
    }
    
    public static NSArray objectsMatchingKeyAndValue(EOEditingContext anEditingContext, String anEntityName, String aKey, Object aValue, Store aStore) {
        return objectsMatchingValues(anEditingContext, anEntityName, new NSDictionary(aValue, aKey), aStore);
    }
    
    public static NSArray objectsWithFetchSpecificationNameAndBindings(EOEditingContext anEditingContext, String anEntityName, String aFetchSpecificationName, NSDictionary aBindingsDictionary, Store aStore) {
        EOFetchSpecification fetchSpecification = EOUtilities.modelGroup(anEditingContext).fetchSpecificationNamed(aFetchSpecificationName, anEntityName);
        if (fetchSpecification == null)
            throw new EOObjectNotAvailableException("objectsWithFetchSpecificationNameAndBindings: Fetch specification '" + aFetchSpecificationName + "' not found in entity named '" + anEntityName + "'");
        EOFetchSpecification fetchSpecificationWithQualifierBindings = fetchSpecification.fetchSpecificationWithQualifierBindings(aBindingsDictionary);
        return objectsWithFetchSpecification(anEditingContext, fetchSpecificationWithQualifierBindings, aStore);
    }

    public static NSArray objectsForEntityNamed(EOEditingContext anEditingContext, String anEntityName, Store aStore) {
        EOFetchSpecification fetchSpec = new EOFetchSpecification(anEntityName, null, null);
        return objectsWithFetchSpecification(anEditingContext, fetchSpec, aStore);
    }
    
    
// optimization idea: objectNotUnique*() methods could be optimized by using the internal methods in-memory only and only go to the DB using EOUtilities if no objects were found

    public static boolean objectNotUniqueForMatchingValues(EOEnterpriseObject anObject, NSDictionary aValueDictionary) {
        NSMutableArray objects = objectsMatchingValues(anObject.editingContext(), anObject.entityName(), aValueDictionary, DBEC).mutableClone();
        objects.removeObject(anObject);
        return (objects.count() > 0);
    }
    
    public static boolean objectNotUniqueForMatchingKeyAndValue(EOEnterpriseObject anObject, String aKey, Object aValue) {
        NSMutableArray objects = objectsMatchingKeyAndValue(anObject.editingContext(), anObject.entityName(), aKey, aValue, DBEC).mutableClone();
        objects.removeObject(anObject);
        return (objects.count() > 0);
    }
    
    public static boolean objectNotUniqueForFetchSpecificationNameAndBindings(EOEnterpriseObject anObject, String aFetchSpecificationName, NSDictionary aBindingsDictionary) {
        NSMutableArray objects = objectsWithFetchSpecificationNameAndBindings(anObject.editingContext(), anObject.entityName(), aFetchSpecificationName, aBindingsDictionary, DBEC).mutableClone();
        objects.removeObject(anObject);
        return (objects.count() > 0);
    }
    
}
