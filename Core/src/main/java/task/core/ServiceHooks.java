package task.core;

import cucumber.api.java.Before;

public class ServiceHooks {
    private static boolean isFirstStep = true;

    @Before
    public void initializeTest() {
        if (isFirstStep) {
            Runtime.getRuntime().addShutdownHook(new Thread(this::afterAll));
            isFirstStep = false;
            AppiumController.instance.getDriver().launchApp();
        }
    }

    private void afterAll() {
        AppiumController.instance.stop();
    }
}
