# Summary

> Briefly describe the feature that is needed.
> 

*Example*: Implement a new feature that allows users to create a new user in the UI

## **Background**

> Explain why this feature is necessary.  What problem will it solve or what gap will it fill?
> 

*Example*: Users need a way to create a new username and password in case they have never been logged into the app before. This feature allows users to create a new, accepted, username and password which gives them a new user to the app. This feature will improve user experience and account security.

## **Implementation Steps**

> What steps should be made for implementing this release?
> 
1. Create a “sign up” page with text fields for username and password + a “log in” button 
2. Create a “new user” button on the login page
3. When clicked, open the “sign up” page where the user can add username and password
4. When clicking the “log in” button, open the existing main page
5. Save the user information in a new file, ensuring the password is hashed and username saved correctly.

## **Acceptance Criteria**

> What features should be implemented for this issue to be accepted?
> 
- [ ]  Sign up page is created
- [ ]  Buttons leads users to the correct pages
- [ ]  Each new user gets its own user file
- [ ]  The new password is hashed and saved in the user file

## **Additional Information**

> Any additional information about the feature
> 
- If the username is not unique, there should be a warning to change username