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

import com.webobjects.appserver.*;
import com.webobjects.woextensions.*;
import junit.framework.*;


public class WOUTRefreshPage extends WOLongResponsePage {
    public TestResult testResult;
    public TestSuite testSuite;
    public WOUTTestRunner runner;
	public WOUTMain mainPage;
    	 
    public WOUTRefreshPage(WOContext context) {
        super(context);
    }

    protected void bugfixResponse(WOResponse aResponse, WOContext aContext) {
        if (aResponse.headerForKey("Refresh") == null) {
            if (refreshInterval() != 0) {
                String modifiedDynamicUrl = aContext.urlWithRequestHandlerKey(WOApplication.application().componentRequestHandlerKey(), null, null);
                String header = ""
                    + refreshInterval()
                    + ";url=" + modifiedDynamicUrl
                    + "/" + aContext.session().sessionID()
                    + "/" + aContext.contextID()
                    + "." + "WOMetaRefresh";
                aResponse.setHeader(header, "Refresh");
            }
        }
    }

    public void appendToResponse(WOResponse aResponse, WOContext aContext) {
        setRefreshInterval(3);
        super.appendToResponse(aResponse, aContext);
        bugfixResponse(aResponse, aContext);
    }
    
    public int numberOfTestCasesAlreadyDone() {
		return testResult != null ? testResult.runCount() : 0;
    }

    public Object performAction() {
        testResult = new TestResult();
        runner.doRun(testSuite, testResult);
        return runner;
    }
	 
    public WOComponent pageForResult(Object result) {
        mainPage.takeValueForKey((WOUTTestRunner)result, "runner");
        return mainPage;
    }

    public WOComponent stop() {
        runner.result().stop();
        return context().page();
    }

	public boolean isStopping() {
		return testResult != null && testResult.shouldStop();
	}

    public boolean canStop() {
        return ((numberOfTestCasesAlreadyDone() < testSuite.countTestCases())
				&& !isStopping());
    }
    
    public String barColor() {
        if (runner.hasError())
			return "#d90000";
		else if (runner.hasFailure())
			return "#ffbe9a";
		else
			return "#00d973";
    }

}
