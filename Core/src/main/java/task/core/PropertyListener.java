package task.core;

import org.testng.ITestContext;
import org.testng.TestListenerAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

public class PropertyListener extends TestListenerAdapter {
    @Override
    public void onStart(ITestContext iTestContext) {
        String fileName = iTestContext.getName().replace(" ", "_").concat(".properties");
        String reportDirectory = new File(System.getProperty("user.dir")).getAbsolutePath() + "/target/classifications";
        File directory = new File(reportDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File destFile = new File(reportDirectory + "/" + fileName);
        try {
            FileOutputStream fos = new FileOutputStream(destFile);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (Map.Entry<String, String> entry : iTestContext.getCurrentXmlTest().getAllParameters().entrySet()) {
                if (!entry.getKey().equals("server") && !entry.getKey().equals("udid")) {
                    bw.write(entry.getKey() + "=" + entry.getValue());
                    bw.newLine();
                }
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStart(iTestContext);
    }
}
