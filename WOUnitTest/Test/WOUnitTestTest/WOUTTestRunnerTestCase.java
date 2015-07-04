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
import com.webobjects.eocontrol.*;
import com.webobjects.eoaccess.*;
import com.codefab.wounittest.*;


public class WOUTTestRunnerTestCase extends WOUTTestCase {
    boolean originalHidePackageNames;
    
    protected void setUp() throws Exception {
        super.setUp();
        originalHidePackageNames = WOUTTestRunner.hidePackageNames();
    }

    protected void tearDown() throws Exception {
        WOUTTestRunner.setHidePackageNames(originalHidePackageNames);
        super.tearDown();
    }

    public void testHidePackageNames() {
        WOUTTestRunner.setHidePackageNames(false);
        assertEquals("Blah", WOUTTestRunner.classNameForDisplay("Blah"));
        assertEquals("com.xyz.Blah", WOUTTestRunner.classNameForDisplay("com.xyz.Blah"));

        WOUTTestRunner.setHidePackageNames(true);
        assertEquals("Blah", WOUTTestRunner.classNameForDisplay("Blah"));
        assertEquals("Blah", WOUTTestRunner.classNameForDisplay("com.xyz.Blah"));
    }

}
