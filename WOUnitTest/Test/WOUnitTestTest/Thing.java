
package com.codefab.testwounittest;

import com.webobjects.foundation.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import java.math.BigDecimal;
import java.util.*;
import org.pekeler.eof.*;


public class Thing extends EOGenericRecord {

    public void awakeFromInsertion(EOEditingContext anEditingContext) {
        super.awakeFromInsertion(anEditingContext);
        editingContext().hasChanges();  // this used to trigger a bug in WOUTMockEditingContext
    }
    
    public Thing() {
        super();
    }

    public String name() {
        return (String)storedValueForKey("name");
    }

    public void setName(String value) {
        takeStoredValueForKey(value, "name");
    }

    public Number type() {
        return (Number)storedValueForKey("type");
    }

    public void setType(Number value) {
        takeStoredValueForKey(value, "type");
    }
    
    protected static String[] uniqueKeyArray = new String[] {"name", "type"};
    public void validateUniqueness() throws NSValidation.ValidationException {
        NSDictionary keyValueDictionary = new NSDictionary(new Object[] {name(), type()}, uniqueKeyArray);
        if (EOFetcher.objectNotUniqueForMatchingValues(this, keyValueDictionary))
            throw new NSValidation.ValidationException("name and type is not unique");
    }
    
    public void validateForSave() throws NSValidation.ValidationException {
        super.validateForSave();
        validateUniqueness();
    }
    
}
