package task.core;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ReportListener extends TestListenerAdapter {
    private static final int TIME_NANO_SECOND = 1000000;
    private File[] files;
    private JSONArray resultFeatures;
    private boolean isTestSuccess = false;

    private long totalFeatures = 0;
    private long featurePassed = 0;
    private long featureFailed = 0;

    private long totalScenarios = 0;
    private long scenarioPassed = 0;
    private long scenarioFailed = 0;

    private long totalSteps = 0;
    private long stepPassed = 0;
    private long stepFailed = 0;
    private long stepSkipped = 0;
    private long stepPending = 0;
    private long stepUndefined = 0;

    private long totalDuration = 0;
    private long passedDuration = 0;
    private long failedDuration = 0;
    private long skipDuration = 0;
    private long pendingDuration = 0;
    private long undefineDuration = 0;

    @Override
    public final void onFinish(final ITestContext testContext) {
        super.onFinish(testContext);
        try {
            mergeJsonFiles();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        isTestSuccess = true;
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        isTestSuccess = false;
    }

    private void mergeJsonFiles() throws IOException, ParseException {
        int size;
        files = getAllFiles();
        List<String> resultIds = new ArrayList<>();
        if (files.length > 0) {
            resultFeatures = getFeatures(new FileReader(files[0].getPath()));
            size = resultFeatures.size();
            for (int i = 0; i < size; i++) {
                resultIds.add((String) ((JSONObject) resultFeatures.get(i)).get("id"));
            }
        } else {
            return;
        }

        for (int pos = 1; pos < files.length; pos++) {
            JSONArray fileFeatures = getFeatures(new FileReader(files[pos].getPath()));
            if (resultFeatures.size() == 0) {
                resultFeatures = fileFeatures;
            }
            for (int i = 0; i < size; i++) {
                for (Object fileFeature : fileFeatures) {
                    JSONObject mergeObject = (JSONObject) resultFeatures.get(i);
                    JSONObject object = (JSONObject) fileFeature;
                    String idMergeFeature = (String) mergeObject.get("id");
                    String idFeature = (String) object.get("id");
                    if (idMergeFeature.equals(idFeature)) {
                        JSONArray mainElements = (JSONArray) mergeObject.get("elements");
                        JSONArray elements = (JSONArray) object.get("elements");
                        mainElements.addAll(elements);
                    } else if (!resultIds.contains(idFeature)) {
                        resultIds.add(idFeature);
                        resultFeatures.add(fileFeature);
                    }
                }
            }
        }
        Files.write(Paths.get(files[0].getPath()), resultFeatures.toJSONString().getBytes());
        System.out.println("Merge json reports success.");
        deleteFiles();

        getDataForReports();
        generateSlackReport();
    }

    private void getDataForReports() throws IOException {
        totalFeatures = resultFeatures.size();
        for (Object resultFeature : resultFeatures) {
            boolean isFeaturePassed = true;
            JSONObject feature = (JSONObject) resultFeature;
            JSONArray elements = (JSONArray) feature.get("elements");
            for (Object element : elements) {
                JSONObject elementObject = (JSONObject) element;
                JSONArray allSteps = (JSONArray) elementObject.get("steps");
                totalSteps += allSteps.size();
                for (Object step : allSteps) {
                    JSONObject stepObject = (JSONObject) step;
                    JSONObject resultObject = (JSONObject) stepObject.get("result");
                    String status = (String) resultObject.get("status");
                    long duration = 0;
                    if (resultObject.containsKey("duration")) {
                        duration = (long) resultObject.get("duration");
                    }
                    switch (status) {
                        case "passed":
                            stepPassed++;
                            passedDuration += duration;
                            break;
                        case "failed":
                            stepFailed++;
                            failedDuration += duration;
                            break;
                        case "skipped":
                            stepSkipped++;
                            skipDuration += duration;
                            break;
                        case "pending":
                            stepPending++;
                            pendingDuration += duration;
                            break;
                        default:
                            stepUndefined++;
                            undefineDuration += duration;
                            break;

                    }
                    totalDuration += duration;
                }
                if (elementObject.get("type").equals("scenario")) {
                    JSONArray steps = (JSONArray) elementObject.get("steps");
                    boolean isScenarioPassed = true;
                    for (Object step : steps) {
                        JSONObject stepObject = (JSONObject) step;
                        JSONObject resultObject = (JSONObject) stepObject.get("result");
                        String status = (String) resultObject.get("status");
                        if (!status.equals("passed") && isScenarioPassed) {
                            isScenarioPassed = false;
                        }
                    }
                    if (isScenarioPassed) {
                        scenarioPassed++;
                    } else {
                        scenarioFailed++;
                        if (isFeaturePassed) {
                            isFeaturePassed = false;
                        }
                    }
                }
                if (elementObject.get("type").equals("scenario")) {
                    totalScenarios++;
                }
            }
            if (isFeaturePassed) {
                featurePassed++;
            } else {
                featureFailed++;
            }
        }
        generateJsonReport();
    }

    private void generateJsonReport() throws IOException {
        JSONObject step = new JSONObject();
        step.put("totalSteps", totalSteps);
        step.put("passedStep", stepPassed);
        step.put("failedStep", stepFailed);
        step.put("skippedStep", stepSkipped);
        step.put("pendingStep", stepPending);
        step.put("undefinedStep", stepUndefined);

        JSONObject scenario = new JSONObject();
        scenario.put("totalScenarios", totalScenarios);
        scenario.put("passedScenario", scenarioPassed);
        scenario.put("failedScenario", scenarioFailed);

        JSONObject feature = new JSONObject();
        feature.put("totalFeatures", totalFeatures);
        feature.put("passedFeature", featurePassed);
        feature.put("failedFeature", featureFailed);

        JSONObject duration = new JSONObject();
        duration.put("totalDuration", getFormatDuration(totalDuration));
        duration.put("passedDuration", getFormatDuration(passedDuration));
        duration.put("failedDuration", getFormatDuration(failedDuration));
        duration.put("skippedDuration", getFormatDuration(skipDuration));
        duration.put("pendingDuration", getFormatDuration(pendingDuration));
        duration.put("undefinedDuration", getFormatDuration(undefineDuration));

        JSONObject report = new JSONObject();
        report.put("features", feature);
        report.put("scenarios", scenario);
        report.put("steps", step);
        report.put("durations", duration);
        Files.write(Paths.get("target/GitHubReport.json"), report.toJSONString().getBytes());
        System.out.println("Generate cucumber report for github success.");
    }

    private void generateSlackReport() throws IOException {
        String app = "";
        String platform = "";
        String color;
        String buildStatus;
        String text;
        try {
            Properties prop = new Properties();
            String propFileName = "target/classifications/Appium_Test.properties";
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
                app = prop.getProperty("app");
                platform = prop.getProperty("platformName");
            } else {
                System.out.println(propFileName + "is not found");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        JSONArray blocks = new JSONArray();

        if (isTestSuccess) {
            color = "#00FF00";
            buildStatus = "Build success";
        } else {
            buildStatus = "Build failed";
            color = "#FF0000";
        }
        JSONObject blockHeader = new JSONObject();
        blockHeader.put("type", "header");
        blockHeader.put("text", createTextObject("plain_text", buildStatus));
        blocks.add(blockHeader);
        JSONObject divider = new JSONObject();
        divider.put("type", "divider");
        blocks.add(divider);
        text = "*Job name:* JOB_NAME\n*Device name:* DEVICE_NAME, *Device version:* DEVICE_VERSION\n*Platform:* " + platform + "\n*Total duration:* " + getFormatDuration(totalDuration);
        if (isTestSuccess) {
            text += "\n*Feature:* " + totalFeatures + ", *Scenario:* " + totalScenarios +
                    ", *Step:* " + totalSteps;
        } else {
            text += "\n*Feature:* Total: " + totalFeatures + ", Passed: " + featurePassed + ", Failed: " + featureFailed +
                    "\n*Scenario:* " + "Total: " + totalScenarios + ", Passed: " + scenarioPassed + ", Failed: " + scenarioFailed +
                    "\n*Step:* " + "Total: " + totalSteps + ", Passed: " + stepPassed + ", Failed: " + stepFailed + ", Skipped: " + stepSkipped + ", Pending: " + stepPending + ", Undefined: " + stepUndefined;
        }
        text += "\n*App:* <" + app + ">\n*Detail*: <BUILD_URL>";
        blocks.add(createBlockObject("mrkdwn", text));

        JSONObject block = new JSONObject();
        block.put("color", color);
        block.put("blocks", blocks);

        JSONArray attachments = new JSONArray();
        attachments.add(block);

        JSONObject attachment = new JSONObject();
        attachment.put("attachments", attachments);
        Files.write(Paths.get("target/SlackReport.json"), attachment.toJSONString().getBytes());
        System.out.println("Generate slack report success.");
    }

    private JSONObject createTextObject(String type, String text) {
        JSONObject textObj = new JSONObject();
        textObj.put("type", type);
        textObj.put("text", text);
        return textObj;
    }

    private JSONObject createBlockObject(String type, String value) {
        JSONObject block = new JSONObject();
        block.put("type", "section");
        block.put("text", createTextObject(type, value));
        return block;
    }

    private String getFormatDuration(final long nanoSecond) {
        long milliSecond = nanoSecond / TIME_NANO_SECOND;
        return DurationFormatUtils.formatDuration(milliSecond, "HH:mm:ss.SSS");
    }

    private JSONArray getFeatures(final FileReader file) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object object = parser.parse(file);
        return (JSONArray) object;
    }

    private File[] getAllFiles() {
        File dir = new File("target/cucumber-reports/");
        return dir.listFiles((dir1, name) -> name.endsWith(".json"));
    }

    private void deleteFiles() {
        for (int i = 1; i < files.length; i++) {
            files[i].delete();
        }
    }
}
