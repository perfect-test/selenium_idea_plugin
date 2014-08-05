package com.unknown.seleniumplugin.generatefield.backend;

import com.unknown.seleniumplugin.pluginproperties.GlobalPluginProperties;

/**
 * Created by mike-sid on 05.08.14.
 */
public interface FieldsGenerationTemplates {
    String CARET_POSITION_PARAM_NAME = "{cp}";
    String FIELD_NAME_PARAM_NAME = "{fieldName}";
    String LOCATOR_METHOD_PARAM_NAME = "{locatorMethod}";
    String SINGLE_FIELD_TEMPLATE = "\t@FindBy(" + LOCATOR_METHOD_PARAM_NAME + " = \"" + CARET_POSITION_PARAM_NAME + "\")\n" +
            "\tprivate " + GlobalPluginProperties.WEB_ELEMENT_FIELD_TYPE + " " + FIELD_NAME_PARAM_NAME + ";\n";

    String LIST_FIELD_TEMPLATE = "\t@FindBy(" + LOCATOR_METHOD_PARAM_NAME + " = \"" + CARET_POSITION_PARAM_NAME + "\")\n" +
            "\tprivate List<" + GlobalPluginProperties.WEB_ELEMENT_FIELD_TYPE + "> " + FIELD_NAME_PARAM_NAME + ";\n";


}
