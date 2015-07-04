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

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eoaccess.*;
import com.codefab.wounittest.*;
import java.util.*;
import junit.framework.*;

 
public class WOUTTestCaseTestCase extends WOUTTestCase {
    int tearDownCounter;
    //private EOEditingContext editingContextToTestWOUTRunner = editingContext();
        
    public void testRunBareWithTestOKAndTearDownOK() throws Throwable {
        InnerTestForRunBare testCase = new InnerTestForRunBare("successfulTest", false);
        testCase.runBare();
        assertEquals(1, tearDownCounter);
    }
    
    public void testRunBareWithTestOKAndTearDownException() throws Throwable {
        InnerTestForRunBare testCase = new InnerTestForRunBare("successfulTest", true);
        try {
            testCase.runBare();
            fail();
        } catch (TearDownException e) {}
        assertEquals(1, tearDownCounter);
    }
    
    public void testRunBareWithTestFailingAndTearDownOK() throws Throwable {
        InnerTestForRunBare testCase = new InnerTestForRunBare("failingTest", false);
        try {
            testCase.runBare();
            fail();
        } catch (AssertionFailedError e) {}
        assertEquals(1, tearDownCounter);
    }
    
    public void testRunBareWithTestFailingAndTearDownException() throws Throwable {
        InnerTestForRunBare testCase = new InnerTestForRunBare("failingTest", true);
        try {
            testCase.runBare();
            fail();
        } catch (AssertionFailedError e) {}
        assertEquals(1, tearDownCounter);
    }
    
    public class TearDownException extends Exception {}
    
    public class InnerTestForRunBare extends WOUTTestCase {
        boolean throwInTearDown;
        
        public InnerTestForRunBare(String aTestName, boolean shouldThrowInTearDown) {
            super(aTestName);
            throwInTearDown = shouldThrowInTearDown;
        }
    
        public void successfulTest() {
            assertTrue(true);
        }
        
        public void failingTest() {
            assertTrue(false);
        }
        
        public void setUp() throws Exception {
            super.setUp();
            tearDownCounter = 0;
        }
        
        public void tearDown() throws Exception {
            tearDownCounter++;
            if (throwInTearDown)
                throw new TearDownException();
            super.tearDown();
        }
    }
    
    public void testEditingContext() {
        assertNotNull(editingContext());
    }

    public void testAssertValidation() {
        EOEnterpriseObject entityA = EOUtilities.createAndInsertInstance(editingContext(), "EntityA");
        assertInvalidForSave(entityA);
        entityA.takeValueForKey("Joe", "name");
        assertValidForSave(entityA);
    }

    public void testRegisterDeletedObjectForDeletion() {
        EOEnterpriseObject entityA = EOUtilities.createAndInsertInstance(editingContext(), "EntityA");
        registerPersistentRootObjectForDeletion(entityA);
        editingContext().deleteObject(entityA);
        //assert that tearDown doesn't raise
    }

    public void testRegisterUnsavedObjectForDeletion() {
        EOEnterpriseObject entityA = EOUtilities.createAndInsertInstance(editingContext(), "EntityA");
        registerPersistentRootObjectForDeletion(entityA);
        //assert that tearDown doesn't raise
    }

    protected void deleteAllEntityAs() {
        Enumeration objectEnum = EOUtilities.objectsForEntityNamed(editingContext(), "EntityA").objectEnumerator();
        while (objectEnum.hasMoreElements())
            editingContext().deleteObject((EOEnterpriseObject)objectEnum.nextElement());
        editingContext().saveChanges();
    }

    public void testRegisterSavedObjectForDeletion() {
        deleteAllEntityAs();
        EOEnterpriseObject entityA = EOUtilities.createAndInsertInstance(editingContext(), "EntityA");
        registerPersistentRootObjectForDeletion(entityA);
        entityA.takeValueForKey("Joe", "name");
        editingContext().saveChanges();

        NSArray objects = EOUtilities.objectsForEntityNamed(editingContext(), "EntityA");
        assertEquals(1, objects.count());
    }

    public void testRegisterSavedObjectForDeletion2() {
        NSArray objects = EOUtilities.objectsForEntityNamed(editingContext(), "EntityA");
        assertEquals(0, objects.count());
    }

    public void testAssertArraysEqualAsBags() {
        Object one = new Object();
        Object two = new Object();
        Object three = new Object();

        NSArray expectedArray = null;
        NSArray actualArray = null;
        assertArraysEqualAsBags(expectedArray, actualArray);

        expectedArray = null;
        actualArray = NSArray.EmptyArray;
        assertArraysNotEqualAsBags(expectedArray, actualArray);

        expectedArray = NSArray.EmptyArray;
        actualArray = null;
        assertArraysNotEqualAsBags(expectedArray, actualArray);

        expectedArray = NSArray.EmptyArray;
        actualArray = NSArray.EmptyArray;
        assertArraysEqualAsBags(expectedArray, actualArray);

        expectedArray = new NSArray(new Object[] {one, two, three});
        actualArray = new NSArray(new Object[] {one, two});
        assertArraysNotEqualAsBags(expectedArray, actualArray);

        expectedArray = new NSArray(new Object[] {one, two, three});
        actualArray = new NSArray(new Object[] {new Object(), new Object(), three});
        assertArraysNotEqualAsBags(expectedArray, actualArray);

        expectedArray = new NSArray(new Object[] {one, two, three});
        actualArray = new NSArray(new Object[] {one, two, three});
        assertArraysEqualAsBags(expectedArray, actualArray);

        expectedArray = new NSArray(new Object[] {three, one, two});
        actualArray = new NSArray(new Object[] {one, two, three});
        assertArraysEqualAsBags(expectedArray, actualArray);

        expectedArray = new NSArray(new Object[] {one, two, three});
        actualArray = new NSArray(new Object[] {one, one, two});
        assertArraysNotEqualAsBags(expectedArray, actualArray);

        expectedArray = new NSArray(new Object[] {one, one, two, three});
        actualArray = new NSArray(new Object[] {one, two, two, three});
        assertArraysNotEqualAsBags(expectedArray, actualArray);
    }
    
    public void testAssertArraysEqualAsBagsWithJavaArray() {
        NSArray actualArray = null;
        assertArraysNotEqualAsBags(new Object[] {}, actualArray);
        actualArray = NSArray.EmptyArray;
        assertArraysEqualAsBags(new Object[] {}, actualArray);
        actualArray = new NSArray("1");
        assertArraysEqualAsBags(new Object[] {"1"}, actualArray);
        actualArray = new NSArray(new Object[] {"1", "2", "3"});
        assertArraysEqualAsBags(new Object[] {"1", "2", "3"}, actualArray);
        assertArraysNotEqualAsBags(new Object[] {"1", "2", "4"}, actualArray);
    }

    public void testAssertEqualTimestamps() {
        NSTimestamp timestamp1 = new NSTimestamp(6000);
        NSTimestamp timestamp2 = new NSTimestamp(7000);
        assertEquals(timestamp1, timestamp2, 1000);
        assertEquals(timestamp2, timestamp1, 1000);
        try {
            assertEquals(timestamp1, timestamp2, 999);
            fail();
        } catch (AssertionFailedError e) {}
        try {
            assertEquals(timestamp2, timestamp1, 999);
            fail();
        } catch (AssertionFailedError e) {}
    }

    public void testPageWithName() {
        WOUTMain component = (WOUTMain)pageWithName("WOUTMain");
        assertNotNull(component);
        assertEquals("com.codefab.wounittest.WOUTMain", component.name());

        WOComponent component2 = pageWithName("Main");
        assertNotNull(component2);
        assertEquals("Main", component2.name());
    }

    public void testAssertBoolean() {
        assertTrue(Boolean.TRUE);
        assertFalse(Boolean.FALSE);
    }

}
