package webui.tests.selenium;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * User: guym
 * Date: 5/23/13
 * Time: 5:51 PM
 */
public class GsHtmlUnitDriver extends HtmlUnitDriver{
    public GsHtmlUnitDriver() {
        super( BrowserVersion.CHROME_16 );
        setJavascriptEnabled( true );
        WebClient webClient = getWebClient();
        webClient.getOptions(  ).setThrowExceptionOnScriptError( false );
    }


}
