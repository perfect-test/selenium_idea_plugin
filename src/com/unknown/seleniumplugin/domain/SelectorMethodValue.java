package com.unknown.seleniumplugin.domain;

/**
 * Created by mike-sid on 17.06.14.
 */
public enum SelectorMethodValue {
    CSS("css"),
    XPATH("xpath"),
    CLASS_NAME("className"),
    ID("id"),
    LINK_TEXT("linkText"),
    NAME("name"),
    TAG_NAME("tagName");
    private final String selectorMethod;

    SelectorMethodValue(String selectorMethod) {
        this.selectorMethod = selectorMethod;
    }

    public String getSelectorMethod() {
        return selectorMethod;
    }

    public static SelectorMethodValue getByText(String text) {
        for(SelectorMethodValue selectorMethodValue : values()){
            if(selectorMethodValue.getSelectorMethod().equalsIgnoreCase(text)){
                return selectorMethodValue;
            }
        }
        return null;
    }
}
