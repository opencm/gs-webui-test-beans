package webui.tests.components;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;

/**
 * User: eliranm
 * Date: 7/17/13
 * Time: 2:00 PM
 */

public class ApplicationMap extends AbstractComponent<ApplicationMap> {

    @Absolute
    @FirstDisplayed
    @FindBy(className = "gs-drop-down-menu")
    private DropdownMenu menu;

    public ApplicationMap uninstall(String name) {
        webElement.findElement(By.cssSelector("image[id*=\"node-tool-ACTIONS-\"][id*=\"" + name + "\"]")).click();
        menu.load().selectById("gs-menu-item-uninstall");
        return this;
    }

    public boolean has(String name) {
        try {
            return webElement.findElement(By.cssSelector("path[id^=\"node-shape-path-\"][id$=\"" + name + "\"]")) != null;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public List<String> names() {
        List<WebElement> nodePaths = webElement.findElements(By.cssSelector("path[id^=\"node-shape-path-\"]"));
        List<String> names = new ArrayList<String>(nodePaths.size());
        for (WebElement path : nodePaths) {
            names.add(path.getAttribute("id").replaceFirst("node-shape-path-.*\\.", ""));
        }
        return names;
    }
}
