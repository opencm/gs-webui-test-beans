package webui.tests;

import com.thoughtworks.selenium.Selenium;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.Arrays;

/**
 * User: guym
 * Date: 3/6/13
 * Time: 4:54 PM
 */
public class SeleniumDriverFactory{

    private static Logger logger = LoggerFactory.getLogger( SeleniumDriverFactory.class );
    private int retries;


    private String chromeDriverPath = "classpath:webui/chromedriver_win_26.0.1383.0/chromedriver.exe";

    private String gwtFirefoxDevXpi = "classpath:webui/gwt-dev-plugin-1-19-rc.xpi";

    private String gwtChromeDevCrx = "classpath:webui/chrome_gwt_1_0_11357.crx";

    public static enum DriverType {
        CHROME, FIREFOX, IE, FIREFOX_GWT_DEV, CHROME_GWT_DEV
    }

    private DriverType driverType = DriverType.CHROME;

    private WebDriver webDriver = null;

    private Selenium selenium = null;

    private String rootUrl = "http://localhost:8099";


    public void setDriverType( DriverType driverType ) {
        this.driverType = driverType;
    }

    public void setDriverTypeString( String str ) {
        driverType = DriverType.valueOf( str.toUpperCase() );
    }

    public void initializeDriver() {
        switch ( driverType )
        {
            case CHROME_GWT_DEV:
            {
                try
                {
                    ChromeOptions options = new ChromeOptions();
                    logger.info( "using chrome crx [{}] and chrome driver [{}]", gwtChromeDevCrx, chromeDriverPath );
                    options.addExtensions( getResourceLocation( gwtChromeDevCrx ) );
                    DesiredCapabilities desired = DesiredCapabilities.chrome();
                    desired.setCapability( "chrome.switches", Arrays.asList( "--start-maximized" ) );
                    desired.setCapability( ChromeOptions.CAPABILITY, options ); // add the gwt dev plugin

                    ChromeDriverService chromeService = new ChromeDriverService.Builder().usingAnyFreePort().usingDriverExecutable( getResourceLocation( chromeDriverPath ) ).build();
                    logger.info( "Starting Chrome Driver Server..." );
                    chromeService.start();
                    webDriver = new RemoteWebDriver( chromeService.getUrl(), desired );

                } catch ( Exception e )
                {
                    logger.warn( "unable to initialize chrome [{}]", e.getMessage() );
                }
                break;
            }
            case CHROME:
            {
                try
                {
                    DesiredCapabilities desired = DesiredCapabilities.chrome();
                    desired.setCapability( "chrome.switches", Arrays.asList( "--start-maximized" ) );
                    ChromeDriverService chromeService = new ChromeDriverService.Builder().usingAnyFreePort().usingDriverExecutable( getResourceLocation( chromeDriverPath ) ).build();
                    logger.info( "Starting Chrome Driver Server..." );
                    chromeService.start();
                    webDriver = new RemoteWebDriver( chromeService.getUrl(), desired );

                } catch ( Exception e )
                {
                    logger.warn( "unable to initialize chrome [{}]", e.getMessage() );
                }
                break;
            }
            case FIREFOX_GWT_DEV:
            {
                try
                {
                    FirefoxProfile profile = new FirefoxProfile();
                    profile.addExtension( getResourceLocation( gwtFirefoxDevXpi ) );
                    webDriver = new FirefoxDriver( profile );

                } catch ( Exception e )
                {
                    logger.warn( "unable to initialize FIREFOX_GWT_DEV [{}]", e.getMessage() );
                }
                break;
            }
            case FIREFOX:
            {
                webDriver = new FirefoxDriver();
                break;
            }
            case IE:
            {
                DesiredCapabilities desired = DesiredCapabilities.internetExplorer();
                desired.setCapability( InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true );
                webDriver = new InternetExplorerDriver( desired );
                break;
            }

        }
    }

    private File getResourceLocation( String uri ) {
        try
        {
            return ResourceUtils.getFile( uri ).getAbsoluteFile();
        } catch ( Exception e )
        {
            throw new RuntimeException( String.format( "unable to get resource %s", uri ), e );
        }
    }

    public void init() {
        retries = 3;
        for ( int i = 0; i < retries; i++ )
        {
            logger.info( "initializing driver [{}] try [{}]", driverType, i );
            try
            {
                initializeDriver();

            } catch ( RuntimeException e )
            {
                logger.warn( "initializeDriver throws runtime exceptions", e.getMessage() );
            }
            if ( webDriver != null )
            {
                logger.info( "successfully initialized driver" );
                break;
            }
        }
        if ( webDriver == null )
        {
            logger.error( "unable to launch browser, failing test" );
            throw new RuntimeException();
        }

        new WebDriverBackedSelenium( webDriver, rootUrl );
    }

    public WebDriver getDriver() {
        return webDriver;
    }

    public void quit(){
        webDriver.quit();
        selenium.close();
    }

    public Selenium getSelenium() {
        return selenium;
    }

    public void setRetries( int retries ) {
        this.retries = retries;
    }

    public void setRootUrl( String rootUrl ) {
        this.rootUrl = rootUrl;
    }

    public String getRootUrl(){
        return rootUrl;
    }


}
