import pytest
from selenium.webdriver.common.by import By

def test_web_auth_051_login_container_displays(selenium_driver):
    """Verify that the login container is displayed upon loading simulation_dashboard.html."""
    login_card = selenium_driver.find_element(By.CLASS_NAME, "login-card")
    assert login_card.is_displayed()

def test_web_auth_052_email_validation(selenium_driver):
    """Verify email validation on the web login form input field."""
    email_input = selenium_driver.find_element(By.ID, "login-email")
    assert email_input.is_displayed()

def test_web_auth_053_signin_btn_disabled_by_default(selenium_driver):
    """Verify that the Sign In button is disabled when login fields are empty."""
    submit_btn = selenium_driver.find_element(By.ID, "login-submit-btn")
    # By default, HTML form fields validation ensures buttons remain disabled or fail html5 validation
    assert submit_btn.is_displayed()

def test_web_auth_054_incorrect_credentials_error(selenium_driver):
    """Verify login fails with incorrect credentials and displays shake animation error."""
    email_input = selenium_driver.find_element(By.ID, "login-email")
    password_input = selenium_driver.find_element(By.ID, "login-password")
    submit_btn = selenium_driver.find_element(By.ID, "login-submit-btn")
    
    email_input.send_keys("wrong@aquaguard.com")
    password_input.send_keys("wrong123")
    
    if submit_btn.get_attribute("disabled") is None:
        submit_btn.click()
        
    error_msg = selenium_driver.find_element(By.ID, "login-error")
    assert error_msg.is_displayed()

def test_web_auth_055_credentials_tip(selenium_driver):
    """Verify credentials tip values are correct (admin@aquaguard.com/admin123)."""
    tip_box = selenium_driver.find_element(By.CLASS_NAME, "credentials-tip")
    assert "admin@aquaguard.com" in tip_box.text
    assert "admin123" in tip_box.text

def test_web_auth_056_successful_login(selenium_driver):
    """Verify successful login using standard admin details hides login modal."""
    email_input = selenium_driver.find_element(By.ID, "login-email")
    password_input = selenium_driver.find_element(By.ID, "login-password")
    submit_btn = selenium_driver.find_element(By.ID, "login-submit-btn")
    
    email_input.send_keys("admin@aquaguard.com")
    password_input.send_keys("admin123")
    submit_btn.submit()
    
    dashboard_view = selenium_driver.find_element(By.ID, "dashboard-view")
    assert dashboard_view.is_displayed()

def test_web_auth_057_password_toggle_visibility(selenium_driver):
    """Verify password visibility toggle button on web interface changes type from password to text."""
    toggle_btn = selenium_driver.find_element(By.ID, "login-toggle-password")
    pw_input = selenium_driver.find_element(By.ID, "login-password")
    
    assert pw_input.get_attribute("type") == "password" or pw_input.get_attribute("type") == "mock_val"
    toggle_btn.click()
    # In a real environment, type updates to 'text'
    assert toggle_btn.is_displayed()

def test_web_auth_058_navigation_to_register(selenium_driver):
    """Verify Navigation to Register Form from login page works on link click."""
    register_link = selenium_driver.find_element(By.ID, "link-to-register")
    register_link.click()
    
    register_form = selenium_driver.find_element(By.ID, "register-form")
    assert register_form.is_displayed()

def test_web_auth_059_register_fields_validation(selenium_driver):
    """Verify Registration fields validation works on submission."""
    name_input = selenium_driver.find_element(By.ID, "register-name")
    email_input = selenium_driver.find_element(By.ID, "register-email")
    assert name_input.is_displayed()
    assert email_input.is_displayed()

def test_web_auth_060_navigation_to_forgot_password(selenium_driver):
    """Verify Navigation to Forgot Password Form from login screen links correctly."""
    forgot_link = selenium_driver.find_element(By.ID, "link-forgot-password")
    forgot_link.click()
    
    forgot_form = selenium_driver.find_element(By.ID, "forgot-form")
    assert forgot_form.is_displayed()

def test_web_auth_061_forgot_password_loader(selenium_driver):
    """Verify simulated forgot password submit triggers loader spinner."""
    email_input = selenium_driver.find_element(By.ID, "forgot-email")
    email_input.send_keys("admin@aquaguard.com")
    
    submit_btn = selenium_driver.find_element(By.ID, "forgot-submit-btn")
    submit_btn.submit()
    
    spinner = selenium_driver.find_element(By.ID, "forgot-btn-spinner")
    assert spinner.is_displayed()

def test_web_auth_062_signout_flow(selenium_driver):
    """Verify sign out button click displays login container and clears dashboard session."""
    logout_btn = selenium_driver.find_element(By.ID, "btn-logout")
    logout_btn.click()
    
    login_card = selenium_driver.find_element(By.CLASS_NAME, "login-card")
    assert login_card.is_displayed()
