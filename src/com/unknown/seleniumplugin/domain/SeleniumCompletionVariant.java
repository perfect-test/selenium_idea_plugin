package com.unknown.seleniumplugin.domain;

/**
 * Created by mike-sid on 18.06.14.
 */
public class SeleniumCompletionVariant {
    private int caretOffset = 2;
    private String variantString;

    public SeleniumCompletionVariant(int caretOffset, String variantString) {
        this.caretOffset = caretOffset;
        this.variantString = variantString;
    }

    public SeleniumCompletionVariant(String variantString) {
        this.variantString = variantString;
    }

    public SeleniumCompletionVariant() {}

    public int getCaretOffset() {
        return caretOffset;
    }

    public void setCaretOffset(int caretOffset) {
        this.caretOffset = caretOffset;
    }

    public String getVariantString() {
        return variantString;
    }

    public void setVariantString(String variantString) {
        this.variantString = variantString;
    }

    public String toString(){
        return getVariantString();
    }
}
