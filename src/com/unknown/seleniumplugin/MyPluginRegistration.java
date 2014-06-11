package com.unknown.seleniumplugin;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mike-sid on 16.04.14.
 */
public class MyPluginRegistration implements ApplicationComponent {
    @Override
    public void initComponent() {
        ActionManager actionManager = ActionManager.getInstance();
        TextBoxes action = new TextBoxes();
        actionManager.registerAction("MyPluginSidelnikovAction", action);
        DefaultActionGroup mainWindow = (DefaultActionGroup) actionManager.getAction("WindowMenu");
        mainWindow.addSeparator();
        mainWindow.add(action);
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "MyPluginSidelnikov";
    }
}
