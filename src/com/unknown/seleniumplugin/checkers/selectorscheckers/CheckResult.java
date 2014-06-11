package com.unknown.seleniumplugin.checkers.selectorscheckers;

/**
 * Created by mike-sid on 30.04.14.
 * Class represents the result of check of selector
 * if result not success - isResultSuccess is false and message not empty
 */

public class CheckResult {
    private boolean isResultSuccess;
    private String message;
    private int position;


    public CheckResult(boolean isResultSuccess, String message) {
        this.isResultSuccess = isResultSuccess;
        this.message = message;
    }

    public boolean isResultSuccess() {
        return isResultSuccess;
    }

    public void setResultSuccess(boolean isResultSuccess) {
        this.isResultSuccess = isResultSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
