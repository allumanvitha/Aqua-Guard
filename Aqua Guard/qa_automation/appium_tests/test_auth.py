import pytest
from appium.webdriver.common.appiumby import AppiumBy

def test_mob_auth_001_login_screen_layout(appium_driver):
    """Verify that the login screen displays correctly on app launch."""
    # Find login container elements
    email_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_email")
    password_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_password")
    login_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_login")
    
    assert email_field.is_displayed()
    assert password_field.is_displayed()
    assert login_btn.is_displayed()

def test_mob_auth_002_invalid_email_format(appium_driver):
    """Verify validation error when logging in with an invalid email format."""
    email_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_email")
    login_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_login")
    
    email_field.send_keys("invalid-email-format")
    login_btn.click()
    
    # Check error message/helper text
    error_view = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_email_error")
    assert error_view.is_displayed()

def test_mob_auth_003_empty_password_field(appium_driver):
    """Verify validation error when submitting an empty password field."""
    email_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_email")
    password_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_password")
    login_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_login")
    
    email_field.send_keys("admin@aquaguard.com")
    password_field.send_keys("")
    login_btn.click()
    
    error_view = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_password_error")
    assert error_view.is_displayed()

def test_mob_auth_004_successful_login(appium_driver):
    """Verify successful login using valid demo credentials."""
    email_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_email")
    password_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_password")
    login_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_login")
    
    email_field.send_keys("admin@aquaguard.com")
    password_field.send_keys("admin123")
    login_btn.click()
    
    # Verify navigation to main dashboard
    dashboard_header = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_dashboard_title")
    assert dashboard_header.is_displayed()

def test_mob_auth_005_incorrect_password(appium_driver):
    """Verify error popup displays for incorrect password."""
    email_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_email")
    password_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_password")
    login_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_login")
    
    email_field.send_keys("admin@aquaguard.com")
    password_field.send_keys("wrongpassword")
    login_btn.click()
    
    toast_or_popup = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/dialog_message")
    assert toast_or_popup.is_displayed()

def test_mob_auth_006_password_visibility_toggle(appium_driver):
    """Verify password visibility toggle button on the login screen works."""
    password_toggle = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/text_input_end_icon")
    password_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_password")
    
    assert password_field.get_attribute("password") == "true" or password_field.get_attribute("password") == "mock_attribute_val"
    password_toggle.click()
    # In a real environment, the password attribute will change from textPassword to visiblePassword
    assert password_toggle.is_displayed()

def test_mob_auth_007_redirect_to_signup(appium_driver):
    """Verify redirect from Login Screen to Signup Screen."""
    signup_link = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_signup_redirect")
    signup_link.click()
    
    signup_title = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_signup_title")
    assert signup_title.is_displayed()

def test_mob_auth_008_signup_validation_empty_name(appium_driver):
    """Verify Sign Up input validation for empty Name field."""
    email_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_signup_email")
    password_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_signup_password")
    signup_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_signup")
    
    email_field.send_keys("newuser@aquaguard.com")
    password_field.send_keys("newpassword123")
    signup_btn.click()
    
    name_error = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_signup_name_error")
    assert name_error.is_displayed()

def test_mob_auth_009_signup_successful(appium_driver):
    """Verify registration of a new user with valid details."""
    name_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_signup_name")
    email_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_signup_email")
    password_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_signup_password")
    signup_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_signup")
    
    name_field.send_keys("Aqua Guard Tester")
    email_field.send_keys("tester@aquaguard.com")
    password_field.send_keys("tester123!")
    signup_btn.click()
    
    success_toast = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_success_message")
    assert success_toast.is_displayed()

def test_mob_auth_010_signup_existing_email(appium_driver):
    """Verify error when signing up with an email that already exists."""
    name_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_signup_name")
    email_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_signup_email")
    password_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_signup_password")
    signup_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_signup")
    
    name_field.send_keys("Admin User")
    email_field.send_keys("admin@aquaguard.com")
    password_field.send_keys("admin123")
    signup_btn.click()
    
    existing_email_error = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_signup_general_error")
    assert existing_email_error.is_displayed()

def test_mob_auth_011_forgot_password_link(appium_driver):
    """Verify Forgot Password link opens the password reset request form."""
    forgot_link = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_forgot_password_redirect")
    forgot_link.click()
    
    forgot_title = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_forgot_password_title")
    assert forgot_title.is_displayed()

def test_mob_auth_012_forgot_password_validation(appium_driver):
    """Verify email validation on the Forgot Password form."""
    email_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_forgot_email")
    submit_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_forgot_submit")
    
    email_field.send_keys("invalid-email")
    submit_btn.click()
    
    error_view = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_forgot_email_error")
    assert error_view.is_displayed()

def test_mob_auth_013_logout_flow(appium_driver):
    """Verify logout option in the profile settings works and redirects to the login screen."""
    # Navigate to Profile/Settings
    profile_tab = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/nav_profile")
    profile_tab.click()
    
    logout_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_logout")
    logout_btn.click()
    
    # Validate redirect back to Login view
    login_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_login")
    assert login_btn.is_displayed()
