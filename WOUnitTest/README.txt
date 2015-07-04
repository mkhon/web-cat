
 WOUnitTest r8


Intro
=====

WOUnitTest is a WebObjects framework for unit testing WebObjects applications based on JUnit. It is developed and tested with WebObjects 5.3, Xcode 2.1 and JUnit 3.8.1.



History
=======

release 8	2006/01/01	eos belonging to WOUTMockEditingContext.entityNamesToGetFromDatabase are now initialized
				implementation classes now located in proper package directories to make other IDEs happy
				WOUTMockEditingContext.create/insertSavedObject() was recording the eo too late - fixed
				overloaded assertArrays(Not)EqualAsBags() to take a java array as first parameter
				new EOFetcher which allows in-memory fetching, see example below for how to use it for testing
				reimplemented WOUTMockEditingContext based on EOFetcher
release 7	2004/03/23	new preferences setting "Hide Package Names"
				protected WOUTTestCenter against testcases with errors in their static initializer
				new WOUTMockEditingContext for fast in-memory tests
				WOUTTestCase is creating the editingContext now lazily, the ivar is now private
					(it used to be protected, use the get-method editingContext() instead)
				abstract test classes which contain concrete test methods are now filtered out
release 6	2003/11/22	TestRunner is making and restoring a backup of System.properties in case any test changes them
				added assertTrue and assertFalse for Boolean
				internal changes for WO5.2.2 incompatible with earlier version of WO
				use NSLog for logging
				removed assertValidates() which was deprecated since release 3
				javadocs for WOUTTestCase
release 5	2003/02/10	added assertEquals(Date, Date, long deltaMilliseconds)
				java.lang.* and sun.reflect.* now also filtered from stacktraces
				improved stacktrace filter when dealing with non-debug code
				new WOUTTestCase template
				classes with a suite() method are now found
				TestCase subclasses can now end on just 'Test' in addition to 'TestCase'
				support for the Ant task 'junit' (see below for details)
				added Ant build scripts (only tested under OS-X, send in your patches for other OSs)
				EOAdaptorDebugEnabled can now be set in the gui (not persistent like other settings)
				added pageWithName() to WOUTTestCase for those who want to unit test their components
release 4	2002/10/15	suite() methods in TestCase subclasses are now acknowledged by TestCenter/TestRunner
				improved stop on progress-bar page
				added simple optional logging
				added optional filtering of stacktraces
				preferences can now be saved with JUnit's settings in ~/junit.properties
				improved handling of errors during automatic deletion of registered eos
				updated gui
release 3	2002/08/30	Main page remembers settings between runs
				ignore TestCases without test methods to allow common abstract subclasses of TestCase
				junit 3.7 -> 3.8 (see junit3.8/README.html for changes)
				added assertValidFor*() and assertInvalidFor*() for all methods of EOValidation
				deprecated assertValidates()
				added assertArraysEqualAsBags() and assertArraysNotEqualAsBags()
release 2	2002/03/08	simplified progress-bar page, cleaned up projects
release 1	2002/02/26	initial public release



Installation
============

1. Get JUnit from http://junit.org
2. Install JUnit (copy junit.jar to /Library/Java/Extensions/)
3. Build and install the WOUnitTest framework (to /Library/Frameworks/)
	- with Xcode by building with the "Deployment" build style
	- or from the command line: "xcodebuild -target WOUnitTest -buildstyle Deployment install"
	- or with Ant: "ant all" (requires http://www.objectstyle.org/woproject)
	- you might not have permissions for the subtarget Template to copy the WOUT template to "/Library/Application Support/Apple/Developer Tools/File Templates/WebObjects/", use sudo
4. Add the WOUnitTest framework to the application project you want to test



Run test cases
==============

1. Launch an application that contains the WOUnitTest framework

2. Go to the WOUnitTest page with the direct action "ut" as in this URL:
	http://<hostname>:<port>/cgi-bin/WebObjects/<application name>.woa/wa/ut

Or use Ant - see below.



Write a test case
=================

1. Create a test case class by extending WOUTTestCase (name of test case class has to have the suffix 'TestCase' or 'Test'):
	public class xxxxxTestCase extends WOUTTestCase {
		.......
	}

2. Add test case methods (name of method has to start with 'test'):
	public void testXxxxx() {
		.........
		assertYyyyy(...........);
	}

3. WOUTTestCase contains an editing context (editingContext()) and a mock editing context (mockEditingContext()). Use either to test enterprise objects.

4. WOUTTestCase implements assertValidForSave(EOValidation validationObject). Use it to assert that enterprise objects validate without having to save them.

5. WOUTTestCase's tearDown() method is reverting the editing context. Therefore, unsaved enterprise objects don't need to be deleted at the end of tests.

6. If a test is actually saving enterprise objects, instead of deleting them explicity after the test, they can be registered for automatic deletion with registerPersistentRootObjectForDeletion(EOEnterpriseObject anEnterpriseObject). Registered enterprise objects will be deleted in reverse order (LiFo).

7. Instead of sending saveChanges() to the editing context directly, you can use saveChanges(boolean assumeSuccess).

Note: Please look at the JUnit documentation for more general information on how to write tests.


Example
=======

Assuming there is an EOCustomObject called Customer, there are three basic ways to test the object. The first is using assertValidForSave() which only tests the object in memory. The second is to actually save the object to the database. The third (this is new as of release 7) utilizes the mock editing context.

import com.codefab.wounittest.*;
public class CustomerTestCase extends WOUTTestCase {
    public void testCustomerValidates() {
        Customer customer = new Customer();
        editingContext().insertObject(customer);
        customer.foo();
        assertInvalidForSave(customer);
        customer.bar();
        assertValidForSave(customer);
    }
    public void testCustomerSaves() {
        Customer customer = new Customer();
        editingContext().insertObject(customer);
        registerPersistentRootObjectForDeletion(customer);
        customer.foo();
        saveChanges(false);
        customer.bar();
        saveChanges(true);
    }
    public void testCustomer() {
        Customer customer = (Customer)mockEditingContext().createSavedObject("Customer");
        customer.foo();
        assertInvalidForSave(customer);
        customer.bar();
        assertValidForSave(customer);
    }
}


EOFetcher
=========

Code like
    static NSArray customersNamed(EOEditingContext ec, String aName) {
        NSDictionary bindings = new NSDictionary(aName, "name");
        return EOUtilities.objectsWithFetchSpecificationAndBindings(ec, "Customer", "byNameFS", bindings);
    }
is difficult to unit test because real customer records will need to be written to the database in preparation for the test, and cleaned up after the test. By using EOFetcher instead of EOUtilities in your production code, testing becomes easier and faster:
    static NSArray customersNamed(EOEditingContext ec, String aName) {
        NSDictionary bindings = new NSDictionary(aName, "name");
        return EOFetcher.objectsWithFetchSpecificationNameAndBindings(ec, "Customer", "byNameFS", bindings, EOFetcher.DB);
    }
Now we can unit test this code using the mock editing context in memory without ever having to access the database:
    public void testCustomerFetchingByName() {
	Customer customer1 = (Customer)mockEditingContext().createSavedObject("Customer");
        customer1.setName("foo");
	Customer customer2 = (Customer)mockEditingContext().createSavedObject("Customer");
        customer2.setName("bar");
	Customer customer3 = (Customer)mockEditingContext().createSavedObject("Customer");
        customer3.setName("bar");
	assertArraysEqualAsBags(new Object[] {}, Customer.customersNamed(mockEditingContext(), "baz"));
	assertArraysEqualAsBags(new Object[] {customer1}, Customer.customersNamed(mockEditingContext(), "foo"));
	assertArraysEqualAsBags(new Object[] {customer2, customer3}, Customer.customersNamed(mockEditingContext(), "bar"));
    }

See EOFetcher's javadoc for more details.


Shortcuts
=========

1. WOUnitTest comes with a template for TestCases called WOUTTestCase. It will be located under the WebObjects group in the list of templates you get when creating a new file in Project Builder.

2. To run all the unit tests directly, use the direct action "uta" as in this URL:
	http://<hostname>:<port>/cgi-bin/WebObjects/<application name>.woa/wa/uta



Ant
=========

If Ant (http://ant.apache.org) is used as the build tool, the unit tests can be run as part of the build process. To take advantage of WOUnitTest's automatic test suite, use WOUTAllTestsSuite as the test name for the JUnit task and fork the tests from the WOApp's directory. This will run all the test cases that WOUnitTest can find in your application and frameworks. Example:
    ...
        <path id="runClasspath">
            <pathelement location="/Library/Frameworks/WOUnitTest.framework/Resources/Java/WOUnitTest.jar"/>
            <pathelement location="/Library/WebObjects/Applications/MyWOApp.woa/Contents/Resources/Java/MyWOApp.jar"/>
            ...
    <target name="test" depends="package" description="run the unit tests">
        <junit fork="yes" haltonfailure="on" dir="/Library/WebObjects/Applications/MyWOApp.woa">
            <classpath refid="runClasspath"/>
            <jvmarg value="-Djava.compiler=NONE"/>
            <formatter type="brief"/>
            <test name="com.codefab.wounittest.WOUTAllTestsSuite"/>
        </junit>
    </target>



About
=====

WOUnitTest was created by Shin Ogino and Christian Pekeler. It is based on WOFJUnit by Shin Ogino. Contributions were made by Josh Flowers and Bill Bumgarner. Development since release 3 by Christian Pekeler.

christian@pekeler.org

http://wounittest.sourceforge.net
