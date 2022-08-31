package pages.jv_pr;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import task.base.BasePage;
import task.core.PageFactory;

import java.util.List;

public class RegisterPage extends BasePage {

    @AndroidFindBy(id = "inputUsername")
    private MobileElement username;

    @AndroidFindBy(id = "inputEmail")
    private MobileElement email;

    @AndroidFindBy(id = "inputPassword")
    private MobileElement password;

    @AndroidFindBy(id = "inputName")
    private MobileElement labelName;

    @AndroidFindBy(id = "android:id/text1")
    private MobileElement dropdown;

    @AndroidFindBy(xpath = "//*[@resource-id='android:id/text1']")
    private List<MobileElement> listDropdownValue;

    @AndroidFindBy(id = "input_adds")
    private MobileElement chbAdds;

    @AndroidFindBy(id = "btnRegisterUser")
    private MobileElement btnRegis;

    public RegisterPage(MobileDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageDisplayed() {
        return isForElementPresent(username);
    }

    @Override
    public RegisterPage open() {
        if (!isPageDisplayed()) {
            HomePage homePage = new PageFactory<>(HomePage.class).create();
            homePage.open().tapButton("Show Progress Bar Button").waitUntilLoadingCompleted();
            waitForPageDisplayed();
        }
        return this;
    }

    @Override
    public RegisterPage waitForPageDisplayed() {
        return this;
    }

    public RegisterPage tapRegisterButton() {
        waitForElementDisplay(btnRegis);
        btnRegis.click();
        return this;
    }

    public RegisterPage fillFormWithInfo(String txtUsername, String txtEmail, String txtPw, String txtLabelName, String programLanguage, String stt) {
        inputTxtToElement(txtUsername, username);
        inputTxtToElement(txtEmail, email);
        inputTxtToElement(txtPw, password);
        inputTxtToElement(txtLabelName, labelName);
        selectProgramLanguage(programLanguage);
        actionWithCheckBoxAdd(stt);
        getDriver().hideKeyboard();
        return this;
    }

    private RegisterPage selectProgramLanguage(String language) {
        dropdown.click();
        waitForElementDisplay(listDropdownValue.get(0));
        for (MobileElement mobileElement : listDropdownValue) {
            if (mobileElement.getText().equals(language)) {
                System.out.println(mobileElement.getText());
                mobileElement.click();
                return this;
            }
        }
        getDriver().hideKeyboard();
        return this;
    }

    private RegisterPage checkedAddsCheckBox() {
        if (!chbAdds.isSelected()) {
            chbAdds.click();
        }
        return this;
    }

    private RegisterPage unCheckedAddsCheckBox() {
        if (chbAdds.isSelected()) {
            chbAdds.click();
        }
        return this;
    }

    private RegisterPage actionWithCheckBoxAdd(String stt) {
        switch (stt) {
            case "true":
                checkedAddsCheckBox();
                return this;
            case "false":
                unCheckedAddsCheckBox();
                return this;
        }
        return this;
    }

}
