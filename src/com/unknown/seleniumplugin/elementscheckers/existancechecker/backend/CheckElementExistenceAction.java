package com.unknown.seleniumplugin.elementscheckers.existancechecker.backend;

import com.intellij.CommonBundle;
import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.hint.HintManagerImpl;
import com.intellij.find.FindBundle;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiNameValuePairImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.unknown.seleniumplugin.domain.SelectorMethodValue;
import com.unknown.seleniumplugin.elementscheckers.existancechecker.ExistenceCheckUtils;
import com.unknown.seleniumplugin.elementscheckers.existancechecker.ui.CheckElementExistenceDialog;
import com.unknown.seleniumplugin.generatefield.backend.FieldsGenerationTemplates;
import com.unknown.seleniumplugin.pluginproperties.GlobalPluginProperties;
import com.unknown.seleniumplugin.utils.PsiCommonUtils;

import java.util.Collection;

/**
 * Created by mike-sid on 05.08.14.
 */
public class CheckElementExistenceAction extends AnAction {
    private static final String ERROR_MESSAGE = "Check couldn't be done on not @FindBy . Move cursor to annotation and try again";

    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(LangDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        Document document = editor.getDocument();
        if (!document.isWritable()) {
            return;
        }
        ExistenceCheckUtils.showCheckExistenceDialog(e.getProject(), editor, document, PsiCommonUtils.getPsiClassFromContext(e));
    }

}
