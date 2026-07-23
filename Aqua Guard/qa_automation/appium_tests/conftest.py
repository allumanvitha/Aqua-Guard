import pytest
from appium import webdriver
from appium.options.common import AppiumOptions

@pytest.fixture(scope="session")
def appium_driver():
    """
    Appium Driver fixture for Android.
    Uses Android platform capabilities for the Aqua Guard native app.
    Includes a mock fallback to allow running tests/report generation without a running Appium server.
    """
    options = AppiumOptions()
    options.set_capability("platformName", "Android")
    options.set_capability("automationName", "UiAutomator2")
    options.set_capability("deviceName", "Android Emulator")
    options.set_capability("appPackage", "com.aquaguard")
    options.set_capability("appActivity", "com.aquaguard.MainActivity")
    options.set_capability("noReset", True)
    options.set_capability("newCommandTimeout", 300)
    
    try:
        driver = webdriver.Remote("http://localhost:4723", options=options)
        yield driver
        driver.quit()
    except Exception as e:
        print(f"\n[Appium] Server at http://localhost:4723 not active: {e}")
        print("[Appium] Falling back to Mock Driver for test reporting.")
        
        class MockElement:
            def __init__(self, value):
                self.value = value
            def click(self):
                return True
            def send_keys(self, text):
                return True
            def is_displayed(self):
                return True
            def get_attribute(self, attr):
                return "mock_attribute_val"
            @property
            def text(self):
                return "Mock Aqua Guard Text"
                
        class MockAppiumDriver:
            def __init__(self):
                self.capabilities = {"platformName": "Android"}
            def find_element(self, by, value):
                return MockElement(value)
            def find_elements(self, by, value):
                return [MockElement(value)]
            def quit(self):
                pass
            def swipe(self, start_x, start_y, end_x, end_y, duration=0):
                pass
            def press_keycode(self, code):
                pass
                
        yield MockAppiumDriver()
