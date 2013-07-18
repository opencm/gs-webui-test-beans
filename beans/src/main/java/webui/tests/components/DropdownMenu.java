package webui.tests.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import webui.tests.utils.CollectionUtils;

import java.util.List;

/**
 * User: eliranm
 * Date: 7/17/13
 * Time: 4:08 PM
 */
public class DropdownMenu extends AbstractComponent<DropdownMenu> {

    public void select(final String text) {
        WebElement item = CollectionUtils.find(getMenuItems(), new CollectionUtils.Predicate<WebElement>() {
            @Override
            public boolean apply(WebElement webElement) {
                return webElement.getText().equals(text);
            }
        });
        item.click();
    }

    public void selectById(final String id) {
        WebElement item = CollectionUtils.find(getMenuItems(), new CollectionUtils.Predicate<WebElement>() {
            @Override
            public boolean apply(WebElement webElement) {
                return webElement.getAttribute("id").equals(id);
            }
        });
        item.click();
    }

    private List<WebElement> getMenuItems() {
        return webDriver.findElements(By.className("x-menu-item"));
    }

}
