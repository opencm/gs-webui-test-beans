package webui.tests;

import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import webui.tests.pages.DashboardPage;
import webui.tests.pages.ComplexLoginPage;
import webui.tests.pages.LoginPage;
import webui.tests.utils.CollectionUtils;

/**
 * User: guym
 * Date: 3/6/13
 * Time: 11:08 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:example-context.xml"})
public class LicenseTest {

    private static Logger logger = LoggerFactory.getLogger( LicenseTest.class );

    @Autowired
    private LoginPage loginPage;


    @Autowired
    private ComplexLoginPage complexLoginPage;

    @Autowired
    private CloudifyTestBean cloudifyManager;

    @Autowired
    private LicenseTestConf licenseTestConf;

    @Before
    public void before(){
        logger.info( "bootstrapping" );
        cloudifyManager.bootstrap();
    }

    @After
    public void after(){
        logger.info( "tearing down" );
        cloudifyManager.teardown();
    }

    @Test
    public void complexLicenseTest(){
        logger.info( "license test" );
        DashboardPage dashboard = complexLoginPage.gotoPage().login( licenseTestConf.username, licenseTestConf.password );
        dashboard.getAboutButton().click();
        Assert.assertTrue( String.format( "expecting to see %s in popup with text [%s]", licenseTestConf.aboutText, CollectionUtils.first( dashboard.findDisplayedWindowDialogs() ).getText() ), dashboard.isTextInPopup( licenseTestConf.aboutText ) );
        dashboard.closeDialog( "OK" ).clickLogout().closeDialog( "yes" );
        loginPage.load();
        Assert.assertTrue( "We are now in login page. We should see welcome message", loginPage.isLoginWelcomeMessageVisible() );
    }

    @Test
    public void licenseTest(){
        logger.info( "license test" );
        DashboardPage dashboard = loginPage.gotoPage().login( licenseTestConf.username, licenseTestConf.password );
        dashboard.getAboutButton().click();
        Assert.assertTrue( String.format( "expecting to see %s in popup with text [%s]", licenseTestConf.aboutText, CollectionUtils.first(dashboard.findDisplayedWindowDialogs()).getText() ), dashboard.isTextInPopup( licenseTestConf.aboutText ) );
        dashboard.closeDialog("OK").clickLogout().closeDialog( "yes" );
        loginPage.load();
       Assert.assertTrue( "We are now in login page. We should see welcome message",loginPage.isLoginWelcomeMessageVisible() );


    }

    public static class LicenseTestConf{
            public String username = null;
            public String password = null;
            public String aboutText;

        public void setUsername( String username ) {
            this.username = username;
        }

        public void setPassword( String password ) {
            this.password = password;
        }

        public void setAboutText( String aboutText ) {
            this.aboutText = aboutText;
        }
    }
}
