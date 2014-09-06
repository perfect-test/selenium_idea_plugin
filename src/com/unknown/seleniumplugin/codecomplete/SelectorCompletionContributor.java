package com.unknown.seleniumplugin.codecomplete;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.unknown.seleniumplugin.codecomplete.generators.ICompletionVariantsGenerator;
import com.unknown.seleniumplugin.codecomplete.generators.impl.TagNameCompletionVariantsGenerator;
import com.unknown.seleniumplugin.codecomplete.generators.impl.CssCompletionVariantsGenerator;
import com.unknown.seleniumplugin.codecomplete.generators.impl.XpathCompletionVariantsGenerator;
import com.unknown.seleniumplugin.codecomplete.inserthandlers.ISeleniumInsertHandler;
import com.unknown.seleniumplugin.codecomplete.inserthandlers.impl.CssInsertHandler;
import com.unknown.seleniumplugin.codecomplete.inserthandlers.impl.XpathInsertHandler;
import com.unknown.seleniumplugin.domain.SelectorMethodValue;
import com.unknown.seleniumplugin.domain.SeleniumCompletionVariant;
import com.unknown.seleniumplugin.utils.AnnotationChecker;
import com.unknown.seleniumplugin.utils.AnnotationsUtils;
import com.unknown.seleniumplugin.utils.PsiCommonUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * Created by mike-sid on 16.06.14.
 */
public class SelectorCompletionContributor extends CompletionContributor {
    private static final ISeleniumInsertHandler CSS_INSERT_HANDLER = new CssInsertHandler();
    private static final ISeleniumInsertHandler XPATH_INSERT_HANDLER = new XpathInsertHandler();
    private static final ICompletionVariantsGenerator CSS_COMPLETION_VARIANTS_GENERATOR = new CssCompletionVariantsGenerator();
    private static final ICompletionVariantsGenerator TAG_NAME_COMPLETION_VARIANTS_GENERATOR = new TagNameCompletionVariantsGenerator();
    private static final ICompletionVariantsGenerator XPATH_COMPLETION_VARIANTS_GENERATOR = new XpathCompletionVariantsGenerator();

    public SelectorCompletionContributor() {
        extend(CompletionType.BASIC, psiElement(), new CompletionProvider<CompletionParameters>() {
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                String locator = null;
                SelectorMethodValue selectorMethodValue = null;
                PsiElement locatorElement = parameters.getPosition().getParent();
                if (locatorElement != null && locatorElement instanceof PsiLiteralExpression) {
                    locator = PsiCommonUtils.getLocatorValue(locatorElement);
                    selectorMethodValue = PsiCommonUtils.getSelectorValue(locatorElement);
                    System.out.println(locator + ":" + selectorMethodValue);
                }
                if (selectorMethodValue != null && locator != null) {
                    List<SeleniumCompletionVariant> completionVariants = getCompletionVariants(selectorMethodValue, locator);
                    if (completionVariants != null) {
                        addVariantsToResult(completionVariants, result, selectorMethodValue);
                    }
                }
            }
        });
    }

    private void addVariantsToResult(List<SeleniumCompletionVariant> completionVariants, CompletionResultSet result, SelectorMethodValue selectorMethodValue) {
        ISeleniumInsertHandler insertHandler = null;
        if (selectorMethodValue != null) {
            switch (selectorMethodValue) {
                case CSS:
                    insertHandler = CSS_INSERT_HANDLER;
                    break;
                case XPATH:
                    insertHandler = XPATH_INSERT_HANDLER;
                    break;
                default:
                    //do nothing
                    break;
            }
        }
        for (SeleniumCompletionVariant variant : completionVariants) {
            if (insertHandler != null) {
                result.addElement(LookupElementBuilder.create(variant, variant.getVariantString()).bold().withInsertHandler(insertHandler));
            } else {
                result.addElement(LookupElementBuilder.create(variant, variant.getVariantString()).bold());
            }
        }
    }

    private List<SeleniumCompletionVariant> getCompletionVariants(SelectorMethodValue selectorMethodValue,
                                                                  String locatorValue) {
        String leftValue = getValueBeforeCaret(locatorValue);
        System.out.println("left value : " + leftValue);
        ICompletionVariantsGenerator completionVariantsGenerator = null;
        if (selectorMethodValue != null) {
            switch (selectorMethodValue) {
                case CSS:
                    completionVariantsGenerator = CSS_COMPLETION_VARIANTS_GENERATOR;
                    break;
                case TAG_NAME:
                    completionVariantsGenerator = TAG_NAME_COMPLETION_VARIANTS_GENERATOR;
                    break;
                case XPATH:
                    completionVariantsGenerator = XPATH_COMPLETION_VARIANTS_GENERATOR;
                    break;
                default:
                    //do nothing
                    break;
            }
        }
        if (completionVariantsGenerator != null) {
            return completionVariantsGenerator.generateVariants(leftValue);
        } else {
            return null;
        }
    }


    private List<SeleniumCompletionVariant> getCompletionVariants(PsiNameValuePair annotationParameterNameValuePair, SelectorMethodValue selectorMethodValue) {
        PsiAnnotationMemberValue value = annotationParameterNameValuePair.getValue();
        String leftValue = getValueBeforeCaret(AnnotationsUtils.getAnnotationParameterValue(value));
        if (isAnnotationValueString(leftValue)) {
            System.out.println("left value : " + leftValue);
            ICompletionVariantsGenerator completionVariantsGenerator = null;
            if (selectorMethodValue != null) {
                switch (selectorMethodValue) {
                    case CSS:
                        completionVariantsGenerator = CSS_COMPLETION_VARIANTS_GENERATOR;
                        break;
                    case TAG_NAME:
                        completionVariantsGenerator = TAG_NAME_COMPLETION_VARIANTS_GENERATOR;
                        break;
                    case XPATH:
                        completionVariantsGenerator = XPATH_COMPLETION_VARIANTS_GENERATOR;
                        break;
                    default:
                        //do nothing
                        break;
                }
            }
            if (completionVariantsGenerator != null) {
                return completionVariantsGenerator.generateVariants(getValueBeforeCaret(AnnotationsUtils.getClearAnnotationParameterValue(value)));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    private boolean isAnnotationValueString(String value) {
        return value.startsWith("\"");
    }

    private String getValueBeforeCaret(String annotationParameterValue) {
        if (annotationParameterValue != null) {
            int dummyIdentifierIndex = annotationParameterValue.indexOf(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED);
            if (dummyIdentifierIndex > 0) {
                return annotationParameterValue.substring(0, dummyIdentifierIndex);
            }
        }
        return "";
    }

}
