package com.unknown.seleniumplugin.codecomplete.generators.impl;

import com.unknown.seleniumplugin.codecomplete.generators.ICompletionVariantsGenerator;
import com.unknown.seleniumplugin.domain.CssSelectorSymbolConstants;
import com.unknown.seleniumplugin.domain.SeleniumCompletionVariant;
import com.unknown.seleniumplugin.codecomplete.properties.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike-sid on 17.06.14.
 */
public class CssCompletionVariantsGenerator implements ICompletionVariantsGenerator {
    private static final String CARET_POSITION_ELEMENT_VALUE = "{cp}";

    private enum CompletionBase {
        CLASS_NAME(CssSelectorSymbolConstants.CLASS_SYMBOL),
        ID_NAME(CssSelectorSymbolConstants.ID_SYMBOL),
        ATTRIBUTE_NAME(CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_START_ELEMENT),
        ATTRIBUTE_VALUE(CssSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL),
        FUNCTION_NAME(CssSelectorSymbolConstants.FUNCTION_START_SYMBOL),
        ATTRIBUTE_EQUALITY(CssSelectorSymbolConstants.EQUAL_SYMBOL),
        ATTRIBUTE_VALUE_END(CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT),
        FUNCTION_INDEX_START(CssSelectorSymbolConstants.FUNCTION_INDEX_OPENING_SYMBOL),
        FUNCTION_INDEX_END(CssSelectorSymbolConstants.FUNCTION_INDEX_CLOSING_SYMBOL),
        ;


        private final String baseSymbol;

        private CompletionBase(String baseSymbol) {
            this.baseSymbol = baseSymbol;
        }

        public String getBaseSymbol() {
            return baseSymbol;
        }

        public static CompletionBase getByBaseSymbol(char baseSymbol) {
            for (CompletionBase base : values()) {
                if (base.getBaseSymbol().equals(String.valueOf(baseSymbol))) {
                    return base;
                }
            }
            return null;
        }
    }

    private class CompletionEntry {
        private String value;
        private CompletionBase base;

        private CompletionEntry(String value, CompletionBase base) {
            this.value = value;
            this.base = base;
        }

        public String getValue() {
            return value;
        }

        public CompletionBase getBase() {
            return base;
        }
    }

    private void addAllStartVariants(List<String> variantsStrings) {
        variantsStrings.addAll(SeleniumPropertiesReader.getAllTags());
        variantsStrings.addAll(SeleniumPropertiesReader.getSeleniumStartElements());
    }

    @Override
    public List<SeleniumCompletionVariant> generateVariants(String selectorValueBefore) {
        List<SeleniumCompletionVariant> variants = new ArrayList<SeleniumCompletionVariant>();
        boolean replaceLastDigitInBeforeString = false;
        List<String> variantsStrings = new ArrayList<String>();
        if (selectorValueBefore.isEmpty()) {
            addAllStartVariants(variantsStrings);
        } else if (selectorValueBefore.endsWith(CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_START_ELEMENT)) {
            replaceLastDigitInBeforeString = true;
            variantsStrings.addAll(SeleniumPropertiesReader.getAttributesSelectorVariants());
        } else if (selectorValueBefore.endsWith(CssSelectorSymbolConstants.ID_SYMBOL)) {
            return variants;
        } else if (selectorValueBefore.endsWith(CssSelectorSymbolConstants.CLASS_SYMBOL)) {
            return variants;
        } else if (selectorValueBefore.endsWith(CssSelectorSymbolConstants.ANY_CHARACTER_SYMBOL)) {
            CompletionEntry entry = getCompletionEntryFromEnteredString(selectorValueBefore);
            if (entry != null) {
                if (entry.getBase().equals(CompletionBase.ATTRIBUTE_NAME)) {
                    variantsStrings.add(CssSelectorSymbolConstants.EQUAL_SYMBOL +
                            CssSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                            CARET_POSITION_ELEMENT_VALUE +
                            CssSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                            CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT);
                }
            } else {
                variantsStrings.add(CssSelectorSymbolConstants.FUNCTION_START_SYMBOL);
            }

        } else if (selectorValueBefore.endsWith(CssSelectorSymbolConstants.FUNCTION_START_SYMBOL)) {
            variantsStrings.addAll(SeleniumPropertiesReader.getFunctions());
        } else if (selectorValueBefore.endsWith(CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT)) {
            variantsStrings.add(" ");
            variantsStrings.add(CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_START_ELEMENT);
        } else if (selectorValueBefore.endsWith(CssSelectorSymbolConstants.EQUAL_SYMBOL)) {
            variantsStrings.add(CssSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                    CARET_POSITION_ELEMENT_VALUE +
                    CssSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                    CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT);
        } else if (selectorValueBefore.endsWith(" ")) {
            CompletionEntry entry = getCompletionEntryFromEnteredString(selectorValueBefore);
            if (entry != null) {
                String entryValue = entry.getValue().trim();
                switch (entry.getBase()) {
                    case ATTRIBUTE_NAME:
                        if (entryValue.isEmpty()) {
                            replaceLastDigitInBeforeString = true;
                            variantsStrings.addAll(SeleniumPropertiesReader.getAttributesSelectorVariants());
                        } else {
                            List<String> attributesNames = SeleniumPropertiesReader.getAttributesSelectorVariants();
                            for (String attributeName : attributesNames) {
                                if (attributeName.startsWith(entryValue, 1)) {
                                    variantsStrings.add(attributeName.substring(entryValue.length() + 1, attributeName.length()));
                                }
                            }
                        }
                        selectorValueBefore = selectorValueBefore.trim();
                        break;
                    case ATTRIBUTE_EQUALITY:
                        variantsStrings.add(CssSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                                CARET_POSITION_ELEMENT_VALUE +
                                CssSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                                CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT);
                        selectorValueBefore = selectorValueBefore.trim();
                        break;
                    case ATTRIBUTE_VALUE:
                        if (entryValue.isEmpty()) {
                            String temp = selectorValueBefore.trim();
                            CompletionEntry entryBefore = getCompletionEntryFromEnteredString(temp.substring(0, temp.length() - 1));
                            if (entryBefore != null) {
                                if (entryBefore.getBase().equals(CompletionBase.ATTRIBUTE_EQUALITY)) {
                                    variantsStrings.add(CARET_POSITION_ELEMENT_VALUE +
                                            CssSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                                            CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT);
                                } else {
                                    variantsStrings.add(CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT);
                                }
                            }
                        } else {
                            variantsStrings.add(CARET_POSITION_ELEMENT_VALUE +
                                    CssSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                                    CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT);
                        }
                        selectorValueBefore = selectorValueBefore.trim();
                        break;
                    case ATTRIBUTE_VALUE_END:
                        addAllStartVariants(variantsStrings);
                        break;
                    case FUNCTION_INDEX_START:
                        variantsStrings.add(CARET_POSITION_ELEMENT_VALUE + CssSelectorSymbolConstants.FUNCTION_INDEX_CLOSING_SYMBOL);
                        selectorValueBefore = selectorValueBefore.trim();
                        break;
                    case FUNCTION_INDEX_END:
                        addAllStartVariants(variantsStrings);
                        break;
                    default:
                        addAllStartVariants(variantsStrings);
                        break;
                }
            } else {
                addAllStartVariants(variantsStrings);
            }

        } else if (selectorValueBefore.endsWith(CssSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL)) {
            CompletionEntry entry = getCompletionEntryFromEnteredString(selectorValueBefore.substring(0, selectorValueBefore.length() - 1));
            if (entry != null) {
                if (entry.getBase().equals(CompletionBase.ATTRIBUTE_EQUALITY)) {
                    variantsStrings.add(CARET_POSITION_ELEMENT_VALUE +
                            CssSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL +
                            CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT);
                } else {
                    variantsStrings.add(CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT);
                }
            }
        } else if (selectorValueBefore.endsWith(CssSelectorSymbolConstants.ATTRIBUTE_VALUE_ENDS_WITH_SYMBOL) ||
                selectorValueBefore.endsWith(CssSelectorSymbolConstants.ATTRIBUTE_VALUE_STARTS_WITH_SYMBOL)) {
            variantsStrings.add(CssSelectorSymbolConstants.EQUAL_SYMBOL);
        } else if (selectorValueBefore.endsWith(CssSelectorSymbolConstants.FUNCTION_INDEX_OPENING_SYMBOL)) {
            variantsStrings.add(CARET_POSITION_ELEMENT_VALUE + CssSelectorSymbolConstants.FUNCTION_INDEX_CLOSING_SYMBOL);
        } else if (selectorValueBefore.endsWith(CssSelectorSymbolConstants.FUNCTION_INDEX_CLOSING_SYMBOL)) {
            variantsStrings.add(" ");
        } else if (isEndsWithTagName(selectorValueBefore)) {
            variantsStrings.add(CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_START_ELEMENT);
            variantsStrings.add(CssSelectorSymbolConstants.CLASS_SYMBOL);
            variantsStrings.add(CssSelectorSymbolConstants.FUNCTION_START_SYMBOL);
            variantsStrings.add(" ");
        }

        else {
            CompletionEntry entry = getCompletionEntryFromEnteredString(selectorValueBefore);
            if (entry != null) {
                String entryValue = entry.getValue();
                switch (entry.getBase()) {
                    case CLASS_NAME:
                    case ID_NAME:
                        return variants;
                    case FUNCTION_NAME:
                        List<String> functionNames = SeleniumPropertiesReader.getFunctions();
                        for (String functionName : functionNames) {
                            if (functionName.startsWith(entryValue)) {
                                variantsStrings.add(functionName.substring(entryValue.length(), functionName.length()));
                            }
                        }
                        break;
                    case ATTRIBUTE_VALUE:
                        variantsStrings.add("']");
                        break;
                    case ATTRIBUTE_NAME:
                        List<String> attributesNames = SeleniumPropertiesReader.getAttributesSelectorVariants();
                        for (String attributeName : attributesNames) {
                            if (attributeName.startsWith(entryValue, 1)) {
                                variantsStrings.add(attributeName.substring(entryValue.length() + 1, attributeName.length()));
                            }
                        }
                        break;
                    default:
                        return variants;
                }
            }
        }
        if (replaceLastDigitInBeforeString) {
            selectorValueBefore = selectorValueBefore.substring(0, selectorValueBefore.length() - 1);
        }
        for (String variant : variantsStrings) {
            String finalVariant = selectorValueBefore + variant;
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

    private boolean isEndsWithTagName(String selectorValueBefore) {
        List<String> tags = SeleniumPropertiesReader.getAllTags();
        String[] splittedSelectorValue = selectorValueBefore.split(" ");
        if(splittedSelectorValue.length > 0) {
            String lastValue = splittedSelectorValue[splittedSelectorValue.length - 1];
            if(!lastValue.isEmpty()) {
                for(String tag : tags) {
                    if(lastValue.equals(tag)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private CompletionEntry getCompletionEntryFromEnteredString(String selectorValueBefore) {
        for (int i = selectorValueBefore.length() - 1; i >= 0; i--) {
            CompletionBase base;
            if ((base = CompletionBase.getByBaseSymbol(selectorValueBefore.charAt(i))) != null) {
                return new CompletionEntry(selectorValueBefore.substring(i + 1, selectorValueBefore.length()), base);
            }
        }
        return null;
    }
}
