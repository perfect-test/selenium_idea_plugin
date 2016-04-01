package com.unknown.seleniumplugin.codecomplete.generators.impl;

import com.unknown.seleniumplugin.codecomplete.generators.ICompletionVariantsGenerator;
import com.unknown.seleniumplugin.codecomplete.properties.SeleniumPropertiesReader;
import com.unknown.seleniumplugin.domain.SeleniumCompletionVariant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike-sid on 18.08.14.
 */
public class TagNameCompletionVariantsGenerator implements ICompletionVariantsGenerator {
    private static final String CARET_POSITION_ELEMENT_VALUE = "{cp}";

    private void addAllStartVariants(List<String> variantsStrings) {
        variantsStrings.addAll(SeleniumPropertiesReader.getAllTags());
    }

    @Override
    public List<SeleniumCompletionVariant> generateVariants(String selectorValueBefore) {
        List<SeleniumCompletionVariant> variants = new ArrayList<SeleniumCompletionVariant>();
        List<String> variantsStrings = new ArrayList<String>();
        if(selectorValueBefore.isEmpty()) {
            addAllStartVariants(variantsStrings);
        } else {
            variantsStrings.addAll(getVariantsBasedOnInsertedValue(selectorValueBefore));
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

    private List<String> getVariantsBasedOnInsertedValue(String selectorValueBefore) {
        List<String> result = new ArrayList<String>();
        List<String> attributesNames = SeleniumPropertiesReader.getAllTags();
        String entryValue = selectorValueBefore.trim();
        for (String attributeName : attributesNames) {
            if (attributeName.startsWith(entryValue, 1)) {
                result.add(attributeName.substring(entryValue.length() + 1, attributeName.length()));
            }
        }
        return result;

    }
}
