package com.unknown.seleniumplugin.codecomplete;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.unknown.seleniumplugin.codecomplete.generators.ICompletionVariantsGenerator;
import com.unknown.seleniumplugin.codecomplete.generators.impl.CssCompletionVariantsGenerator;
import com.unknown.seleniumplugin.codecomplete.inserthandlers.ISeleniumInsertHandler;
import com.unknown.seleniumplugin.codecomplete.inserthandlers.impl.CssInsertHandler;
import com.unknown.seleniumplugin.domain.SelectorMethodValue;
import com.unknown.seleniumplugin.domain.SeleniumCompletionVariant;
import com.unknown.seleniumplugin.utils.AnnotationChecker;
import com.unknown.seleniumplugin.utils.AnnotationsUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * Created by mike-sid on 16.06.14.
 */
public class SelectorCompletionContributor extends CompletionContributor {
    private static final ISeleniumInsertHandler CSS_INSERT_HANDLER = new CssInsertHandler();
    private static final ICompletionVariantsGenerator CSS_COMPLETION_VARIANTS_GENERATOR = new CssCompletionVariantsGenerator();

    public SelectorCompletionContributor() {
        extend(CompletionType.BASIC, psiElement(), new CompletionProvider<CompletionParameters>() {
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                PsiNameValuePair pair = PsiTreeUtil.getParentOfType(parameters.getPosition().getParent(), PsiNameValuePair.class);
                if (null == pair) {
                    System.out.println("pair is null");
                } else {
                    PsiAnnotation annotation = PsiTreeUtil.getParentOfType(pair, PsiAnnotation.class);
                    if (annotation == null) {
                        System.out.println("annotation is null");
                    } else {
                        System.out.println("annotation name : " + annotation.getQualifiedName());

                        if (AnnotationChecker.isFindByAnnotation(annotation.getQualifiedName())) {
                            SelectorMethodValue selectorMethodValue = SelectorMethodValue.getByText(pair.getName());
                            List<SeleniumCompletionVariant> completionVariants = getCompletionVariants(pair, selectorMethodValue);
                            if(completionVariants != null) {
                                addVariantsToResult(completionVariants, result, selectorMethodValue);
                            }
                        }
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
                default:
                    //do nothing
                    break;
            }
        }
        for(SeleniumCompletionVariant variant : completionVariants) {
            if(insertHandler != null) {
                result.addElement(LookupElementBuilder.create(variant, variant.getVariantString()).bold().withInsertHandler(insertHandler));
            } else {
                result.addElement(LookupElementBuilder.create(variant, variant.getVariantString()).bold());
            }
        }
    }

    private List<SeleniumCompletionVariant> getCompletionVariants(PsiNameValuePair annotationParameterNameValuePair, SelectorMethodValue selectorMethodValue) {
        PsiAnnotationMemberValue value = annotationParameterNameValuePair.getValue();
        String leftValue = getValueBeforeCaret(AnnotationsUtils.getAnnotationParameterValue(value));
        if(isAnnotationValueString(leftValue)) {
            System.out.println("left value : " + leftValue);
            ICompletionVariantsGenerator completionVariantsGenerator = null;
            if (selectorMethodValue != null) {
                switch (selectorMethodValue) {
                    case CSS:
                        completionVariantsGenerator = CSS_COMPLETION_VARIANTS_GENERATOR;
                        break;
                    default:
                        //do nothing
                        break;
                }
            }
            if(completionVariantsGenerator != null) {
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
        if(annotationParameterValue != null) {
            int dummyIdentifierIndex = annotationParameterValue.indexOf(CompletionUtilCore.DUMMY_IDENTIFIER);
            if(dummyIdentifierIndex > 0) {
                return annotationParameterValue.substring(0, dummyIdentifierIndex);
            }
        }
        return "";
    }

}
