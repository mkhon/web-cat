package net.sf.webcat.reporter;

import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;

public interface IReportParameterConstants
{
    // Property keys that apply both to parameters and parameter groups
    static final String NAME_KEY = "name"; // String

    // Property keys that apply only to parameter groups
    static final String PARAMETERS_KEY = "parameters"; // NSArray
    static final String DISPLAY_NAME_KEY = "displayName"; // String

    // Property keys that apply only to parameters
    static final String PROMPT_TEXT_KEY = "promptText"; // String
    static final String HELP_TEXT_KEY = "helpText"; // String
    static final String DATA_TYPE_KEY = "dataType"; // String (TYPE_ below)
    static final String DEFAULT_VALUE_KEY = "defaultValue"; // String
    static final String REQUIRED_KEY = "required"; // Boolean
    static final String HIDDEN_KEY = "hidden"; // Boolean
    static final String CONCEALED_KEY = "concealed"; // Boolean

    // Type strings used by DATA_TYPE_KEY
    static final String TYPE_STRING = DesignChoiceConstants.PARAM_TYPE_STRING;
    static final String TYPE_FLOAT = DesignChoiceConstants.PARAM_TYPE_FLOAT;
    static final String TYPE_DECIMAL = DesignChoiceConstants.PARAM_TYPE_DECIMAL;
    static final String TYPE_INTEGER = DesignChoiceConstants.PARAM_TYPE_INTEGER;
    static final String TYPE_DATE = DesignChoiceConstants.PARAM_TYPE_DATE;
    static final String TYPE_TIME = DesignChoiceConstants.PARAM_TYPE_TIME;
    static final String TYPE_DATETIME =
        DesignChoiceConstants.PARAM_TYPE_DATETIME;
    static final String TYPE_BOOLEAN = DesignChoiceConstants.PARAM_TYPE_BOOLEAN;
}
