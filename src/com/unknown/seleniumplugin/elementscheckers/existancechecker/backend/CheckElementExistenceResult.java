package com.unknown.seleniumplugin.elementscheckers.existancechecker.backend;

/**
 * Created by mike-sid on 12.08.14.
 */
public class CheckElementExistenceResult {

    private boolean isFound;
    private String error;
    private int elementsCount;

    public CheckElementExistenceResult() {
    }

    public CheckElementExistenceResult(String error, boolean isFound) {
        this.error = error;
        this.isFound = isFound;
    }

    public boolean isFound() {
        return isFound;
    }

    public void setFound(boolean isFound) {
        this.isFound = isFound;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setElementsCount(int elementsCount) {
        this.elementsCount = elementsCount;
    }

    public int getElementsCount() {
        return elementsCount;
    }
}
