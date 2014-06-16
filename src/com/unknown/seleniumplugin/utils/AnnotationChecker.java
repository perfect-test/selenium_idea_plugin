package com.unknown.seleniumplugin.utils;

/**
 * Created by mike-sid on 11.06.14.
 */
public class AnnotationChecker {
    private static final String SELENIUM_FIND_BY_ANNOTATION_CLASS_NAME = "org.openqa.selenium.support.FindBy";

    public static boolean isFindByAnnotation(String annotationName) {
        return annotationName.equals(SELENIUM_FIND_BY_ANNOTATION_CLASS_NAME);
    }
}
