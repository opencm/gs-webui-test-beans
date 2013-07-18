package webui.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import webui.tests.pages.ApplicationsPage;
import webui.tests.pages.LoginPage;

import java.util.List;

/**
 * User: eliranm
 * Date: 6/25/13
 * Time: 12:26 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:example-context.xml"})
public class ApplicationsUninstallTest {

    public static final String APP_NAME = "helloworld";
    public static final String SERVICE_NAME = "tomcat";
    private static Logger logger = LoggerFactory.getLogger( ApplicationsUninstallTest.class );

    @Autowired
    private CloudifyTestBean cloudifyManager;

    @Autowired
    private LoginPage loginPage;

    @Autowired
    private ApplicationsPage applicationsPage;

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
    public void uninstallApplicationTest() {
        logger.info( "uninstall application test" );

        logger.info("uninstalling application [{}]...", APP_NAME);
        loginPage.gotoPage().login().gotoApplications().load().uninstallApplication(APP_NAME);

        List<String> installedApplications = applicationsPage.listApplications();
        logger.info("uninstalled [{}], installed applications are now [{}]", APP_NAME, installedApplications);

        Assert.assertTrue(String.format(
                "application [%s] should no longer appear in the applications combo box items [%s]",
                APP_NAME, installedApplications),
                !installedApplications.contains(APP_NAME));
    }

    @Test
    public void uninstallServiceTest() {
        logger.info( "uninstall service test" );

        logger.info("uninstalling service [{}]...", SERVICE_NAME);
        loginPage.gotoPage().login().gotoApplications().load()
                .uninstallDefaultService(SERVICE_NAME)
                .closeDialog("yes")
                .waitForServiceUninstall(SERVICE_NAME);

        List<String> installedServices = applicationsPage.listDefaultServices();
        logger.info("uninstalled [{}], installed services are now [{}]", SERVICE_NAME, installedServices);


        Assert.assertTrue(String.format(
                "service [%s] should no longer appear under the installed services [%s]",
                SERVICE_NAME, installedServices),
                !installedServices.contains(SERVICE_NAME));

    }

}
