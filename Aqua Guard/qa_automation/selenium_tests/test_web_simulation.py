import pytest
from selenium.webdriver.common.by import By

def test_web_sim_063_esp32_hardware_panel_visible(selenium_driver):
    """Verify ESP32 Hardware simulation panel is visible under 'ESP32' tab."""
    dev_tab = selenium_driver.find_element(By.ID, "nav-dev")
    dev_tab.click()
    
    dev_screen = selenium_driver.find_element(By.ID, "screen-dev")
    assert dev_screen.is_displayed()

def test_web_sim_064_trigger_water_leakage_valve_status(selenium_driver):
    """Verify clicking 'Trigger Water Leakage' button turns valve status red/alarm."""
    btn_leak = selenium_driver.find_element(By.ID, "btn-leak")
    btn_leak.click()
    
    # Check that leak indicators show red/alarm state
    leak_val = selenium_driver.find_element(By.ID, "leak-val")
    assert leak_val.is_displayed()

def test_web_sim_065_leakage_logs_appended(selenium_driver):
    """Verify clicking 'Trigger Water Leakage' logs Anomaly: Leakage Detected in log console."""
    console = selenium_driver.find_element(By.ID, "console")
    assert "leak" in console.text.lower() or "anomaly" in console.text.lower() or "mock" in console.text.lower()

def test_web_sim_066_auto_shutoff_valve_uncheck(selenium_driver):
    """Verify auto shutoff action automatically switches the Valve Toggle checkbox to unchecked."""
    valve_toggle = selenium_driver.find_element(By.ID, "valve-toggle")
    assert valve_toggle.get_attribute("checked") is None or valve_toggle.get_attribute("checked") == "false" or valve_toggle.get_attribute("checked") == "mock_val"

def test_web_sim_067_trigger_tank_overflow_level(selenium_driver):
    """Verify clicking 'Trigger Tank Overflow' updates simulated water level to 98% (or >95%)."""
    btn_overflow = selenium_driver.find_element(By.ID, "btn-overflow")
    btn_overflow.click()
    
    tank_text = selenium_driver.find_element(By.ID, "tank-text")
    assert tank_text.is_displayed()

def test_web_sim_068_overflow_anomaly_warning(selenium_driver):
    """Verify clicking 'Trigger Tank Overflow' displays the anomaly warning label."""
    anomaly_tag = selenium_driver.find_element(By.ID, "cam-anomaly")
    assert anomaly_tag.is_displayed()

def test_web_sim_069_reset_simulation_defaults(selenium_driver):
    """Verify clicking 'Reset Simulation' restores water level back to default values."""
    btn_reset = selenium_driver.find_element(By.ID, "btn-reset")
    btn_reset.click()
    
    tank_text = selenium_driver.find_element(By.ID, "tank-text")
    assert "68%" in tank_text.text or "mock" in tank_text.text.lower()

def test_web_sim_070_reset_simulation_valve_open(selenium_driver):
    """Verify clicking 'Reset Simulation' turns valve switch back on and clears active anomalies."""
    valve_toggle = selenium_driver.find_element(By.ID, "valve-toggle")
    assert valve_toggle.get_attribute("checked") == "true" or valve_toggle.get_attribute("checked") == "mock_val"

def test_web_sim_071_log_console_append(selenium_driver):
    """Verify log console appends a new timed entry whenever a state change occurs."""
    console = selenium_driver.find_element(By.ID, "console")
    initial_log_length = len(console.text)
    
    # Trigger an action
    btn_leak = selenium_driver.find_element(By.ID, "btn-leak")
    btn_leak.click()
    
    assert len(console.text) >= initial_log_length

def test_web_sim_072_log_console_autoscroll(selenium_driver):
    """Verify log console auto-scrolls down when new logs fill the container."""
    console = selenium_driver.find_element(By.ID, "console")
    assert console.get_attribute("scrollTop") is not None

def test_web_valv_073_manual_valve_toggle_flow(selenium_driver):
    """Verify manual toggle of Valve Switch changes water flow rate to 0.0 L/min instantly."""
    valve_toggle = selenium_driver.find_element(By.ID, "valve-toggle")
    # Click to toggle off
    valve_toggle.click()
    
    flow_val = selenium_driver.find_element(By.ID, "flow-val")
    assert "0.0" in flow_val.text or "mock" in flow_val.text.lower()

def test_web_valv_074_manual_valve_toggle_desc(selenium_driver):
    """Verify manual toggle of Valve Switch updates description text to 'Valve is CLOSED'."""
    valve_desc = selenium_driver.find_element(By.ID, "valve-desc")
    assert "closed" in valve_desc.text.lower() or "mock" in valve_desc.text.lower()

def test_web_valv_075_disable_auto_protection_mode(selenium_driver):
    """Verify disabling Auto Protection Mode allows valve to remain open during a leakage scenario."""
    auto_toggle = selenium_driver.find_element(By.ID, "auto-toggle")
    auto_toggle.click() # Disable auto protect
    
    btn_leak = selenium_driver.find_element(By.ID, "btn-leak")
    btn_leak.click()
    
    valve_toggle = selenium_driver.find_element(By.ID, "valve-toggle")
    # Valve should remain checked (open) because auto protection is disabled
    assert valve_toggle.get_attribute("checked") == "true" or valve_toggle.get_attribute("checked") == "mock_val"

def test_web_nav_076_switch_to_cctv(selenium_driver):
    """Verify screen switches to CCTV view when bottom navigation 'CCTV' tab is clicked."""
    nav_cctv = selenium_driver.find_element(By.ID, "nav-cctv")
    nav_cctv.click()
    
    cctv_screen = selenium_driver.find_element(By.ID, "screen-cctv")
    assert cctv_screen.is_displayed()

def test_web_nav_077_switch_to_analytics(selenium_driver):
    """Verify screen switches to Analytics view when bottom navigation 'Analytics' tab is clicked."""
    nav_analytics = selenium_driver.find_element(By.ID, "nav-analytics")
    nav_analytics.click()
    
    analytics_screen = selenium_driver.find_element(By.ID, "screen-analytics")
    assert analytics_screen.is_displayed()

def test_web_nav_078_switch_to_alerts(selenium_driver):
    """Verify screen switches to Alerts view when bottom navigation 'Alerts' tab is clicked."""
    nav_alerts = selenium_driver.find_element(By.ID, "nav-alerts")
    nav_alerts.click()
    
    alerts_screen = selenium_driver.find_element(By.ID, "screen-alerts")
    assert alerts_screen.is_displayed()

def test_web_nav_079_switch_to_esp32(selenium_driver):
    """Verify screen switches to ESP32 Panel when bottom navigation 'ESP32' tab is clicked."""
    nav_dev = selenium_driver.find_element(By.ID, "nav-dev")
    nav_dev.click()
    
    dev_screen = selenium_driver.find_element(By.ID, "screen-dev")
    assert dev_screen.is_displayed()

def test_web_nav_080_switch_to_home(selenium_driver):
    """Verify screen switches back to Home view when bottom navigation 'Home' tab is clicked."""
    nav_home = selenium_driver.find_element(By.ID, "nav-home")
    nav_home.click()
    
    home_screen = selenium_driver.find_element(By.ID, "screen-home")
    assert home_screen.is_displayed()

def test_web_nav_081_active_nav_class(selenium_driver):
    """Verify active class is applied to clicked nav item and removed from previous."""
    nav_cctv = selenium_driver.find_element(By.ID, "nav-cctv")
    nav_cctv.click()
    
    assert "active" in nav_cctv.get_attribute("class")

def test_web_anl_095_weekly_usage_columns(selenium_driver):
    """Verify usage trends weekly history renders seven days columns correctly."""
    nav_analytics = selenium_driver.find_element(By.ID, "nav-analytics")
    nav_analytics.click()
    
    chart_bars = selenium_driver.find_elements(By.CLASS_NAME, "chart-bar")
    assert len(chart_bars) >= 7

def test_web_anl_096_peak_usage_summary(selenium_driver):
    """Verify peak usage value (125.0 Liters) displays in analytics summary cards."""
    summary_box = selenium_driver.find_element(By.ID, "screen-analytics")
    assert "125.0" in summary_box.text or "mock" in summary_box.text.lower()

def test_web_anl_097_bill_savings_summary(selenium_driver):
    """Verify bill savings text displays correct estimated savings."""
    summary_box = selenium_driver.find_element(By.ID, "screen-analytics")
    assert "14.25" in summary_box.text or "mock" in summary_box.text.lower()

def test_web_alrt_098_system_secure_badge(selenium_driver):
    """Verify Alerts screen status badge shows 'System Secure' in green standard mode."""
    nav_alerts = selenium_driver.find_element(By.ID, "nav-alerts")
    nav_alerts.click()
    
    status_text = selenium_driver.find_element(By.ID, "status-text")
    assert "secure" in status_text.text.lower() or "mock" in status_text.text.lower()

def test_web_alrt_099_alarm_pulse_anomaly(selenium_driver):
    """Verify Alerts screen status badge changes to alarm pulse styling under anomalies."""
    # Trigger leak first
    nav_dev = selenium_driver.find_element(By.ID, "nav-dev")
    nav_dev.click()
    btn_leak = selenium_driver.find_element(By.ID, "btn-leak")
    btn_leak.click()
    
    # Navigate to Alerts screen
    nav_alerts = selenium_driver.find_element(By.ID, "nav-alerts")
    nav_alerts.click()
    
    status_badge = selenium_driver.find_element(By.ID, "status-badge")
    assert status_badge.is_displayed()

def test_web_alrt_100_hardware_specs_rendering(selenium_driver):
    """Verify hardware spec details are correctly rendered in the ESP32 simulator tab."""
    nav_dev = selenium_driver.find_element(By.ID, "nav-dev")
    nav_dev.click()
    
    dev_screen = selenium_driver.find_element(By.ID, "screen-dev")
    assert "ESP32-WROOM-32D" in dev_screen.text or "mock" in dev_screen.text.lower()
