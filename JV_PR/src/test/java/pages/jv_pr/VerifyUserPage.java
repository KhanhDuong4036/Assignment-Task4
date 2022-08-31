package pages.jv_pr;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import task.base.BasePage;
import task.core.PageFactory;

public class VerifyUserPage extends BasePage {

    @AndroidFindBy(id = "label_name_data")
    private MobileElement labelName;

    @AndroidFindBy(id = "label_username_data")
    private MobileElement userName;

    @AndroidFindBy(id = "label_password_data")
    private MobileElement password;

    @AndroidFindBy(id = "label_email_data")
    private MobileElement email;

    @AndroidFindBy(id = "label_preferedProgrammingLanguage_data")
    private MobileElement preferedProgrammingLanguage;

    @AndroidFindBy(id = "label_acceptAdds_data")
    private MobileElement acceptAdds;

    @AndroidFindBy(id = "buttonRegisterUser")
    private MobileElement btnRegis;

    public VerifyUserPage(MobileDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageDisplayed() {
        return isForElementPresent(preferedProgrammingLanguage);
    }

    @Override
    public BasePage open() {
        if (!isPageDisplayed()) {
            RegisterPage registerPage = new PageFactory<>(RegisterPage.class).create();
            registerPage.open().tapRegisterButton();
            waitForPageDisplayed();
        }
        return this;
    }

    @Override
    public VerifyUserPage waitForPageDisplayed() {
        waitForElementDisplay(preferedProgrammingLanguage);
        return this;
    }

    public VerifyUserPage tapRegisterButton() {
        btnRegis.click();
        return this;
    }

    public String getInfoAfterSubmit(String info) {
        switch (info) {
            case "user name":
                return userName.getText();
            case "email":
                return email.getText();
            case "password":
                return password.getText();
            case "label name":
                return labelName.getText();
            case "program language":
                return preferedProgrammingLanguage.getText();
            case "action":
                return acceptAdds.getText();
        }
        return "";
    }
}
