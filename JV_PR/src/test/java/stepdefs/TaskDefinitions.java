package stepdefs;

import pages.jv_pr.RegisterPage;
import pages.jv_pr.VerifyUserPage;
import task.core.PageFactory;
import cucumber.api.java8.En;
import org.testng.Assert;
import pages.jv_pr.HomePage;

public class TaskDefinitions implements En {

    HomePage homePage = new PageFactory<>(HomePage.class).create();
    RegisterPage registerPage = new PageFactory<>(RegisterPage.class).create();
    VerifyUserPage verifyUserPage = new PageFactory<>(VerifyUserPage.class).create();

    public TaskDefinitions() {

        When("^Open \\[Home\\] screen$", () -> {
            homePage.open();
        });
        Then("Item {string} is displayed", (String item) -> {
            Assert.assertTrue(homePage.isItemDisplayed(item), "Item " + item + " is not item displayed");
        });
        When("Tap on {string} button", (String button) -> {
            homePage.tapButton(button);
        });
        And("{string} is displayed in Toast Message", (String toast) -> {
            Thread.sleep(1000);
            Assert.assertTrue(homePage.getDriver().getPageSource().contains(toast), "Toast Message is not correct");
        });
        Then("^Move to \\[Home Page\\] screen successfully$", () -> {
            Assert.assertTrue(homePage.waitForPageDisplayed().isPageDisplayed(), "Home Page is not displayed");
        });
        And("^Uncheck the checkbox Adds$", () -> {
            homePage.unCheckCheckBoxAdd();
        });
        Then("^Check box is not checked$", () -> {
            Thread.sleep(2000);
            Assert.assertFalse(homePage.isCheckBoxAddSelected(), "Check Box is still checked");
        });
        When("^Wait until loading is complete$", () -> {
            homePage.waitUntilLoadingCompleted();
        });
        Then("^Move to \\[Register Page\\] screen successfully$", () -> {
            Assert.assertTrue(registerPage.waitForPageDisplayed().isPageDisplayed(), "Register Page is not displayed");
        });
        When("^Tap \\[Register\\] button at \\[Verify User Page\\]$", () -> {
            verifyUserPage.tapRegisterButton();
        });
        And("^Tap \\[Register\\] button at \\[Register Page\\]$", () -> {
            //registerPage.hideKeyBoard();
            registerPage.tapRegisterButton();
        });
        Then("^Move to \\[Verify User Page\\] successfully$", () -> {
            Assert.assertTrue(verifyUserPage.waitForPageDisplayed().isPageDisplayed(), "Verify User Page is not displayed");
        });
        When("Fill the register form with {string},{string},{string},{string},{string} and {string}", (String name, String email, String pass, String label, String programeLanguage, String action) -> {
            registerPage.fillFormWithInfo(name, email, pass, label, programeLanguage, action);
        });
        And("Submitted data is displayed with {string},{string},{string},{string},{string} and {string}", (String name, String email, String pass, String label, String programeLanguage, String action) -> {
            Assert.assertEquals(verifyUserPage.getInfoAfterSubmit("user name"), name);
            Assert.assertEquals(verifyUserPage.getInfoAfterSubmit("email"), email);
            Assert.assertEquals(verifyUserPage.getInfoAfterSubmit("password"), pass);
            Assert.assertEquals(verifyUserPage.getInfoAfterSubmit("label name"), label);
            Assert.assertEquals(verifyUserPage.getInfoAfterSubmit("program language"), programeLanguage);
            Assert.assertEquals(verifyUserPage.getInfoAfterSubmit("action"), action);
        });

    }
}
