package com.unknown.seleniumplugin.generatefield.ui;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.unknown.seleniumplugin.domain.SelectorMethodValue;
import com.unknown.seleniumplugin.generatefield.backend.FieldsGenerationTemplates;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GenerateWebElementDialog extends DialogWrapper {
    private JPanel contentPane;
    private JRadioButton singleFieldCheckBox;
    private JRadioButton listRadioButton;
    private JTextField fieldNameElement;
    private JComboBox methodSelectElement;
    private JTextArea errorTextArea;
    private StringBuilder error = new StringBuilder();

    private String selectedLocatorMethod;
    private String fieldName;
    private boolean isSingleSelected;
    private boolean isListSelected;


    public GenerateWebElementDialog(Project project) {
        super(project);
        setStates();
        init();
        setTitle("Generate your web element(s) field");
    }

    private void setStates() {
        setCheckBoxStated();
        setLocatorMethods();
    }

    private void setLocatorMethods() {
//        methodSelectElement.addItem("");
        for (SelectorMethodValue value : SelectorMethodValue.values()) {
            methodSelectElement.addItem(value.getSelectorMethod());
        }
    }

    private void setCheckBoxStated() {
        singleFieldCheckBox.setSelected(true);
        listRadioButton.setSelected(false);
        ButtonGroup group = new ButtonGroup();
        group.add(singleFieldCheckBox);
        group.add(listRadioButton);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    protected void doOKAction() {
        error.setLength(0);
        fieldName = fieldNameElement.getText();
        if (fieldName.isEmpty()) {
            error.append("Field name must be set");
        } else {
            if (!isLegalJavaIdentifier(fieldName)) {
                error.append("Field name is not legal java identifier");
            }
        }
        Object selectedItem = methodSelectElement.getSelectedItem();
        if (selectedItem != null && selectedItem instanceof String) {
            selectedLocatorMethod = (String) selectedItem;
            if (selectedLocatorMethod.isEmpty()) {
                if (error.length() > 0) {
                    error.append("\n");
                }
                error.append("Locator method must be set");
            }
        }
        isSingleSelected = singleFieldCheckBox.isSelected();
        isListSelected = listRadioButton.isSelected();
        if (error.length() > 0) {
            logError();
        } else {
            super.doOKAction();
        }


    }

    private void logError() {
        errorTextArea.setText(error.toString());

    }

    private boolean isLegalJavaIdentifier(String value) {
        return true;
    }

    public String getGeneratedFieldText() {
        String fieldText;
        if(isListSelected) {
            fieldText = FieldsGenerationTemplates.LIST_FIELD_TEMPLATE;
        } else {
            fieldText = FieldsGenerationTemplates.SINGLE_FIELD_TEMPLATE;
        }
        return fieldText
                .replace(FieldsGenerationTemplates.FIELD_NAME_PARAM_NAME, fieldName)
                .replace(FieldsGenerationTemplates.LOCATOR_METHOD_PARAM_NAME, selectedLocatorMethod);
    }
}
