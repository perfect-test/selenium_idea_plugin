package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by mike-sid on 11.06.14.
 */
public class PluginIcons {
    private static final Icon SELENIUM_LOGO = load("/icons/selenium_icon.png");

    private static Icon load(String path) {
        return IconLoader.getIcon(path, PluginIcons.class);
    }

    public static Icon getSeleniumLogo() {
        return SELENIUM_LOGO;
    }
}