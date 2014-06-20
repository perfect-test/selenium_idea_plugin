package com.unknown.seleniumplugin.utils;

import com.intellij.psi.PsiAnnotationMemberValue;

/**
 * Created by mike-sid on 17.06.14.
 */
public class AnnotationsUtils {

    /**
     * returns value without quotes
     * @param value annotation value
     * @return string value
     */
    public static String getClearAnnotationParameterValue(PsiAnnotationMemberValue value) {
        return getAnnotationParameterValue(value).replaceAll("\"", "");
    }


    /**
     * returns value of annotation
     * @param value annotation value
     * @return string value
     */
    public static String getAnnotationParameterValue(PsiAnnotationMemberValue value) {
        return value.getText();
    }
}
