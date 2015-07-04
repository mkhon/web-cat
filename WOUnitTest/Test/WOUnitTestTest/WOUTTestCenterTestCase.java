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
import com.codefab.wounittest.*;
import junit.framework.*;


public class WOUTTestCenterTestCase extends WOUTTestCase {
    protected NSArray classNamesBackUp;
    protected WOUTTestCenter testCenter;

    public void setUp() throws Exception {
        super.setUp();
        testCenter = WOUTTestCenter.sharedTestCenter();
        classNamesBackUp = testCenter.classNames();
        testCenter.setClassNames(new NSArray(new String [] {
            "com.codefab.testwounittest.SuccessTestCase",
            "com.codefab.testwounittest.FailureTestCase",
            "com.codefab.testwounittest.ErrorTestCase",
            "com.codefab.testwounittest.AbstractTestCase",
            "com.codefab.testwounittest.ConcreteTest",
            "com.codefab.testwounittest.WithSuiteTestCase",
            "com.codefab.testwounittest.AllRegularTestsTestCase",
            "Application"}));
    }

    public void tearDown() throws Exception {
        testCenter.setClassNames(classNamesBackUp);
        classNamesBackUp = null;
        super.tearDown();
    }

    public void testTestNames() {
        NSArray testNames = testCenter.testNames();
        assertEquals(6, testNames.count());
        assertFalse(testNames.containsObject("Application"));
        assertEquals("com.codefab.testwounittest.AllRegularTestsTestCase", testNames.objectAtIndex(0));
        assertEquals("com.codefab.testwounittest.ConcreteTest", testNames.objectAtIndex(1));
        assertEquals("com.codefab.testwounittest.ErrorTestCase", testNames.objectAtIndex(2));
        assertEquals("com.codefab.testwounittest.FailureTestCase", testNames.objectAtIndex(3));
        assertEquals("com.codefab.testwounittest.SuccessTestCase", testNames.objectAtIndex(4));
        assertEquals("com.codefab.testwounittest.WithSuiteTestCase", testNames.objectAtIndex(5));
    }

    public void testTestSuiteForNamesEmpty() {
		WOUTTestRunner runner = new WOUTTestRunner();
        TestSuite testSuite = runner.testSuiteForNames(NSArray.EmptyArray);
        assertEquals(0, testSuite.countTestCases());
    }

    public void testTestSuiteForNamesOne() {
		WOUTTestRunner runner = new WOUTTestRunner();
        TestSuite testSuite = runner.testSuiteForNames(new NSArray("com.codefab.testwounittest.SuccessTestCase"));
        assertEquals(1, testSuite.countTestCases());
        TestSuite innerTestSuite = (TestSuite)testSuite.tests().nextElement();

        assertSame(SuccessTestCase.class, innerTestSuite.tests().nextElement().getClass());
    }

    public void testTestSuiteForNamesOneExplicitSuite() {
		WOUTTestRunner runner = new WOUTTestRunner();
        TestSuite testSuite = runner.testSuiteForNames(new NSArray("com.codefab.testwounittest.WithSuiteTestCase"));
        assertEquals(1, testSuite.countTestCases());
        TestSuite innerTestSuite = (TestSuite)testSuite.tests().nextElement();

        assertSame(WithSuiteTestCase.class, innerTestSuite.tests().nextElement().getClass());
    }
	
    public void testTestSuiteForNames() {
		WOUTTestRunner runner = new WOUTTestRunner();
        TestSuite testSuite = runner.testSuiteForNames(new NSArray(new String[] {
            "com.codefab.testwounittest.SuccessTestCase",
            "com.codefab.testwounittest.FailureTestCase"}));
        assertEquals(2, testSuite.countTestCases());
    }

}
