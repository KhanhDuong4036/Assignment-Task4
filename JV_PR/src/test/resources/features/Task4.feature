Feature: Do task 4

  Scenario: Check Toast Message
    When Open [Home] screen
    Then Item "Show Progress Bar Button" is displayed
    And Item "Displays a Toast Button" is displayed
    When Tap on "Displays a Toast Button" button
    And "Hello selendroid toast!" is displayed in Toast Message

  Scenario: Check function of Checkbox
    When Open [Home] screen
    And Uncheck the checkbox Adds
    Then Check box is not checked

  Scenario Outline: Check function of [Show Progress Bar for a while] button
    When Open [Home] screen
    And Tap on "Show Progress Bar Button" button
    Then Item "Progress Bar" is displayed
    When Wait until loading is complete
    Then Move to [Register Page] screen successfully
    When Fill the register form with "<user name>","<email>","<password>","<label name>","<program language>" and "<adds checkbox>"
    And Tap [Register] button at [Register Page]
    Then Move to [Verify User Page] successfully
    And Submitted data is displayed with "<user name>","<email>","<password>","<label name>","<program language>" and "<adds checkbox>"
    When Tap [Register] button at [Verify User Page]
    Then Move to [Home Page] screen successfully

    Examples:
      | user name   | email                | password  | label name | program language | adds checkbox |
      | Khanh Duong | khanhduong@gmail.com | Khanh@123 | Auto Khanh | Java             | true          |

