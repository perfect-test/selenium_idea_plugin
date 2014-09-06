package com.unknown.seleniumplugin.codecomplete.generators.impl;

import com.unknown.seleniumplugin.codecomplete.generators.ICompletionVariantsGenerator;
import com.unknown.seleniumplugin.codecomplete.properties.SeleniumPropertiesReader;
import com.unknown.seleniumplugin.domain.SeleniumCompletionVariant;
import com.unknown.seleniumplugin.domain.XPathSelectorSymbolConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike-sid on 22.08.14.
 */
public class XpathCompletionVariantsGenerator implements ICompletionVariantsGenerator {
    private static final String CARET_POSITION_ELEMENT_VALUE = "{cp}";


    @Override
    public List<SeleniumCompletionVariant> generateVariants(String selectorValueBefore) {
        List<SeleniumCompletionVariant> variants = new ArrayList<SeleniumCompletionVariant>();
        boolean isStartOfSelector = false;
        boolean replaceLastDigitInBeforeString = false;
        String tagName;
        String attributeName;
        String functionName;
        String indexValue;
        List<String> variantsStrings = new ArrayList<String>();
        if (selectorValueBefore.isEmpty()) {
            isStartOfSelector = true;
            addAllStartVariants(variantsStrings);
        } else if (selectorValueBefore.endsWith(XPathSelectorSymbolConstants.START_STEP_SYMBOL)) {
            variantsStrings.addAll(SeleniumPropertiesReader.getAllTags());
        } else if ((tagName = getEndsTagName(selectorValueBefore)) != null) {
            if (isWordInAttributeValue(selectorValueBefore, tagName)) {
                variantsStrings.add(
                        XPathSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                                XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT
                );
            } else if (isStartStepTagName(selectorValueBefore, tagName)) {
                variantsStrings.add(XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_START_ELEMENT);
                variantsStrings.add(XPathSelectorSymbolConstants.START_STEP_SYMBOL);
            } else {
                return variants;
            }
        } else if (selectorValueBefore.endsWith(XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_START_ELEMENT)) {
            replaceLastDigitInBeforeString = true;
            variantsStrings.addAll(SeleniumPropertiesReader.getXpathAttributesSelectorVariants());
            variantsStrings.addAll(SeleniumPropertiesReader.getXpathEqualityFunctionsAttributesSelectorVariants());
            variantsStrings.addAll(SeleniumPropertiesReader.getXpathFunctionsAttributesSelectorVariants());
        } else if (selectorValueBefore.endsWith(XPathSelectorSymbolConstants.ATTRIBUTE_NAME_START_SYMBOL)) {
            variantsStrings.addAll(SeleniumPropertiesReader.getAttributesValuesList());
        } else if ((attributeName = getEndsAttributeName(selectorValueBefore)) != null) {
            if (isWordInAttributeValue(selectorValueBefore, attributeName)) {
                variantsStrings.add(
                        XPathSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                                XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT
                );
            } else if (isStartAttributeSearchAttributeName(selectorValueBefore, attributeName)) {
                variantsStrings.add(
                        XPathSelectorSymbolConstants.EQUAL_SYMBOL +
                                XPathSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                                CARET_POSITION_ELEMENT_VALUE +
                                XPathSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                                XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT
                );
            }
        } else if((functionName = getEndsFunctionName(selectorValueBefore))!= null) {
            if (isWordInAttributeValue(selectorValueBefore, functionName)) {
                variantsStrings.add(
                        XPathSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                                XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT
                );
            } else if(isStartAttributeSearchFunctionName(selectorValueBefore, functionName)) {
                String toAdd = null;
                if(SeleniumPropertiesReader.getXPathSimpleFunctions().contains(functionName)) {
                    toAdd = XPathSelectorSymbolConstants.OPENING_BRACE_ELEMENT +
                            CARET_POSITION_ELEMENT_VALUE +
                            XPathSelectorSymbolConstants.CLOSING_BRACE_ELEMENT +
                            XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT;
                } else if (SeleniumPropertiesReader.getXpathEqualityFunctions().contains(functionName)) {
                    toAdd = XPathSelectorSymbolConstants.OPENING_BRACE_ELEMENT +
                            XPathSelectorSymbolConstants.CLOSING_BRACE_ELEMENT +
                            XPathSelectorSymbolConstants.EQUAL_SYMBOL +
                            XPathSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                            CARET_POSITION_ELEMENT_VALUE +
                            XPathSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                            XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT;
                } else {
                    toAdd = XPathSelectorSymbolConstants.OPENING_BRACE_ELEMENT +
                            XPathSelectorSymbolConstants.CLOSING_BRACE_ELEMENT +
                            XPathSelectorSymbolConstants.EQUAL_SYMBOL +
                            XPathSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                            CARET_POSITION_ELEMENT_VALUE +
                            XPathSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                            XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT;
                }
                variantsStrings.add(toAdd);
            }
        } else if((indexValue = getEndsIndex(selectorValueBefore)) != null) {
            if (isWordInAttributeValue(selectorValueBefore, indexValue)) {
                variantsStrings.add(
                        XPathSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                                XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT
                );
            } else if(isInAttributeSearchIndex(selectorValueBefore, indexValue)) {
                variantsStrings.add(XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT);
            }
        } else if(selectorValueBefore.endsWith(XPathSelectorSymbolConstants.EQUAL_SYMBOL)) {
            variantsStrings.add(XPathSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                    CARET_POSITION_ELEMENT_VALUE +
                    XPathSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                    XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT
            );
        } else if(selectorValueBefore.endsWith(XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT)) {
            variantsStrings.add(XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_START_ELEMENT);
        }
        if (replaceLastDigitInBeforeString) {
            selectorValueBefore = selectorValueBefore.substring(0, selectorValueBefore.length() - 1);
        }
        for (String variant : variantsStrings) {
            String finalVariant = selectorValueBefore + variant;
            if (!isStartOfSelector) {
                finalVariant = finalVariant.substring(2, finalVariant.length());
            }
            int caretPositionIndex = finalVariant.indexOf(CARET_POSITION_ELEMENT_VALUE);
            SeleniumCompletionVariant completionVariant = new SeleniumCompletionVariant();
            if (caretPositionIndex >= 0) {
                completionVariant.setCaretOffset(caretPositionIndex);
            } else {
                completionVariant.setCaretOffset(finalVariant.length());
            }
            completionVariant.setVariantString(finalVariant.replace(CARET_POSITION_ELEMENT_VALUE, ""));
            variants.add(completionVariant);
        }
        return variants;
    }

    private boolean isInAttributeSearchIndex(String selectorValueBefore, String indexValue) {
        return selectorValueBefore.endsWith(XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_START_ELEMENT + indexValue);
    }


    private boolean isStartAttributeSearchFunctionName(String selectorValueBefore, String functionName) {
        return selectorValueBefore.endsWith(XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_START_ELEMENT + functionName);
    }

    private boolean isStartAttributeSearchAttributeName(String selectorValueBefore, String attributeName) {
        return selectorValueBefore.endsWith(XPathSelectorSymbolConstants.ATTRIBUTE_NAME_START_SYMBOL + attributeName);
    }

    private boolean isWordInAttributeValue(String selectorValueBefore, String word) {
        for (int i = selectorValueBefore.length() - word.length(); i > 0; i--) {
            String element = String.valueOf(selectorValueBefore.charAt(i));
            if (element.equals(XPathSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL)) {
                return true;
            } else if (element.equals(XPathSelectorSymbolConstants.ATTRIBUTE_NAME_START_SYMBOL)) {
                return false;
            }
        }
        return false;
    }

    private boolean isStartStepTagName(String selectorValueBefore, String tagName) {
        return selectorValueBefore.endsWith(XPathSelectorSymbolConstants.START_STEP_SYMBOL + tagName);
    }

    private String getLastSelectorWord(String selectorValueBefore) {
        int startWordIndex = selectorValueBefore.length();
        for (int i = selectorValueBefore.length() - 1; i >= 0; i--) {
            String elementText = String.valueOf(selectorValueBefore.charAt(i));
            if (elementText.equals(" ") ||
                    elementText.equals(XPathSelectorSymbolConstants.ATTRIBUTE_NAME_START_SYMBOL) ||
                    elementText.equals(XPathSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL) ||
                    elementText.equals(XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_START_ELEMENT) ||
                    elementText.equals(XPathSelectorSymbolConstants.START_STEP_SYMBOL)) {
                startWordIndex = i;
                break;
            }
        }
        if (startWordIndex == selectorValueBefore.length()) {
            return null;
        } else {
            return selectorValueBefore.substring(startWordIndex + 1, selectorValueBefore.length());
        }
    }

    private String getEndsAttributeName(String selectorValueBefore) {
        String attributeName = getLastSelectorWord(selectorValueBefore);
        if (attributeName != null) {
            List<String> attributes = SeleniumPropertiesReader.getAttributesValuesList();
            for (String attribute : attributes) {
                if (attributeName.equals(attribute)) {
                    return attribute;
                }
            }
        }
        return null;

    }

    private String getEndsIndex(String selectorValueBefore) {
        String indexValue = getLastSelectorWord(selectorValueBefore);
        if(indexValue != null) {
            try{
                Integer integer = Integer.valueOf(indexValue);
                return indexValue;
            } catch (Exception e) {
                //do nothing
            }
        }
        return null;
    }



    private String getEndsFunctionName(String selectorValueBefore) {
        String functionName = getLastSelectorWord(selectorValueBefore);
        if (functionName != null) {
            List<String> functions = SeleniumPropertiesReader.getXpathFunctions();
            for (String function : functions) {
                if (functionName.equals(function)) {
                    return function;
                }
            }
        }
        return null;
    }

    private String getEndsTagName(String selectorValueBefore) {
        String tagName = getLastSelectorWord(selectorValueBefore);
        if (tagName != null) {
            List<String> tags = SeleniumPropertiesReader.getAllTags();
            for (String tag : tags) {
                if (tagName.equals(tag)) {
                    return tag;
                }
            }
        }
        return null;
    }


//        String[] splittedSelectorValue = selectorValueBefore.split("/");
//        if (splittedSelectorValue.length > 0) {
//            String lastValue = splittedSelectorValue[splittedSelectorValue.length - 1];
//            if (!lastValue.isEmpty()) {
//                for (String tag : tags) {
//                    if (lastValue.equals(tag)) {
//                        return tag;
//                    }
//                }
//            }
//        }
//        return null;

    private void addAllStartVariants(List<String> variantsStrings) {
        variantsStrings.add("//");
    }
}
