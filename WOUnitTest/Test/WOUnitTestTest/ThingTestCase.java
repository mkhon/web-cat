package com.codefab.testwounittest;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eoaccess.*;
import com.codefab.wounittest.*;


public class ThingTestCase extends WOUTTestCase {

    public void testUnique() {
        Thing thing1 = new Thing();
        editingContext().insertObject(thing1);
        registerPersistentRootObjectForDeletion(thing1);
        thing1.setName("blah");
        thing1.setType(new Integer(1));
        editingContext().saveChanges();
        
        Thing thing2 = new Thing();
        editingContext().insertObject(thing2);
        thing2.setName("blah");
        thing2.setType(new Integer(1));
        assertInvalidForSave(thing2);
        
        thing2.setName("blah2");
        assertValidForSave(thing2);
        
        thing2.setName("blah");
        thing2.setType(new Integer(2));
        assertValidForSave(thing2);
    }
    
    public void testUniqueMultiSave() {
        Thing thing1 = new Thing();
        editingContext().insertObject(thing1);
        thing1.setName("blah");
        thing1.setType(new Integer(1));
        assertValidForSave(thing1);
        
        Thing thing2 = new Thing();
        editingContext().insertObject(thing2);
        thing2.setName("blah");
        thing2.setType(new Integer(1));
        assertInvalidForSave(thing1);
        assertInvalidForSave(thing2);
    }
    
    public void testUniqueWithMock() {
        Thing thing1 = (Thing)mockEditingContext().createSavedObject("Thing");
        thing1.setName("blah");
        thing1.setType(new Integer(1));
        
        Thing thing2 = new Thing();
        mockEditingContext().insertObject(thing2);
        thing2.setName("blah");
        thing2.setType(new Integer(1));
        assertInvalidForSave(thing2);
        
        thing2.setName("blah2");
        assertValidForSave(thing2);
        
        thing2.setName("blah");
        thing2.setType(new Integer(2));
        assertValidForSave(thing2);
    }
    
    public void testUniqueMultiSaveWithMock() {
        Thing thing1 = new Thing();
        mockEditingContext().insertObject(thing1);
        thing1.setName("blah");
        thing1.setType(new Integer(1));
        assertValidForSave(thing1);
        
        Thing thing2 = new Thing();
        mockEditingContext().insertObject(thing2);
        thing2.setName("blah");
        thing2.setType(new Integer(1));
        assertInvalidForSave(thing1);
        assertInvalidForSave(thing2);
    }
    
}
