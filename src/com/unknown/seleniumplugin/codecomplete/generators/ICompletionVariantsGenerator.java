package com.unknown.seleniumplugin.codecomplete.generators;

import com.unknown.seleniumplugin.domain.SeleniumCompletionVariant;

import java.util.List;

/**
 * Created by mike-sid on 17.06.14.
 */
public interface ICompletionVariantsGenerator {


    /**
     * getting variants for completion
     * @param selectorValueBefore value of selector before caret
     * @return list of variants{@link com.unknown.seleniumplugin.domain.SeleniumCompletionVariant}
     */
    List<SeleniumCompletionVariant> generateVariants(String selectorValueBefore);
}
