package pages.jv_pr;

import task.base.BasePage;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class HomePage extends BasePage {

    @AndroidFindBy(id = "waitingButtonTest")
    private MobileElement btnShowProgressBar;

    @AndroidFindBy(id = "showToastButton")
    private MobileElement btnShowToastButton;

    @AndroidFindBy(id = "android:id/button1")
    private MobileElement btnOk;

    @AndroidFindBy(xpath = "//*[(text(),'Hello selendroid toast!')]")
    private MobileElement toastMessage;

    @AndroidFindBy(id = "input_adds_check_box")
    private MobileElement chbAcceptAdd;

    @AndroidFindBy(id = "android:id/progress")
    private MobileElement progressBar;

    @AndroidFindBy(id = "android:id/progress_percent")
    private MobileElement progressPercent;

    @AndroidFindBy(id = "android:id/progress_number")
    private MobileElement progressNumber;

    @AndroidFindBy(id = "com.android.permissioncontroller:id/continue_button")
    private MobileElement btnContinous;

    public HomePage(MobileDriver driver) {
        super(driver);
    }

    @Override
    public HomePage open() {
        if (!isPageDisplayed()) {
            getDriver().launchApp();
            //acceptSystemPopup();
            acceptWarningPopup();
            waitForPageDisplayed();
        }
        return this;
    }

    private HomePage acceptSystemPopup() {
        try {
            waitForElementDisplay(btnContinous);
            btnContinous.click();
        } catch (Exception e) {
            System.out.println("System popup is not displayed");
        }
        return this;
    }

    private HomePage acceptWarningPopup() {
        try {
            waitForElementDisplay(btnOk);
            btnOk.click();
        } catch (Exception e) {
            System.out.println("Warning popup is not displayed");
        }
        return this;
    }

    @Override
    public HomePage waitForPageDisplayed() {
        waitForElementDisplay(chbAcceptAdd);
        return this;
    }


    @Override
    public boolean isPageDisplayed() {
        return isForElementPresent(chbAcceptAdd);
    }

    public HomePage tapButton(String button) {
        switch (button) {
            case "Show Progress Bar Button":
                btnShowProgressBar.click();
                break;
            case "Displays a Toast Button":
                btnShowToastButton.click();
                break;
        }
        return this;
    }

    public boolean isItemDisplayed(String item) {
        switch (item) {
            case "Show Progress Bar Button":
                return isForElementPresent(btnShowProgressBar);
            case "Displays a Toast Button":
                return isForElementPresent(btnShowToastButton);
            case "Toast Message":
                return isForElementPresent(toastMessage);
            case "Progress Bar":
                return isForElementPresent(progressBar);
        }
        return false;
    }

    public String getTxtToastMessage() {
        return toastMessage.getText();
    }

    public HomePage unCheckCheckBoxAdd() {
        chbAcceptAdd.click();
        return this;
    }

    public boolean isCheckBoxAddSelected() {
        return chbAcceptAdd.isSelected();
    }

    public HomePage waitUntilLoadingCompleted() {
        waitForElementHide(progressBar);
        return this;
    }
}
