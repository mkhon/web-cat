/**
Copyright (c) 2004-2006, Christian Pekeler
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package org.pekeler.eof;

import com.codefab.wounittest.*;
import org.pekeler.eof.*;
import com.codefab.testwounittest.*;
import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eoaccess.*;


public class EOFetcherTestCase extends WOUTTestCase {
    // see high-level tests in WOUTMockEditingContextTestCase and ThingTestCase

    protected Thing savedThing() {
        Thing thing = new Thing();
        editingContext().insertObject(thing);
        thing.setName("" + new NSTimestamp().getTime());
        thing.setType(new Integer(2));
        registerPersistentRootObjectForDeletion(thing);
        editingContext().saveChanges();
        return thing;
    }

    public void testFetchOfNonSavedObject() {
        Thing thing = new Thing();
        editingContext().insertObject(thing);
        assertTrue(EOFetcher.objectsForEntityNamed(editingContext(), "Thing", EOFetcher.EC).containsObject(thing));
        assertFalse(EOFetcher.objectsForEntityNamed(editingContext(), "Thing", EOFetcher.DB).containsObject(thing));
        assertTrue(EOFetcher.objectsForEntityNamed(editingContext(), "Thing", EOFetcher.DBEC).containsObject(thing));
    }
    
    public void testFetchOfSavedObject() {
        Thing thing = savedThing();
        assertTrue(EOFetcher.objectsForEntityNamed(editingContext(), "Thing", EOFetcher.EC).containsObject(thing));
        assertTrue(EOFetcher.objectsForEntityNamed(editingContext(), "Thing", EOFetcher.DB).containsObject(thing));
        assertTrue(EOFetcher.objectsForEntityNamed(editingContext(), "Thing", EOFetcher.DBEC).containsObject(thing));
    }

    public void testFetchOfSavedObjectIntoNewEC() {
        Thing thing = savedThing();
        EOEditingContext anotherEditingContext = new EOEditingContext();
        thing = (Thing)EOUtilities.localInstanceOfObject(anotherEditingContext, thing);
        assertFalse(EOFetcher.objectsForEntityNamed(anotherEditingContext, "Thing", EOFetcher.EC).containsObject(thing));
        anotherEditingContext = new EOEditingContext();
        thing = (Thing)EOUtilities.localInstanceOfObject(anotherEditingContext, thing);
        assertTrue(EOFetcher.objectsForEntityNamed(anotherEditingContext, "Thing", EOFetcher.DB).containsObject(thing));
        anotherEditingContext = new EOEditingContext();
        thing = (Thing)EOUtilities.localInstanceOfObject(anotherEditingContext, thing);
        assertTrue(EOFetcher.objectsForEntityNamed(anotherEditingContext, "Thing", EOFetcher.DBEC).containsObject(thing));
    }

    public void testDeletedButUnsavedObject() {
        Thing thing = (Thing)mockEditingContext().createSavedObject("Thing");
        assertTrue(EOFetcher.objectsForEntityNamed(mockEditingContext(), "Thing", EOFetcher.DBEC).containsObject(thing));
        mockEditingContext().deleteObject(thing);
        assertFalse(EOFetcher.objectsForEntityNamed(mockEditingContext(), "Thing", EOFetcher.DBEC).containsObject(thing));
    }

    public void testDeletedSavedObject() {
        Thing thing = savedThing();
        editingContext().deleteObject(thing);
        assertTrue(EOFetcher.objectsForEntityNamed(editingContext(), "Thing", EOFetcher.DB).containsObject(thing));
        assertFalse(EOFetcher.objectsForEntityNamed(editingContext(), "Thing", EOFetcher.EC).containsObject(thing));
        assertFalse(EOFetcher.objectsForEntityNamed(editingContext(), "Thing", EOFetcher.DBEC).containsObject(thing));
    }
    
    public void testMultipleValidation() {
        Thing thing1 = savedThing();
        Thing thing2 = savedThing();
        thing1.setName("a");
        thing2.setName("b");
        editingContext().saveChanges();
        
        thing1.setName("b");
        thing2.setName("a");
        assertValidForSave(thing1);
        assertValidForSave(thing2);
    }

    public void testCachedEntity() {
        EOUtilities.entityNamed(editingContext(), "Thing").setCachesObjects(true);
        try {
            Thing savedThing = savedThing();
            Thing unsavedThing = new Thing();
            editingContext().insertObject(unsavedThing);
            assertArraysEqualAsBags(new NSArray(savedThing), EOFetcher.objectsForEntityNamed(editingContext(), "Thing", EOFetcher.DB));
            assertArraysEqualAsBags(new Object[] {savedThing, unsavedThing}, EOFetcher.objectsForEntityNamed(editingContext(), "Thing", EOFetcher.EC));
            assertArraysEqualAsBags(new NSArray(savedThing), EOFetcher.objectsForEntityNamed(editingContext(), "Thing", EOFetcher.DBEC));
        } finally {
            EOUtilities.entityNamed(editingContext(), "Thing").setCachesObjects(false);
        }
    }

}
