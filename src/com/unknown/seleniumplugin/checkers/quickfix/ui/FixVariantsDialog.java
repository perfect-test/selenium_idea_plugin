package com.unknown.seleniumplugin.checkers.quickfix.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FixVariantsDialog extends DialogWrapper {
    private JPanel contentPane;
    private JTextField locatorValueTextField;
    private JTextArea fixVariantsTextArea;
    private JLabel locatorStatusTextField;
    private String newSelectorValue;
    private ISelectorChecker selectorChecker;
    private String startLocator;

    public FixVariantsDialog(Project project, ISelectorChecker selectorChecker, String locator) {
        super(project);
        this.selectorChecker = selectorChecker;
        this.startLocator = locator;
        setStates();
        init();
        setTitle("Fix locator popup");
        locatorValueTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                checkLocator(getCurrentLocatorValue());
            }
        });

    }

    private void setStates() {
        locatorValueTextField.setText(startLocator);
        checkLocator(startLocator);
    }

    private void checkLocator(String locatorValue) {
        try {
            locatorStatusTextField.setText("");
            fixVariantsTextArea.setText("");
            CheckResult checkResult = selectorChecker.checkSelectorValid(locatorValue);
            if(!checkResult.isResultSuccess()) {
                setLocatorError(checkResult.getMessage());
                setFixVariant(checkResult.getFixVariant());
            } else {
                setLocatorGood();
            }
        } catch (NotParsebleSelectorException e) {
            setLocatorWarning("Selector can't be parsed");
        }

    }

    private void setLocatorWarning(String message) {
        locatorStatusTextField.setText(message);
        locatorStatusTextField.setForeground(JBColor.ORANGE);

    }

    private void setLocatorGood() {
        locatorStatusTextField.setText("Locator is correct");
        locatorStatusTextField.setForeground(JBColor.GREEN);
    }

    private void setFixVariant(String fixVariant) {
        fixVariantsTextArea.setText(fixVariant);
    }

    private void setLocatorError(String message) {
        locatorStatusTextField.setText(message);
        locatorStatusTextField.setForeground(JBColor.RED);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        contentPane.setPreferredSize(new Dimension(700,0));
        return contentPane;
    }

    private String getCurrentLocatorValue(){
        return locatorValueTextField.getText();
    }

    protected void doOKAction() {
        newSelectorValue = getCurrentLocatorValue();
        super.doOKAction();
    }



    public String geNewSelectorValue() {
        return newSelectorValue;
    }
}
