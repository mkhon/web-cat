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
import com.webobjects.woextensions.*;
import junit.framework.*;
import junit.runner.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;


public class WOUTTestRunner extends BaseTestRunner {
    protected static long EOADAPTOR_DEBUG_GROUPS = NSLog.DebugGroupSQLGeneration | NSLog.DebugGroupDatabaseAccess;
    public static int originalAllowedDebugLevel = NSLog.debug.allowedDebugLevel();
    protected TestResult result;
    protected String time;

    public WOUTTestRunner() {
        super();
    }

    protected void runFailed(String s) {
        NSLog.err.appendln(s);
    }

    public WOUTTestRunner(PrintStream writer) {
        super();
    }

    public static void setEOAdaptorDebugEnabled(boolean yn) {
        if (yn) {
            NSLog.allowDebugLoggingForGroups(EOADAPTOR_DEBUG_GROUPS);
            NSLog.debug.setAllowedDebugLevel(NSLog.DebugLevelInformational);
        } else {
            NSLog.debug.setAllowedDebugLevel(originalAllowedDebugLevel);
            NSLog.refuseDebugLoggingForGroups(EOADAPTOR_DEBUG_GROUPS);
        }
    }

    public static boolean eoAdaptorDebugEnabled() {
        return NSLog.debugLoggingAllowedForLevelAndGroups(NSLog.DebugLevelInformational, EOADAPTOR_DEBUG_GROUPS);
    }

    public static void setContinueAfterFailureOrError(boolean yn) {
        _bf_setPreference("continueAfterFailureOrError", (yn ? "true" : "false"));
    }

    public static boolean continueAfterFailureOrError() {
        return "true".equals(getPreference("continueAfterFailureOrError"));
    }

    public static void setFilterStack(boolean yn) {
        _bf_setPreference("filterstack", (yn ? "true" : "false"));
    }

    public static boolean filterStack() {
        return "true".equals(getPreference("filterstack"));  // BaseTestRunner.showStackRaw() is unusable
    }

    public static void setShouldLog(boolean yn) {
        _bf_setPreference("shouldLog", (yn ? "true" : "false"));
    }
    
    public static boolean shouldLog() {
        return "true".equals(getPreference("shouldLog"));
    }
    
    public static void setHidePackageNames(boolean yn) {
        _bf_setPreference("hidePackageNames", (yn ? "true" : "false"));
    }
    
    public static boolean hidePackageNames() {
        return "true".equals(getPreference("hidePackageNames"));
    }
    
	public static void _bf_setPreference(String key, String value) {  // setPreference() should be static in BaseTestRunner
		getPreferences().setProperty(key, value);
	}
	
    public void doRun(Test suite) {
        doRun(suite, new TestResult());
    }

    public void doRun(Test suite, TestResult aResult) {
        time = "";
        result = aResult;
        result.addListener(this);
        if (shouldLog())
            result.addListener(new WOUTLogger());
        Properties originalProperties = (Properties)System.getProperties().clone();
        long startTime = System.currentTimeMillis();
        suite.run(result);
        long endTime = System.currentTimeMillis();
        time = elapsedTimeAsString(endTime - startTime) + " s";
        System.setProperties(originalProperties);
    }

    public TestResult result() {
        return result;
    }
    
    public String time() {
        return time;
    }

    public boolean hasError() {
        return result() != null && result().errorCount() != 0;
    }

    public boolean hasFailure() {
        return result() != null && result().failureCount() != 0;
    }

	public void testFailed(int aStatus, Test aTest, Throwable aThrowable) {
        if (!continueAfterFailureOrError())
            result().stop();
	}

	public void testStarted(String aTestName) {}

	public void testEnded(String aTestName) {}

    protected static boolean errorLineIgnoresPackage(WOParsedErrorLine anErrorLine) {
        // WOParsedErrorLine should have a public accessor for _ignorePackage, a bug has been filed
        try {
            Field field = WOParsedErrorLine.class.getDeclaredField("_ignorePackage");
            field.setAccessible(true);
            return field.getBoolean(anErrorLine);
        } catch (Exception e) {
            return false;
        }
    }

    protected static boolean errorLineIsImportant(WOParsedErrorLine anErrorLine) {
        return ("deletePersistentObjects".equals(anErrorLine.methodName())
                && "WOUTTestCase.java".equals(anErrorLine.fileName()));
    }
    
    public static NSArray filteredErrorLines(NSArray errorLines) {
		if (filterStack()) {
			Enumeration errorLineEnum = errorLines.objectEnumerator();
			NSMutableArray enabledErrorLines = new NSMutableArray();
			while (errorLineEnum.hasMoreElements()) {
				WOParsedErrorLine errorLine = (WOParsedErrorLine)errorLineEnum.nextElement();
                if (!errorLineIgnoresPackage(errorLine) || errorLineIsImportant(errorLine))
					enabledErrorLines.addObject(errorLine);
			}
            return enabledErrorLines.count() > 0 ? enabledErrorLines : errorLines;
		} else
			return errorLines;
	}

    protected Class loadSuiteClass(String suiteClassName) throws ClassNotFoundException {
        return Class.forName(suiteClassName);
    }

    protected boolean useReloadingTestSuiteLoader() {
        return false; // The application is already running and has cached classes so that reloading classes has the high risk of causing ClassCastExceptions (typically with WOComponent and EOCustomObject subclasses)
    }
    
    public TestSuite testSuiteForNames(NSArray theTestNames) {
        TestSuite testSuite = new TestSuite("All Selected JUnit Tests");
        Enumeration testNameEnum = theTestNames.objectEnumerator();
        while (testNameEnum.hasMoreElements()) {
			Test test = getTest((String)testNameEnum.nextElement());
			if (test != null)
				testSuite.addTest(test);
        }
        return testSuite;
    }
	
    public static String classNameForDisplay(String aClassName) {
        if (hidePackageNames())
            return _NSStringUtilities.lastComponentInString(aClassName, '.');
        else
            return aClassName;
    }

}
