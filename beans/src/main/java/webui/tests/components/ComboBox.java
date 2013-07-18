package webui.tests.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import webui.tests.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: eliranm
 * Date: 7/14/13
 * Time: 5:49 PM
 */
public class ComboBox extends AbstractComponent<ComboBox> {

    @FindBy(tagName = "input")
    private WebElement active;

    public String active() {
        return active.getAttribute("value");
    }

    public void select(String name) {
        webElement.findElement(By.cssSelector(".icon")).click();
        List<WebElement> items = getItems();
        for (WebElement item : items) {
            if (item.getText().equals(name)) {
                item.click();
                break;
            }
        }
    }

    public boolean has(final String name) {
        return CollectionUtils.find(getItems(), new CollectionUtils.Predicate<WebElement>() {
            @Override
            public boolean apply(WebElement el) {
                return el.getText().equals(name);
            }
        }) != null;
    }

    public List<String> items() {
        ArrayList<String> textItems = new ArrayList<String>();
        for (WebElement item : getItems()) {
            textItems.add(item.getText());
        }
        return textItems;
    }

    private List<WebElement> getItems() {
        // expand the list, there's no way to find the elements without them being visible
        webElement.findElement(By.cssSelector(".icon")).click();
        List<WebElement> items = webElement.findElements(By.cssSelector(".list-wrapper ul li"));
        waitFor(items);
        return items;
    }

}
