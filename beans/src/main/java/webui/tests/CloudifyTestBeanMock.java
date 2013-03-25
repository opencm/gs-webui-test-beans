package webui.tests;

import webui.tests.cloudify.commands.CloudifyCliManager;

/**
 * User: guym
 * Date: 3/21/13
 * Time: 3:41 PM
 */
public class CloudifyTestBeanMock implements CloudifyTestBean{

    @Override
    public CloudifyCliManager.Execution bootstrap() {
        return null;
    }

    @Override
    public CloudifyCliManager.Execution teardown() {
        return null;
    }
}
