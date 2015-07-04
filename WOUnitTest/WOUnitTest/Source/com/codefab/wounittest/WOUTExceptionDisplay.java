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
import com.webobjects.woextensions.*;


public class WOUTExceptionDisplay extends WOComponent {
    public Throwable exception;
    public WOExceptionParser error;
    public WOParsedErrorLine errorline;
	protected NSArray enabledErrorLines;

    public WOUTExceptionDisplay(WOContext context) {
        super(context);
    }

    public void setException(Throwable currentException) {
        if (currentException != null) {
            if (currentException instanceof Exception)
                error = new WOExceptionParser(currentException);
            else
                error = new WOExceptionParser(new NSForwardException(currentException));
        } else
            error = null;
        exception = currentException;
    }

    public String errorMessage() {
        // Construct the error message that should be displayed in Xcode
        StringBuffer buffer = new StringBuffer(128);
        buffer.append("Error : ");
        buffer.append(exception.getClass().getName());
        buffer.append(" - Reason :");
        buffer.append(exception.getMessage());
        return new String(buffer);
    }

	public NSArray errorLines() {
		if (enabledErrorLines == null)
			enabledErrorLines = com.codefab.wounittest.WOUTTestRunner.filteredErrorLines(error.stackTrace());
		return enabledErrorLines;
	}
	
    public void appendToResponse(WOResponse aResponse, WOContext aContext) {
        exception = (Throwable)valueForBinding("exception");
        if (exception != null)
            setException(exception);
        super.appendToResponse(aResponse, aContext);
    }

    public void setSingleException(Throwable anException) {
        if (anException != null)
            exception = anException;
    }

    public boolean isStateless() {
		return true;
	}
    
	public boolean synchronizesVariablesWithBindings() {
		return false;
	}
	
	public void reset() {
		super.reset();
		enabledErrorLines = null;
	}
	
}
