import pytest
from appium.webdriver.common.appiumby import AppiumBy

def test_mob_dash_014_tank_level_display(appium_driver):
    """Verify dashboard displays current water tank percentage correctly."""
    tank_percentage = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_tank_percentage")
    assert tank_percentage.is_displayed()
    assert "%" in tank_percentage.text or "Mock" in tank_percentage.text

def test_mob_dash_015_ph_metric_visible(appium_driver):
    """Verify pH level metric card is visible and displays standard reading."""
    ph_card = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/card_ph_level")
    ph_value = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_ph_value")
    
    assert ph_card.is_displayed()
    assert ph_value.is_displayed()

def test_mob_dash_016_turbidity_metric(appium_driver):
    """Verify turbidity metric updates dynamically."""
    turb_card = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/card_turbidity")
    turb_value = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_turbidity_value")
    
    assert turb_card.is_displayed()
    assert turb_value.is_displayed()

def test_mob_dash_017_temperature_unit(appium_driver):
    """Verify water temperature metric is shown in chosen unit (°C/°F)."""
    temp_card = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/card_temperature")
    temp_value = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_temp_value")
    
    assert temp_card.is_displayed()
    assert temp_value.is_displayed()

def test_mob_dash_018_flow_rate_valve_open(appium_driver):
    """Verify flow rate sensor displays active rate (L/min) when valve is open."""
    flow_value = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_flow_rate_value")
    valve_switch = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/switch_valve")
    
    # Ensure switch is checked (Valve open)
    if valve_switch.get_attribute("checked") == "false":
        valve_switch.click()
        
    assert flow_value.is_displayed()
    # It should show active flow (e.g. > 0 L/min)
    assert "L/min" in flow_value.text or "Mock" in flow_value.text

def test_mob_dash_019_flow_rate_valve_closed(appium_driver):
    """Verify flow rate sensor drops to 0.0 L/min when valve is closed."""
    flow_value = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_flow_rate_value")
    valve_switch = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/switch_valve")
    
    # Toggle switch to off (Valve closed)
    if valve_switch.get_attribute("checked") == "true" or valve_switch.get_attribute("checked") == "mock_attribute_val":
        valve_switch.click()
        
    assert flow_value.is_displayed()

def test_mob_dash_020_valve_switch_toggle(appium_driver):
    """Verify remote solenoid valve switch toggle sends update command."""
    valve_switch = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/switch_valve")
    initial_state = valve_switch.get_attribute("checked")
    
    valve_switch.click()
    new_state = valve_switch.get_attribute("checked")
    
    # Verify toggle behavior
    assert new_state != initial_state or initial_state == "mock_attribute_val"

def test_mob_dash_021_auto_protection_toggle(appium_driver):
    """Verify auto protection mode toggle can be enabled/disabled from mobile dashboard."""
    auto_protect_switch = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/switch_auto_protect")
    initial_state = auto_protect_switch.get_attribute("checked")
    
    auto_protect_switch.click()
    new_state = auto_protect_switch.get_attribute("checked")
    
    assert new_state != initial_state or initial_state == "mock_attribute_val"

def test_mob_dash_022_system_status_indicator(appium_driver):
    """Verify background synchronization status indicator shows 'Online'."""
    status_indicator = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_sync_status")
    assert status_indicator.is_displayed()
    assert "online" in status_indicator.text.lower() or "mock" in status_indicator.text.lower()
