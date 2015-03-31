package com.unknown.seleniumplugin.checkers.annotator;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.unknown.seleniumplugin.checkers.quickfix.SelectorVariantsQuickFix;
import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.classnamechecker.ClassNameSelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.css.CssSelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.id.IDSelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.tagname.TagNameSelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.xpath.XpathSelectorChecker;
import com.unknown.seleniumplugin.domain.SelectorMethodValue;
import com.unknown.seleniumplugin.elementscheckers.existancechecker.quickfix.CheckExistenceDialogQuickFix;
import com.unknown.seleniumplugin.settings.SeleniumSettingsParams;
import com.unknown.seleniumplugin.utils.AnnotationChecker;
import com.unknown.seleniumplugin.utils.AnnotationsUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mike-sid on 30.04.14.
 */
public class SeleniumSelectorAnnotator implements Annotator {
    private static final ISelectorChecker CSS_SELECTOR_CHECKER = new CssSelectorChecker();
    private static final ISelectorChecker ID_SELECTOR_CHECKER = new IDSelectorChecker();
    private static final ISelectorChecker TAG_NAME_SELECTOR_CHECKER = new TagNameSelectorChecker();
    private static final ISelectorChecker CLASS_NAME_SELECTOR_CHECKER = new ClassNameSelectorChecker();
    private static final ISelectorChecker XPATH_SELECTOR_CHECKER = new XpathSelectorChecker();
    private ISelectorChecker selectorChecker;


    @Override()
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof PsiAnnotation) {
            checkAnnotation(element, holder);
        } else if (element instanceof PsiMethodCallExpression) {
            if (isByReference(element)) {
                checkByExpression(element, holder);
            }

        }
    }


    private boolean isByReference(PsiElement element) {
        return element.getText().startsWith("By");
    }

    private void checkByExpression(PsiElement element, AnnotationHolder holder) {
        PsiMethodCallExpression callExpression = (PsiMethodCallExpression) element;
        PropertiesComponent properties = PropertiesComponent.getInstance(element.getProject());
        if (properties.isValueSet(SeleniumSettingsParams.IS_SELECTOR_CHECK_ENABLED)) {
            boolean isCheckEnabled = properties.isTrueValue(SeleniumSettingsParams.IS_SELECTOR_CHECK_ENABLED);
            if (isCheckEnabled) {
                PsiElement selectorValueElement = getSelectorValueFromExpression(callExpression);
                if(selectorValueElement instanceof PsiPolyadicExpression) {
                    TextRange range = new TextRange(selectorValueElement.getTextRange().getStartOffset(),
                            selectorValueElement.getTextRange().getEndOffset());
                    holder.createWarningAnnotation(range, "Check of dynamic locator's not supported");
                } else {
                    SelectorMethodValue findMethod = getFindMethodFromExpressionText(callExpression.getText());
                    if (findMethod != null && selectorValueElement != null) {
                        setSelectorChecker(findMethod);
                        if (selectorChecker != null && isMethodCheckEnabled(findMethod, properties)) {
                            String selectorValue = replaceUnnecessarySymbols(selectorValueElement.getText());
                            System.out.println("Expression value : " + findMethod + " : " + selectorValue);
                            checkError(selectorValue, selectorValueElement, holder);
                        }
                    }
                }
            }
        }
    }

    private String replaceUnnecessarySymbols(String selectorValue) {
        return selectorValue.replaceAll("\"", "");
    }

    private PsiElement getSelectorValueFromExpression(PsiElement psiElement) {
        PsiElement[] children = psiElement.getChildren();
        for (PsiElement child : children) {
            if (child instanceof PsiLiteralExpression || child instanceof PsiPolyadicExpression) {
                return child;
            } else {
                PsiElement subChildValue = getSelectorValueFromExpression(child);
                if (subChildValue != null) {
                    return subChildValue;
                }
            }
        }
        return null;
    }

    private SelectorMethodValue getFindMethodFromExpressionText(String text) {
        for (SelectorMethodValue selectorMethodValue : SelectorMethodValue.values()) {
            if (text.contains(selectorMethodValue.getSelectorMethod())) {
                return selectorMethodValue;
            }
        }
        return null;
    }

    private void checkAnnotation(PsiElement element, AnnotationHolder holder) {
        PsiAnnotation annotation = (PsiAnnotation) element;
        PsiJavaCodeReferenceElement referenceElement = annotation.getNameReferenceElement();
        if (referenceElement != null) {
            PropertiesComponent properties = PropertiesComponent.getInstance(element.getProject());
            if (properties.isValueSet(SeleniumSettingsParams.IS_SELECTOR_CHECK_ENABLED)) {
                boolean isCheckEnabled = properties.isTrueValue(SeleniumSettingsParams.IS_SELECTOR_CHECK_ENABLED);
                if (isCheckEnabled) {
                    if (AnnotationChecker.isFindByAnnotation(referenceElement.getQualifiedName()) ||
                            AnnotationChecker.isFindBysAnnotation(referenceElement.getQualifiedName())) {
                        PsiNameValuePair[] nameValuePairs = annotation.getParameterList().getAttributes();
                        if (nameValuePairs.length > 0) {
                            for (PsiNameValuePair nameValuePair : nameValuePairs) {
                                String name = nameValuePair.getName();
                                SelectorMethodValue selectorMethodValue = null;
                                if (name != null) {
                                    selectorMethodValue = SelectorMethodValue.getByText(name);
                                    if (selectorMethodValue != null) {
                                        setSelectorChecker(selectorMethodValue);
                                    }
                                }
                                if (selectorChecker != null && isMethodCheckEnabled(selectorMethodValue, properties)) {
                                    PsiAnnotationMemberValue nameValuePairValue = nameValuePair.getValue();
                                    if (nameValuePairValue != null && nameValuePairValue instanceof PsiLiteralExpression) {
                                        String value = AnnotationsUtils.getClearAnnotationParameterValue(nameValuePairValue);
                                        System.out.println("Значение : '" + value + "'");
                                        if (value != null) {
                                            checkError(value, nameValuePairValue, holder);
                                        }
                                    } else if(nameValuePairValue instanceof PsiPolyadicExpression) {
                                        TextRange range = new TextRange(nameValuePairValue.getTextRange().getStartOffset(),
                                                nameValuePairValue.getTextRange().getEndOffset());
                                        holder.createWarningAnnotation(range, "Check of dynamic locator's not supported");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isMethodCheckEnabled(SelectorMethodValue selectorMethodValue, PropertiesComponent properties) {
        switch (selectorMethodValue) {
            case CSS:
                return properties.isTrueValue(SeleniumSettingsParams.CSS_SELECTOR_CHECK_ENABLED);
            case XPATH:
                return properties.isTrueValue(SeleniumSettingsParams.XPATH_SELECTOR_CHECK_ENABLED);
            case CLASS_NAME:
                return properties.isTrueValue(SeleniumSettingsParams.CLASS_NAME_SELECTOR_CHECK_ENABLED);
            case ID:
                return properties.isTrueValue(SeleniumSettingsParams.ID_SELECTOR_CHECK_ENABLED);
            case TAG_NAME:
                return properties.isTrueValue(SeleniumSettingsParams.TAG_NAME_SELECTOR_CHECK_ENABLED);
        }
        return false;
    }

    private void checkError(String value, PsiElement element, AnnotationHolder holder) {
        if (selectorChecker == null) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(),
                    element.getTextRange().getEndOffset());
            holder.createWarningAnnotation(range, "This type of locator not supported");
        } else {
            try {
                CheckResult checkResult = selectorChecker.checkSelectorValid(value);
                if (!checkResult.isResultSuccess()) {
                    int startOffset = element.getTextRange().getStartOffset() + checkResult.getPosition();
                    int endOffset = startOffset + 2;
                    TextRange range = new TextRange(startOffset, endOffset);
                    Annotation annotation = holder.createErrorAnnotation(range, checkResult.getMessage());
                    annotation.registerFix(new CheckExistenceDialogQuickFix(selectorChecker, value, element));
                    annotation.registerFix(new SelectorVariantsQuickFix(selectorChecker, value, element));
                }
            } catch (NotParsebleSelectorException e) {
                TextRange range = new TextRange(element.getTextRange().getStartOffset(),
                        element.getTextRange().getEndOffset());
                holder.createWarningAnnotation(range, "Not parseble selector");
            } finally {
                selectorChecker = null;
            }
        }
    }


    private void setSelectorChecker(SelectorMethodValue methodValue) {
        switch (methodValue) {
            case CSS:
                selectorChecker = CSS_SELECTOR_CHECKER;
                break;
            case ID:
                selectorChecker = ID_SELECTOR_CHECKER;
                break;
            case TAG_NAME:
                selectorChecker = TAG_NAME_SELECTOR_CHECKER;
                break;
            case CLASS_NAME:
                selectorChecker = CLASS_NAME_SELECTOR_CHECKER;
                break;
            case XPATH:
                selectorChecker = XPATH_SELECTOR_CHECKER;
                break;
            default:
                //do nothing
                break;
        }
    }

}
