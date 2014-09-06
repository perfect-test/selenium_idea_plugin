package com.unknown.seleniumplugin.codecomplete.properties;

import com.unknown.seleniumplugin.codecomplete.exceptions.UnableToReadPropertiesException;
import com.unknown.seleniumplugin.domain.SeleniumCompletionVariant;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by mike-sid on 18.06.14.
 */
public class SeleniumPropertiesReader {
    private static final String TAGS_PROPERTY_NAME = "tags";
    private static final String START_ELEMENTS_PROPERTY_NAME = "start.elements";
    private static final String ATTRIBUTE_PATTERN_PROPERTY_NAME = "attributes.elements.patterns";
    private static final String ATTRIBUTES_PROPERTY_NAME = "attributes";
    private static final String FUNCTIONS_PROPERTY_NAME = "functions";
    private static final String XPATH_FUNCTIONS_PROPERTY_NAME = "xpath.functions";
    private static final String PATH_TO_PROPERTIES_FILE = "properties/selector_parts.properties";
    private static final String ATTRIBUTE_VALUE_REPLACE_PARAM = "{attribute_name}";
    private static final String FUNCTION_VALUE_REPLACE_PARAM = "{function_name}";
    private static final String XPATH_EQUALITY_FUNCTIONS_PROPERTY_NAME = "xpath.equality.functions";
    private static final String XPATH_SIMPLE_ATTRIBUTE_PATTERN_PROPERTY_NAME = "xpath.simple.attribute.pattern";
    private static final String XPATH_FUNCTION_ATTRIBUTE_PATTERN_PROPERTY_NAME = "xpath.simple.function.attribute.pattern";
    private static final String XPATH_EQUALITY_FUNCTION_ATTRIBUTE_PATTERN_PROPERTY_NAME = "xpath.equality.function.attribute.pattern";

    private static final String XPATH_FUNCTION_PARAM_NAME = "fn";

    private static List<String> attributesVariants;
    private static List<String> tagsVariants;
    private static List<String> fullStartElementsVariants;
    private static List<String> startElementsVariants;
    private static List<String> attributePatterns;
    private static List<String> attributesSelectorVariants;
    private static List<String> functionsVariants;
    private static List<String> xpathFunctions;
    private static List<String> xpathSimpleFunctions;
    private static List<String> xpathEqualityFunctions;
    private static List<String> xpathAttributesVariants;
    private static List<String> xpathFunctionsAttributesVariants;
    private static List<String> xpathEqualityFunctionsAttributesVariants;


    private static final Properties selectorProperties;

    static {
        try {
            selectorProperties = new Properties();
            InputStream inputStream = SeleniumPropertiesReader.class.getClassLoader().getResourceAsStream(PATH_TO_PROPERTIES_FILE);
            selectorProperties.load(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnableToReadPropertiesException("properties with path '" + PATH_TO_PROPERTIES_FILE + "' not loaded. Check file.");

        }

    }


    public static List<String> getAllTags() {
        if (tagsVariants == null) {
            tagsVariants = new ArrayList<String>();
            String tagsString = selectorProperties.getProperty(TAGS_PROPERTY_NAME);
            if (tagsString != null) {
                String[] variants = tagsString.split(",");
                Collections.addAll(tagsVariants, variants);
            }
        }
        return tagsVariants;
    }

    public static List<String> getXPathSimpleFunctions(){
        if(xpathSimpleFunctions == null) {
            xpathSimpleFunctions = new ArrayList<String>();
            String functionsString = selectorProperties.getProperty(XPATH_FUNCTIONS_PROPERTY_NAME);
            if (functionsString != null) {
                functionsString = functionsString.replaceAll(XPATH_FUNCTION_PARAM_NAME, "").replaceAll(":", "");
                String[] variants = functionsString.split(",");
                Collections.addAll(xpathSimpleFunctions, variants);
            }
        }
        return xpathSimpleFunctions;
    }

    public static List<String> getXpathFunctions() {
        if (xpathFunctions == null) {
            xpathFunctions = new ArrayList<String>();
            xpathFunctions.addAll(getXPathSimpleFunctions());
            xpathFunctions.addAll(getXpathEqualityFunctions());
        }
        return xpathFunctions;
    }


    public static List<String> getSeleniumStartElements() {
        if (fullStartElementsVariants == null) {
            fullStartElementsVariants = new ArrayList<String>();
            fullStartElementsVariants.addAll(getStartElementsList());
        }
        return fullStartElementsVariants;
    }


    public static List<String> getAttributesSelectorVariants() {
        if (attributesSelectorVariants == null) {
            attributesSelectorVariants = new ArrayList<String>();
            List<String> attributesPatterns = getAttributePatternsList();
            if (!attributesPatterns.isEmpty()) {
                List<String> attributeValues = getAttributesValuesList();
                for (String attributePattern : attributesPatterns) {
                    for (String attributeValue : attributeValues) {
                        attributesSelectorVariants.add(attributePattern.replace(ATTRIBUTE_VALUE_REPLACE_PARAM, attributeValue));
                    }
                }
            }

        }
        return attributesSelectorVariants;
    }

    public static List<String> getXpathAttributesSelectorVariants() {
        if (xpathAttributesVariants == null) {
            xpathAttributesVariants = new ArrayList<String>();
            String xpathAttributePattern = selectorProperties.getProperty(XPATH_SIMPLE_ATTRIBUTE_PATTERN_PROPERTY_NAME);
            if (xpathAttributePattern != null) {
                List<String> attributeValues = getAttributesValuesList();
                for (String attributeValue : attributeValues) {
                    xpathAttributesVariants.add(xpathAttributePattern.replace(ATTRIBUTE_VALUE_REPLACE_PARAM, attributeValue));
                }
            }

        }
        return xpathAttributesVariants;
    }

    public static List<String> getXpathFunctionsAttributesSelectorVariants() {
        if (xpathFunctionsAttributesVariants == null) {
            xpathFunctionsAttributesVariants = new ArrayList<String>();
            String xpathFunctionAttributePattern = selectorProperties.getProperty(XPATH_FUNCTION_ATTRIBUTE_PATTERN_PROPERTY_NAME);
            if (xpathFunctionAttributePattern != null) {
                List<String> functions = getXPathSimpleFunctions();
                for (String function : functions) {
                    xpathFunctionsAttributesVariants.add(xpathFunctionAttributePattern.replace(FUNCTION_VALUE_REPLACE_PARAM, function));
                }
            }

        }
        return xpathFunctionsAttributesVariants;
    }

    public static List<String> getXpathEqualityFunctionsAttributesSelectorVariants() {
        if (xpathEqualityFunctionsAttributesVariants == null) {
            xpathEqualityFunctionsAttributesVariants = new ArrayList<String>();
            String xpathFunctionAttributePattern = selectorProperties.getProperty(XPATH_EQUALITY_FUNCTION_ATTRIBUTE_PATTERN_PROPERTY_NAME);
            if (xpathFunctionAttributePattern != null) {
                List<String> functions = getXpathEqualityFunctions();
                for (String function : functions) {
                    xpathEqualityFunctionsAttributesVariants.add(xpathFunctionAttributePattern.replace(FUNCTION_VALUE_REPLACE_PARAM, function));
                }
            }

        }
        return xpathEqualityFunctionsAttributesVariants;
    }





    public static List<String> getAttributesValuesList() {
        if (attributesVariants == null) {
            attributesVariants = new ArrayList<String>();
            String attributes = selectorProperties.getProperty(ATTRIBUTES_PROPERTY_NAME);
            if (attributes != null) {
                String[] variants = attributes.split(",");
                Collections.addAll(attributesVariants, variants);
            }
        }
        return attributesVariants;
    }

    private static List<String> getAttributePatternsList() {
        if (attributePatterns == null) {
            attributePatterns = new ArrayList<String>();
            String patternsString = selectorProperties.getProperty(ATTRIBUTE_PATTERN_PROPERTY_NAME);
            if (attributePatterns != null) {
                String[] variants = patternsString.split(",");
                Collections.addAll(attributePatterns, variants);
            }
        }
        return attributePatterns;
    }


    private static List<String> getStartElementsList() {
        if (startElementsVariants == null) {
            startElementsVariants = new ArrayList<String>();
            String startElementsString = selectorProperties.getProperty(START_ELEMENTS_PROPERTY_NAME);
            if (startElementsString != null) {
                String[] variants = startElementsString.split(",");
                Collections.addAll(startElementsVariants, variants);
            }
        }
        return startElementsVariants;
    }

    public static List<String> getFunctions() {
        if (functionsVariants == null) {
            functionsVariants = new ArrayList<String>();
            String functionsValuesString = selectorProperties.getProperty(FUNCTIONS_PROPERTY_NAME);
            if (functionsValuesString != null) {
                String[] variants = functionsValuesString.split(",");
                Collections.addAll(functionsVariants, variants);
            }
        }
        return functionsVariants;
    }

    public static List<String> getXpathEqualityFunctions() {
        if (xpathEqualityFunctions == null) {
            xpathEqualityFunctions = new ArrayList<String>();
            String functionsString = selectorProperties.getProperty(XPATH_EQUALITY_FUNCTIONS_PROPERTY_NAME);
            if (functionsString != null) {
                functionsString = functionsString.replaceAll(XPATH_FUNCTION_PARAM_NAME, "").replaceAll(":", "");
                String[] variants = functionsString.split(",");
                Collections.addAll(xpathEqualityFunctions, variants);
            }
        }
        return xpathEqualityFunctions;
    }
}
