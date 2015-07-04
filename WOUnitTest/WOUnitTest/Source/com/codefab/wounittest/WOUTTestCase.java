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
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.appserver.*;
import junit.framework.*;
import java.util.*;


/**
* <code>WOUTTestCase</code> adds WO/EOF specific functionality to <code>TestCase</code> like an <code>EOEditingContext</code> and additional asserts.
* <p>
* Assuming there is an <code>EOCustomObject</code> called <code>Customer</code>, there are two basic ways to test the object.
* The first is using <code>assertValidForSave</code> which only tests the object in memory. This is generally preferred for performance reasons.
* The second is to actually save the object to the database.
* You probably only need to do this if you want to test the database at the same time (for example to make sure the tables match the entities).
* <pre>
*  import com.codefab.wounittest.*;
*
*  public class CustomerTestCase extends WOUTTestCase {
*
*      public void testCustomerValidates() {
*          Customer customer = new Customer();
*          editingContext().insertObject(customer);
*          customer.foo();
*          assertInvalidForSave(customer);
*          customer.bar();
*          assertValidForSave(customer);
*      }
*
*      public void testCustomerSaves() {
*          Customer customer = new Customer();
*          editingContext().insertObject(customer);
*          registerPersistentRootObjectForDeletion(customer);
*          customer.foo();
*          saveChanges(false);
*          customer.bar();
*          saveChanges(true);
*      }
*  }
* </pre>
* <p>
* Additional information can be found in
* <a href="http://cvs.sourceforge.net/viewcvs.py/wounittest/WOUnitTest/Test/WOUnitTestTest/WOUTTestCaseTestCase.java?rev=HEAD">WOUTTestCaseTestCase.java</a>.
*/

public class WOUTTestCase extends TestCase {
    private EOEditingContext editingContext;
    private WOUTMockEditingContext mockEditingContext;
    protected NSMutableArray persistentRootObjects = new NSMutableArray();

    /**
    * No-arg constructor to enable serialization. This method
    * is not intended to be used by mere mortals without calling <code>setName()</code>.
    */
    public WOUTTestCase() {
		super();
	}

    /**
    * Constructs a test case with the given <code>aName</code>.
    */
    public WOUTTestCase(String aName) {
		super(aName);
	}

    /**
    * Loads the <code>EOModels</code>. Called by <code>setUp()</code>.
    */
    protected void loadEOModels() {
        EOModelGroup.defaultGroup();
    }

    /**
    * Sets up the fixture, for example, open a network connection.
    * This method is called before a test is executed.
    * When overriding, call <code>super</code> first.
    */
    protected void setUp() throws Exception {
        super.setUp();
        loadEOModels();
    }

    /**
    * Registers <code>anEnterpriseObject</code> to be automatically deleted in <code>tearDown()</code>.
    * Only useful/necessary for enterprise objects which get saved to the database by the test.
    * anEnterpriseObject will delete related objects if their relationship is set to <code>cascade-delete</code>,
    * therefore only register "root objects" with this method.
    */
    protected void registerPersistentRootObjectForDeletion(EOEnterpriseObject anEnterpriseObject) {
        persistentRootObjects.addObject(anEnterpriseObject);
    }

    /**
    * Deletes enterprise objects that have been registered with <code>registerPersistentRootObjectForDeletion</code>.
    * Called by <code>tearDown()</code>.
    */
    protected void deletePersistentObjects() throws Exception {
        boolean deleteExceptionRaised = false;
        editingContext().saveChanges();
        Enumeration persistentObjectEnum = persistentRootObjects.reverseObjectEnumerator();
        while (persistentObjectEnum.hasMoreElements()) {
            EOEnterpriseObject eo = (EOEnterpriseObject)persistentObjectEnum.nextElement();
            if (eoHasBeenSaved(eo)) {
                try {
                    editingContext().deleteObject(eo);
                    editingContext().saveChanges();
                } catch (Exception e) {
                    NSLog.err.appendln(this + "'s tearDown can't delete object " + eo.entityName() + "(" + eo.userPresentableDescription() + ") because " + NSLog.throwableAsString(e));
                    deleteExceptionRaised = true;
                }
            }
        }
        if (deleteExceptionRaised)
            throw new RuntimeException("tearDown failed, see console output for details");
    }
    
    /**
    * Returns whether <code>anEnterpriseObject</code> has been saved to the database.
    */
    protected boolean eoHasBeenSaved(EOEnterpriseObject anEnterpriseObject) {
        EOGlobalID globalId = editingContext().globalIDForObject(anEnterpriseObject);
        return !(globalId == null || globalId.isTemporary());
    }

    /**
    * Internal helper method for <code>tearDown</code>.
    */
    protected void tearDownMockEditingContext() {
        if (mockEditingContext != null) {
            mockEditingContext.unlock();
            mockEditingContext.dispose();
            mockEditingContext = null;
        }
    }
    
    /**
    * Internal helper method for <code>tearDown</code>.
    */
    protected void tearDownEditingContext() throws Exception {
        if (editingContext != null) {
            editingContext.revert();
            try {
                deletePersistentObjects();
            } finally {
                editingContext.unlock();
                editingContext.dispose();
                editingContext = null;
            }
        }
    }
    
    /**
    * Tears down the fixture, for example, close a network connection.
    * This method is called after a test is executed.
    * Reverts the editingContext so that unsaved enterprise objects don't need to be explicitly deleted.
    * When overriding, call <code>super</code> last.
    */
    protected void tearDown() throws Exception {
        tearDownMockEditingContext();
        tearDownEditingContext();
        super.tearDown();
    }

    /**
    * Runs the bare test sequence.
    * This overwrites <code>runBare()</code> in <code>TestCase</code> such that exceptions in <code>tearDown()</code>
    * are only shown if the test itself didn't throw.
    */
    public void runBare() throws Throwable {
        setUp();
        try {
            runTest();
        } catch (Throwable testException) {
            try {
                tearDown();
            } catch (Throwable ignoredTearDownException) {}
            throw testException;
        }
        tearDown();
    }

    /**
    * Saves the <code>editingContext</code> and asserts an exception does or doesn't get thrown depending on the parameter.
    * @see    com.webobjects.eocontrol.EOEditingContext EOEditingContext.saveChanges() 
    * @param  assumeSuccess  true to assert <code>saveChanges</code> will be successful, false to assert <code>saveChanges</code> will fail
    */
    protected void saveChanges(boolean assumeSuccess) {
        Exception exception = null;
        try {
            editingContext().saveChanges();
        } catch (Exception e) {
            exception = e;
            if (assumeSuccess) {
                NSLog.debug.appendln(e);
                editingContext().revert();
            }
        }
        if (assumeSuccess)
            assertNull(exception);
        else
            assertNotNull(exception);
    }

    /**
    * Returns an <code>EOEditingContext</code> that can be used for testing enterprise objects.
    * Consider using a <code>MockEditingContext</code> instead.
    */
    protected EOEditingContext editingContext() {
        if (editingContext == null) {
            editingContext = new EOEditingContext();
            editingContext.lock();
        }
        return editingContext;
    }

    /**
    * Returns a <code>MockEditingContext</code> that can be used for testing enterprise objects without accessing the database.
    */
    protected WOUTMockEditingContext mockEditingContext() {
        if (mockEditingContext == null) {
            mockEditingContext = new WOUTMockEditingContext();
            mockEditingContext.lock();
        }
        return mockEditingContext;
    }

    /**
    * Internal helper method, fails with message for the unequal objects.
    * This is a duplicate of <code>Assert.failNotEquals()</code>
    * which is unfortunately private.
    */
    static protected void _failNotEquals(String message, Object expected, Object actual) {
        String formatted = "";
        if (message != null)
            formatted = message + " ";
        formatted = formatted + "expected:<" + expected + "> but was:<" + actual + ">";
        fail(formatted);
    }

    /**
    * Asserts that two Dates are equal concerning a delta in milliseconds.
    */
    public static void assertEquals(Date expectedDate, Date actualDate, long deltaMilliseconds) {
        assertEquals(null, expectedDate, actualDate, deltaMilliseconds);
    }

    /**
    * Asserts that two Dates are equal concerning a delta in milliseconds. If they are not,
    * an <code>AssertionFailedError</code> is thrown with the given <code>message</code>.
    */
    public static void assertEquals(String message, Date expectedDate, Date actualDate, long deltaMilliseconds) {
        if (!(Math.abs(expectedDate.getTime() - actualDate.getTime()) <= deltaMilliseconds))
            _failNotEquals(message, expectedDate, actualDate);
    }

    /**
    * Internal helper method for <code>assertArraysEqualAsBags</code> and <code>assertArraysNotEqualAsBags</code>.
    */
    protected static boolean _areArraysEqualAsBags(NSArray firstArray, NSArray secondArray) {
        if (firstArray == null ^ secondArray == null)
            return false;
        if (firstArray == secondArray || firstArray.equals(secondArray))
            return true;
        if (firstArray.count() != secondArray.count())
            return false;
        NSMutableArray firstMutableArray = new NSMutableArray(firstArray);
        Enumeration secondArrayEnum = secondArray.objectEnumerator();
        while (secondArrayEnum.hasMoreElements()) {
            int index = firstMutableArray.indexOfObject(secondArrayEnum.nextElement());
            if (index == NSArray.NotFound)
                return false;
            else
                firstMutableArray.removeObjectAtIndex(index);
        }
        return true;
    }

    /**
    * Asserts that two NSArrays contain the same or equal objects independent of their order.
    */
    public static void assertArraysEqualAsBags(NSArray expectedArray, NSArray actualArray) {
        if (!_areArraysEqualAsBags(expectedArray, actualArray))
            fail("expected: " + expectedArray + " but was: " + actualArray);
    }

    /**
    * Asserts that two NSArrays don't contain the same or equal objects independent of their order.
    */
    public static void assertArraysNotEqualAsBags(NSArray expectedArray, NSArray actualArray) {
        if (_areArraysEqualAsBags(expectedArray, actualArray))
            fail("expected different than: " + expectedArray);
    }

    /**
    * Asserts that an NSArray and a java array contain the same or equal objects independent of their order.
    * Convenience method to allow for:
    * <code>assertArraysEqualAsBags(new Object[] {a, b}, testObject.methodReturningNSArray());</code>
    */
    public static void assertArraysEqualAsBags(Object[] expectedJavaArray, NSArray actualArray) {
        assertArraysEqualAsBags(new NSArray(expectedJavaArray), actualArray);
    }
    
    /**
    * Asserts that an NSArray and a java array don't contain the same or equal objects independent of their order.
    * Convenience method to allow for:
    * <code>assertArraysNotEqualAsBags(new Object[] {a, b}, testObject.methodReturningNSArray());</code>
    */
    public static void assertArraysNotEqualAsBags(Object[] expectedJavaArray, NSArray actualArray) {
        assertArraysNotEqualAsBags(new NSArray(expectedJavaArray), actualArray);
    }
    

    /**
    * Asserts that the object is in a consistent state from the server-side perspective.
    * @see  com.webobjects.eocontrol.EOValidation EOValidation.validateClientUpdate()
    */
    public static void assertValidForClientUpdate(EOValidation validationObject) {
        try {
            validationObject.validateClientUpdate();
        } catch (NSValidation.ValidationException exception) {
            fail("validateClientUpdate() unexpectedly failed: " + exception.getMessage());
        }
    }

    /**
    * Asserts that the object is not in a consistent state from the server-side perspective.
    * @see  com.webobjects.eocontrol.EOValidation EOValidation.validateClientUpdate()
    */
    public static void assertInvalidForClientUpdate(EOValidation validationObject) {
        try {
            validationObject.validateClientUpdate();
            fail("validateClientUpdate() succeeded unexpectedly for: " + validationObject);
        } catch (NSValidation.ValidationException exception) {}
    }


    /**
    * Asserts that the object can be deleted in its current state.
    * @see  com.webobjects.eocontrol.EOValidation EOValidation.validateForDelete()
    */
    public static void assertValidForDelete(EOValidation validationObject) {
        try {
            validationObject.validateForDelete();
        } catch (NSValidation.ValidationException exception) {
            fail("validateForDelete() unexpectedly failed: " + exception.getMessage());
        }
    }

    /**
    * Asserts that the object can not be deleted in its current state.
    * @see  com.webobjects.eocontrol.EOValidation EOValidation.validateForDelete()
    */
    public static void assertInvalidForDelete(EOValidation validationObject) {
        try {
            validationObject.validateForDelete();
            fail("validateForDelete() succeeded unexpectedly for: " + validationObject);
        } catch (NSValidation.ValidationException exception) {}
    }


    /**
    * Asserts that the object can be inserted in its current state.
    * @see  com.webobjects.eocontrol.EOValidation EOValidation.validateForInsert()
    */
    public static void assertValidForInsert(EOValidation validationObject) {
        try {
            validationObject.validateForInsert();
        } catch (NSValidation.ValidationException exception) {
            fail("validateForInsert() unexpectedly failed: " + exception.getMessage());
        }
    }

    /**
    * Asserts that the object can not be inserted in its current state.
    * @see  com.webobjects.eocontrol.EOValidation EOValidation.validateForInsert()
    */
    public static void assertInvalidForInsert(EOValidation validationObject) {
        try {
            validationObject.validateForInsert();
            fail("validateForInsert() succeeded unexpectedly for: " + validationObject);
        } catch (NSValidation.ValidationException exception) {}
    }


    /**
    * Asserts that the object can be saved in its current state.
    * @see  com.webobjects.eocontrol.EOValidation EOValidation.validateForSave()
    */
    public static void assertValidForSave(EOValidation validationObject) {
        try {
            validationObject.validateForSave();
        } catch (NSValidation.ValidationException exception) {
            fail("validateForSave() unexpectedly failed: " + exception.getMessage());
        }
    }

    /**
    * Asserts that the object can not be saved in its current state.
    * @see  com.webobjects.eocontrol.EOValidation EOValidation.validateForSave()
    */
    public static void assertInvalidForSave(EOValidation validationObject) {
        try {
            validationObject.validateForSave();
            fail("validateForSave() succeeded unexpectedly for: " + validationObject);
        } catch (NSValidation.ValidationException exception) {}
    }


    /**
    * Asserts that the object can be updated in its current state.
    * @see  com.webobjects.eocontrol.EOValidation EOValidation.validateForUpdate()
    */
    public static void assertValidForUpdate(EOValidation validationObject) {
        try {
            validationObject.validateForUpdate();
        } catch (NSValidation.ValidationException exception) {
            fail("validateForUpdate() unexpectedly failed: " + exception.getMessage());
        }
    }

    /**
    * Asserts that the object can not be updated in its current state.
    * @see  com.webobjects.eocontrol.EOValidation EOValidation.validateForUpdate()
    */
    public static void assertInvalidForUpdate(EOValidation validationObject) {
        try {
            validationObject.validateForUpdate();
            fail("validateForUpdate() succeeded unexpectedly for: " + validationObject);
        } catch (NSValidation.ValidationException exception) {}
    }

    /**
    * Asserts that the object corresponds to the primitive value <code>true</code>. If it is not,
    * an <code>AssertionFailedError</code> is thrown with the given <code>message</code>.
    */
    static public void assertTrue(String message, Boolean object) {
        if (!object.booleanValue())
            fail(message);
    }

    /**
    * Asserts that the object corresponds to the primitive value <code>true</code>.
    */
    static public void assertTrue(Boolean object) {
        assertTrue(null, object);
    }

    /**
    * Asserts that the object corresponds to the primitive value <code>false</code>. If it is not,
    * an <code>AssertionFailedError</code> is thrown with the given <code>message</code>.
    */
    static public void assertFalse(String message, Boolean object) {
        assertTrue(message, !object.booleanValue());
    }

    /**
    * Asserts that the object corresponds to the primitive value <code>false</code>.
    */
    static public void assertFalse(Boolean object) {
        assertFalse(null, object);
    }

    /**
    * To be called before any resources (like <code>WOComponents</code>) will be loaded.
    * @see  com.webobjects.appserver.WOApplication WOApplication.primeApplication(String, URL, String)
    */
    public void initPrimeApplication() {
        WOApplication.primeApplication(null, NSBundle.mainBundle().bundlePathURL(), null);
    }
    
    /**
    * Returns a new page instance (a <code>WOComponent</code> object) identified by <code>aComponentName</code>.
    * Calls <code>initPrimeApplication()</code> so you don't have to.
    * @see  com.webobjects.appserver.WOApplication WOApplication.pageWithName(String, WOContext)
    */
    public WOComponent pageWithName(String aComponentName) {
        initPrimeApplication();
        WOContext context = new WOContext(null);
        return WOApplication.application().pageWithName(aComponentName, context);
    }

}
