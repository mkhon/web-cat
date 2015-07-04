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
import com.webobjects.appserver.*;
import junit.framework.*;


public class WOUTMain extends WOComponent {
    public boolean continueAfterFailureOrError = WOUTTestRunner.continueAfterFailureOrError();
	public boolean filterStack = WOUTTestRunner.filterStack();
    public boolean shouldLog = WOUTTestRunner.shouldLog();
    public boolean isEOAdaptorDebugEnabled = WOUTTestRunner.eoAdaptorDebugEnabled();
    public boolean hidePackageNames = WOUTTestRunner.hidePackageNames();

    public String testName;
    public NSArray selectedTestNames;
    public WOUTTestCenter testCenter = WOUTTestCenter.sharedTestCenter();
    public WOUTTestRunner runner;

    public WOUTMain(WOContext context) {
        super(context);
    }

    protected void setSettings() {
        WOUTTestRunner.setContinueAfterFailureOrError(continueAfterFailureOrError);
        WOUTTestRunner.setFilterStack(filterStack);
        WOUTTestRunner.setShouldLog(shouldLog);
        WOUTTestRunner.setEOAdaptorDebugEnabled(isEOAdaptorDebugEnabled);
        WOUTTestRunner.setHidePackageNames(hidePackageNames);
    }

    public WOComponent runSelectedTests() {
		if (selectedTestNames != null && selectedTestNames.count() > 0) {
			setSettings();
			runner = new WOUTTestRunner();
			TestSuite testSuite = runner.testSuiteForNames(selectedTestNames);
			if (testSuite.countTestCases() > 0) {
				WOComponent nextPage = pageWithName("WOUTRefreshPage");
				nextPage.takeValueForKey(testSuite, "testSuite");
				nextPage.takeValueForKey(runner, "runner");
				nextPage.takeValueForKey(this, "mainPage");
				return nextPage;
			}
		}
		return context().page();
    }

    public WOComponent runAllTests() {
		selectedTestNames = testCenter.testNames();
		return runSelectedTests();
    }

    public WOComponent saveSettings() {
		setSettings();
        try {
            WOUTTestRunner.savePreferences();
        } catch (java.io.IOException e) {
            NSLog.err.appendln("WOUT failed to save settings because " + e);
        }
        return context().page();
    }

    public String testNameForDisplay() {
        return WOUTTestRunner.classNameForDisplay(testName);
    }

}
