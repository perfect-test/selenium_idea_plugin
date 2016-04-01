package com.unknown.seleniumplugin.settings.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.FileContentUtil;
import com.unknown.seleniumplugin.settings.SeleniumSettingsParams;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SeleniumSettingsDialog extends DialogWrapper {
    private JPanel contentPane;
    private JCheckBox enableSelectorCheckCheckBox;
    private JPanel methodsPanel;
    private JCheckBox cssCheckBox;
    private JCheckBox xpathCheckBox;
    private JCheckBox classNameCheckBox;
    private JCheckBox idCheckBox;
    private JCheckBox tagNameCheckBox;
    private PropertiesComponent properties;
    private Project project;

    public SeleniumSettingsDialog(Project project) {
        super(project);
        this.properties = PropertiesComponent.getInstance(project);
        this.project = project;
        setStates();
        init();
        setTitle("Selenium Settings Configuration");
        enableSelectorCheckCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (!enableSelectorCheckCheckBox.isSelected()) {
                    disableMethodChecks();
                } else {
                    enableChecks();
                }
                System.out.println("Checked? " + enableSelectorCheckCheckBox.isSelected());
            }
        });
    }

    private void enableChecks() {
        cssCheckBox.setEnabled(true);
        cssCheckBox.setSelected(false);
        properties.setValue(SeleniumSettingsParams.CSS_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
        xpathCheckBox.setEnabled(true);
        xpathCheckBox.setSelected(false);
        properties.setValue(SeleniumSettingsParams.XPATH_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
        classNameCheckBox.setEnabled(true);
        classNameCheckBox.setSelected(false);
        properties.setValue(SeleniumSettingsParams.CLASS_NAME_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
        idCheckBox.setEnabled(true);
        idCheckBox.setSelected(false);
        properties.setValue(SeleniumSettingsParams.ID_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
        tagNameCheckBox.setEnabled(true);
        tagNameCheckBox.setSelected(false);
        properties.setValue(SeleniumSettingsParams.TAG_NAME_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
    }

    private void disableMethodChecks() {
        cssCheckBox.setEnabled(false);
        cssCheckBox.setSelected(false);
        properties.setValue(SeleniumSettingsParams.CSS_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
        xpathCheckBox.setEnabled(false);
        xpathCheckBox.setSelected(false);
        properties.setValue(SeleniumSettingsParams.XPATH_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
        classNameCheckBox.setEnabled(false);
        classNameCheckBox.setSelected(false);
        properties.setValue(SeleniumSettingsParams.CLASS_NAME_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
        idCheckBox.setEnabled(false);
        idCheckBox.setSelected(false);
        properties.setValue(SeleniumSettingsParams.ID_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
        tagNameCheckBox.setEnabled(false);
        tagNameCheckBox.setSelected(false);
        properties.setValue(SeleniumSettingsParams.TAG_NAME_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
    }

    private void setStates() {
        if (properties != null) {
            setSelectorsCheckBoxesStates();
        } else {
            setDefaultValues();
        }
    }


    private void setDefaultValues() {
        enableSelectorCheckCheckBox.setSelected(false);
        cssCheckBox.setSelected(false);
        cssCheckBox.setEnabled(false);
        xpathCheckBox.setSelected(false);
        xpathCheckBox.setEnabled(false);
        classNameCheckBox.setSelected(false);
        classNameCheckBox.setEnabled(false);
        idCheckBox.setSelected(false);
        idCheckBox.setEnabled(false);
        tagNameCheckBox.setSelected(false);
        tagNameCheckBox.setEnabled(false);
    }

    private void setSelectorsCheckBoxesStates() {
        if (properties.isValueSet(SeleniumSettingsParams.IS_SELECTOR_CHECK_ENABLED)) {
            boolean isCheckLocatorsEnabled = properties.isTrueValue(SeleniumSettingsParams.IS_SELECTOR_CHECK_ENABLED);
            enableSelectorCheckCheckBox.setSelected(isCheckLocatorsEnabled);
            setSelectorMethodsCheckBoxesStates(isCheckLocatorsEnabled);
        } else {
            setDefaultValues();
        }
    }

    private void setSelectorMethodsCheckBoxesStates(boolean isCheckLocatorsEnabled) {
        if (!isCheckLocatorsEnabled) {
            disableMethodChecks();
        } else {
            if (properties.isValueSet(SeleniumSettingsParams.CSS_SELECTOR_CHECK_ENABLED)) {
                boolean isCheckEnabled = properties.isTrueValue(SeleniumSettingsParams.CSS_SELECTOR_CHECK_ENABLED);
                cssCheckBox.setSelected(isCheckEnabled);
            } else {
                cssCheckBox.setSelected(false);
            }
            if (properties.isValueSet(SeleniumSettingsParams.XPATH_SELECTOR_CHECK_ENABLED)) {
                boolean isCheckEnabled = properties.isTrueValue(SeleniumSettingsParams.XPATH_SELECTOR_CHECK_ENABLED);
                xpathCheckBox.setSelected(isCheckEnabled);
            } else {
                xpathCheckBox.setSelected(false);
            }
            if (properties.isValueSet(SeleniumSettingsParams.CLASS_NAME_SELECTOR_CHECK_ENABLED)) {
                boolean isCheckEnabled = properties.isTrueValue(SeleniumSettingsParams.CLASS_NAME_SELECTOR_CHECK_ENABLED);
                classNameCheckBox.setSelected(isCheckEnabled);
            } else {
                classNameCheckBox.setSelected(false);
            }
            if (properties.isValueSet(SeleniumSettingsParams.ID_SELECTOR_CHECK_ENABLED)) {
                boolean isCheckEnabled = properties.isTrueValue(SeleniumSettingsParams.ID_SELECTOR_CHECK_ENABLED);
                idCheckBox.setSelected(isCheckEnabled);
            } else {
                idCheckBox.setSelected(false);
            }
            if (properties.isValueSet(SeleniumSettingsParams.TAG_NAME_SELECTOR_CHECK_ENABLED)) {
                boolean isCheckEnabled = properties.isTrueValue(SeleniumSettingsParams.TAG_NAME_SELECTOR_CHECK_ENABLED);
                tagNameCheckBox.setSelected(isCheckEnabled);
            } else {
                tagNameCheckBox.setSelected(false);
            }
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        contentPane.setPreferredSize(new Dimension(300, 0));
        return contentPane;
    }


    protected void doOKAction() {
        if (properties != null) {
            if(enableSelectorCheckCheckBox.isSelected()) {
                properties.setValue(SeleniumSettingsParams.IS_SELECTOR_CHECK_ENABLED, Boolean.toString(true));
                properties.setValue(SeleniumSettingsParams.CSS_SELECTOR_CHECK_ENABLED, Boolean.toString(cssCheckBox.isSelected()));
                properties.setValue(SeleniumSettingsParams.XPATH_SELECTOR_CHECK_ENABLED, Boolean.toString(xpathCheckBox.isSelected()));
                properties.setValue(SeleniumSettingsParams.CLASS_NAME_SELECTOR_CHECK_ENABLED, Boolean.toString(classNameCheckBox.isSelected()));
                properties.setValue(SeleniumSettingsParams.ID_SELECTOR_CHECK_ENABLED, Boolean.toString(idCheckBox.isSelected()));
                properties.setValue(SeleniumSettingsParams.TAG_NAME_SELECTOR_CHECK_ENABLED, Boolean.toString(tagNameCheckBox.isSelected()));
            } else {
                properties.setValue(SeleniumSettingsParams.IS_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
                properties.setValue(SeleniumSettingsParams.CSS_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
                properties.setValue(SeleniumSettingsParams.XPATH_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
                properties.setValue(SeleniumSettingsParams.CLASS_NAME_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
                properties.setValue(SeleniumSettingsParams.ID_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
                properties.setValue(SeleniumSettingsParams.TAG_NAME_SELECTOR_CHECK_ENABLED, Boolean.toString(false));
            }
        }
        FileContentUtil.reparseOpenedFiles();
        super.doOKAction();
    }


    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

}
