package com.unknown.seleniumplugin.settings.ui;

import com.intellij.codeInsight.daemon.impl.DefaultHighlightVisitor;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileSystemEntry;
import com.intellij.psi.PsiBundle;
import com.intellij.psi.PsiClass;
import com.intellij.psi.impl.file.impl.FileManagerImpl;
import com.intellij.psi.search.ProjectScopeBuilder;
import com.intellij.util.FileContentUtil;
import com.intellij.util.FileContentUtilCore;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.PathsList;
import com.unknown.seleniumplugin.settings.SeleniumSettingsParams;
import com.unknown.seleniumplugin.utils.ProjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class SeleniumSettingsDialog extends DialogWrapper {
    private JPanel contentPane;
    private JCheckBox enableSelectorCheckCheckBox;
    private PropertiesComponent properties;
    private Project project;

    public SeleniumSettingsDialog(Project project) {
        super(project);
        this.properties = PropertiesComponent.getInstance(project);
        this.project = project;
        setStates();
        init();
        setTitle("Selenium Settings Configuration");
//        setContentPane(contentPane);
//        setModal(true);
//        getRootPane().setDefaultButton(buttonOK);
//
//        buttonOK.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onOK();
//            }
//        });
//
//        buttonCancel.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        });
//
//// call onCancel() when cross is clicked
//        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                onCancel();
//            }
//        });
//
//// call onCancel() on ESCAPE
//        contentPane.registerKeyboardAction(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void setStates() {
        if (properties != null) {
            setEnableCheckSelectorCheckbox();
        } else {
            setDefaultValues();
        }
    }

    private void setDefaultValues() {
        enableSelectorCheckCheckBox.setSelected(true);
    }

    private void setEnableCheckSelectorCheckbox() {
        if (properties.isValueSet(SeleniumSettingsParams.IS_SELECTOR_CHECK_ENABLED)) {
            boolean isSelectedValue = properties.isTrueValue(SeleniumSettingsParams.IS_SELECTOR_CHECK_ENABLED);
            enableSelectorCheckCheckBox.setSelected(isSelectedValue);
        } else {
            setDefaultValues();
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
            properties.setValue(SeleniumSettingsParams.IS_SELECTOR_CHECK_ENABLED, Boolean.toString(enableSelectorCheckCheckBox.isSelected()));
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
