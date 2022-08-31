package task.core;

import cucumber.api.testng.TestNGCucumberRunner;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CustomAbstractTestNGCucumberTests {
    private static int browserCount;
    private TestNGCucumberRunner testNGCucumberRunner;
    private int threadCount;

    public CustomAbstractTestNGCucumberTests() {
    }

    @BeforeClass(alwaysRun = true)
    public void setUpClass(ITestContext context) throws Exception {
        threadCount = context.getCurrentXmlTest().getSuite().getTests().get(0).getThreadCount();
        this.testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
    }

    @DataProvider
    public Object[][] scenarios() {
        ArrayList<Object> scenarios = new ArrayList<>(Arrays.asList(this.testNGCucumberRunner.provideScenarios()));
        int scenarioPerThread = scenarios.size() / threadCount;
        List<Object> runScenarios = (browserCount == threadCount - 1) ? scenarios.subList(browserCount * scenarioPerThread, scenarios.size())
                : scenarios.subList(browserCount * scenarioPerThread, browserCount * scenarioPerThread + scenarioPerThread);
        System.out.println("Thread " + browserCount + " run " + runScenarios.size() + " scenarios");
        browserCount++;
        return this.testNGCucumberRunner == null ? new Object[0][0] : runScenarios.toArray(new Object[0][]);
    }

    @AfterClass(
            alwaysRun = true
    )
    public void tearDownClass() throws Exception {
        if (this.testNGCucumberRunner != null) {
            this.testNGCucumberRunner.finish();
        }
    }
}
