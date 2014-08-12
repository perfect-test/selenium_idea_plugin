package com.unknown.seleniumplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.unknown.seleniumplugin.generatefield.GenerateDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike-sid on 09.04.14.
 */

public class CodeExamples extends AnAction {
    public static final String COM_GOOGLE_COMMON_COLLECT_COMPARISON_CHAIN = "com.google.common.collect.ComparisonChain";
    private PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

        Editor editor = e.getData(LangDataKeys.EDITOR);
        if(psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }
    // If you register the action from Java code, this constructor is used to set the menu item name
    // (optionally, you can specify the menu description and an icon to display next to the menu item).
    // You can omit this constructor when registering the action in the plugin.xml file.
    public CodeExamples() {
        // Set the menu item name.
        super("Text _Boxes");
        // Set the menu item name, description and icon.
        // super("Text _Boxes","Item description",IconLoader.getIcon("/Mypackage/icon.png"));
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        String txt= Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
        Messages.showMessageDialog(project, "Hello, " + txt + "!\n I am glad to see you.", "Information", Messages.getInformationIcon());
    }


    public void actionPerformed2(AnActionEvent e) {
        StringBuilder sourceRootsList = new StringBuilder();
        Project project = e.getProject();
        ModuleManager moduleManager = ModuleManager.getInstance(project);
        Module[] modules = moduleManager.getModules();
        for(Module module : modules) {
            final List<String> libraryNames = new ArrayList<String>();
            ModuleRootManager.getInstance(module).orderEntries().forEachLibrary(new Processor<Library>() {
                @Override
                public boolean process(Library library) {
                    libraryNames.add(library.getName());
                    return true;
                }
            });
            sourceRootsList.append(libraryNames).append("\n");

//            VirtualFile[] roots = ModuleRootManager.getInstance(module).orderEntries().classes().getRoots();
//            for (VirtualFile file : roots) {
//                sourceRootsList.append(file.getUrl()).append("\n");
//            }
        }
        System.out.println(sourceRootsList);
//        Messages.showInfoMessage("Source roots for the " + project.getName() + " plugin:\n" + sourceRootsList, "Project Properties");


        PsiClass psiClass = getPsiClassFromContext(e);
        GenerateDialog generateDialog = new GenerateDialog(psiClass);
        generateDialog.show();
//        if(generateDialog.isOK()) {
//            generateComparable(psiClass, generateDialog.getFields());
//        }
        // TODO: insert action logic here
    }

    public void generateComparable(final PsiClass psiClass, final List<PsiField> fields) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {

            @Override
            protected void run() throws Throwable {
                generateCompareTo(psiClass, fields);
//                generateImplementsComparable(psiClass);


            }

        }.execute();
    }

    private void generateCompareTo(PsiClass psiClass, List<PsiField> fields) {
        StringBuilder builder = new StringBuilder("public int compareTo(");
        builder.append(psiClass.getName()).append(" that) {\n");
        builder.append("return " + COM_GOOGLE_COMMON_COLLECT_COMPARISON_CHAIN + ".start()");
        for (PsiField field : fields) {
            builder.append(".compare(this.").append(field.getName()).append(", that.");
            builder.append(field.getName()).append(")");
        }
        builder.append(".result();\n}");
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        PsiMethod compareTo = elementFactory.createMethodFromText(builder.toString(), psiClass);
        PsiElement method = psiClass.add(compareTo);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(method);

    }


    public void update(AnActionEvent e) {
        PsiClass pciClass = getPsiClassFromContext(e);
        e.getPresentation().setEnabled(pciClass != null);
    }


}