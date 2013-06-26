package webui.tests;

import org.junit.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import webui.tests.pages.*;
import webui.tests.utils.CollectionUtils;

/**
 * User: eliranm
 * Date: 6/25/13
 * Time: 12:26 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:example-context.xml"})
public class RecipesRepositoryTest {

    private static Logger logger = LoggerFactory.getLogger( RecipesRepositoryTest.class );

    @Autowired
    private RecipesRepositoryTestConf conf;

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
    public void installApplicationTest() {
        logger.info( "install appclication test" );
        RecipesRepositoryPage recipesRepositoryPage = loginPage.gotoPage().login().gotoRecipes().applications().install("helloworld");
        Assert.assertTrue( String.format(
                "expecting to see [%s] in popup with text [%s]", conf.progressDialogText, CollectionUtils.first(recipesRepositoryPage.findDisplayedWindowDialogs()).getText() ),
                recipesRepositoryPage.isTextInPopup(conf.progressDialogText));
    }

    @Test
    public void applicationInstallationProgressTest(){
        logger.info( "application installation progress test" );
        loginPage.gotoPage().login().gotoRecipes().applications().install("helloworld").closeDialog("yes");
        ApplicationsPage.InstallationProgressTab installationProgressTab = applicationsPage.load().installationProgress();
        Assert.assertTrue(
                String.format("expecting recipe path [%s] to contain [%s]", installationProgressTab.recipePath.getText().toLowerCase(), "helloworld"),
                installationProgressTab.recipePath.getText().toLowerCase().contains("helloworld"));
        Assert.assertTrue(
                String.format("expecting console text [%s] to contain [%s]", installationProgressTab.console.getText(), conf.progressConsoleText),
                installationProgressTab.console.getText().contains(conf.progressConsoleText));
    }

    public static class RecipesRepositoryTestConf {
        public String progressDialogText;
        public String progressConsoleText;

        public void setProgressDialogText(String progressDialogText) {
            this.progressDialogText = progressDialogText;
        }
        public void setProgressConsoleText(String progressConsoleText) {
            this.progressConsoleText = progressConsoleText;
        }
    }

}
