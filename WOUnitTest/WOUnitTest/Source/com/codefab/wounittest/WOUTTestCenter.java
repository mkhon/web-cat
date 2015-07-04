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
import junit.framework.*;
import java.util.*;
import java.lang.reflect.*;


public class WOUTTestCenter extends Object {
    static protected WOUTTestCenter sharedTestCenter = null;
    protected NSArray testNames;
	protected NSArray classNames;

    protected WOUTTestCenter() {
        initMainBundle();
    }

    static public synchronized WOUTTestCenter sharedTestCenter() {
        if (sharedTestCenter == null)
            sharedTestCenter = new WOUTTestCenter();
        return sharedTestCenter;
    }

    protected void initMainBundle() {
        String woAppPath = System.getProperty("WOAppPath");
        if (woAppPath != null) {
            NSBundle bundle = NSBundle._bundleWithPathShouldCreateIsJar(woAppPath, true, false);
            if (bundle != null)
                NSBundle._setMainBundle(bundle);
            else
                NSLog.err.appendln("can't find " + woAppPath + " or it isn't a valid WOApplication");
        }
    }
    
    protected NSArray bundles() {
        NSMutableArray bundles = new NSMutableArray(NSBundle.frameworkBundles());
        bundles.addObject(NSBundle.mainBundle());
        return bundles;
    }

    public NSArray classNames() {
        if (classNames == null) {
            String thisBundleName = NSBundle.bundleForClass(getClass()).name();
            NSMutableArray theClassNames = new NSMutableArray();
            Enumeration bundleEnum = bundles().objectEnumerator();
            while (bundleEnum.hasMoreElements()) {
                NSBundle bundle = (NSBundle)bundleEnum.nextElement();
                if (!bundle.name().equals(thisBundleName))
                    theClassNames.addObjectsFromArray(bundle.bundleClassNames());
            }
            classNames = theClassNames;
        }
        return classNames;
    }

    public void setClassNames(NSArray theClassNames) {
        classNames = theClassNames;
        testNames = null;
    }

    protected boolean isTestMethodInTestCase(Method aMethod, Class aClass) {
        return (aMethod.getParameterTypes().length == 0
                && aMethod.getReturnType().equals(Void.TYPE)
                && aMethod.getName().startsWith("test")
                && Modifier.isPublic(aMethod.getModifiers())
                && TestCase.class.isAssignableFrom(aClass));
    }

    protected boolean isSuiteMethod(Method aMethod) {
        int modifiers = aMethod.getModifiers();
        return (aMethod.getParameterTypes().length == 0
                && Test.class.isAssignableFrom(aMethod.getReturnType())
                && aMethod.getName().equals("suite")
                && Modifier.isStatic(modifiers)
                && Modifier.isPublic(modifiers));
    }

    protected boolean classHasTestMethods(Class aClass) {
        Enumeration methodEnum = new NSArray(aClass.getDeclaredMethods()).objectEnumerator();
        while (methodEnum.hasMoreElements()) {
            Method method = (Method)methodEnum.nextElement();
            if (isTestMethodInTestCase(method, aClass) || isSuiteMethod(method))
                return true;
        }
        return false;
    }

    protected boolean classIsAccessible(Class aClass) {
        int modifiers = aClass.getModifiers();
        return (!Modifier.isAbstract(modifiers)
                && !Modifier.isInterface(modifiers));
    }
    
    protected boolean classWithNameIsTestCase(String aClassName) {
        if (aClassName.endsWith("TestCase") || aClassName.endsWith("Test")) {
            try {
                Class testClass = Class.forName(aClassName);
                if (classIsAccessible(testClass))
                    return classHasTestMethods(testClass);
            } catch (Throwable e) {
                // Class.forName() can throw more than the declared ClassNotFoundException, for example ExceptionInInitializerError, that's why all Throwables are caught
                NSLog.err.appendln("error loading " + aClassName + " because of " + e.toString());
                //NSLog.debug.appendln(e);
            }
        }
        return false;
    }
    
    protected NSSet testCaseNameSet() {
        NSMutableSet testCaseNameSet = new NSMutableSet();
        Enumeration classEnum = classNames().objectEnumerator();
        while (classEnum.hasMoreElements()) {
            String className = (String)classEnum.nextElement();
            if (className != null && classWithNameIsTestCase(className))
                testCaseNameSet.addObject(className);
        }
        return testCaseNameSet;
    }

    public NSArray testNames() {
        if (testNames == null) {
            try {
                testNames = testCaseNameSet().allObjects().sortedArrayUsingComparator(NSComparator.AscendingStringComparator);
            } catch (NSComparator.ComparisonException e) {
                testNames = null;
            }
        }
        return testNames;
    }

	public boolean hasTests() {
		return (testNames() != null) ? (testNames().count() != 0) : false;
	}

}
