package com.unknown.seleniumplugin.domain;

/**
 * Created by mike-sid on 17.06.14.
 */
public enum SelectorValue {
    CSS("css"),
    XPATH("xpath");
    private final String selectorMethod;

    SelectorValue(String selectorMethod) {
        this.selectorMethod = selectorMethod;
    }

    public String getSelectorMethod() {
        return selectorMethod;
    }

    public static SelectorValue getByText(String text) {
        for(SelectorValue selectorValue : values()){
            if(selectorValue.getSelectorMethod().equalsIgnoreCase(text)){
                return selectorValue;
            }
        }
        return null;
    }
}
