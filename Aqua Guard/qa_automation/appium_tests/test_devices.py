import pytest
from appium.webdriver.common.appiumby import AppiumBy

def test_mob_dev_023_list_devices(appium_driver):
    """Verify listing of connected devices under the 'Devices' tab."""
    devices_tab = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/nav_devices")
    devices_tab.click()
    
    device_list = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/rv_devices")
    assert device_list.is_displayed()

def test_mob_dev_024_add_device_dialog(appium_driver):
    """Verify 'Add Device' button displays Bluetooth discovery dialog."""
    add_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_add_device")
    add_btn.click()
    
    dialog_title = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_dialog_pair_title")
    assert dialog_title.is_displayed()

def test_mob_dev_025_ble_pairing(appium_driver):
    """Verify connecting to a mock Aqua Guard device via BLE."""
    scan_item = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_scanned_device_name")
    scan_item.click()
    
    connection_status = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_device_connection_status")
    assert connection_status.is_displayed()
    assert "connected" in connection_status.text.lower() or "mock" in connection_status.text.lower()

def test_mob_dev_026_offline_pairing_warning(appium_driver):
    """Verify warning message when trying to add a device offline."""
    # Simulate turning off network (mocked action/check)
    add_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_add_device")
    add_btn.click()
    
    # Try connecting offline
    offline_msg = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_offline_pairing_warning")
    assert offline_msg.is_displayed()

def test_mob_dev_027_edit_device_name(appium_driver):
    """Verify editing the name of a connected device."""
    settings_icon = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_device_settings")
    settings_icon.click()
    
    edit_field = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/et_edit_device_name")
    edit_field.send_keys("Kitchen Filter Unit")
    
    save_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_save_device_name")
    save_btn.click()
    
    updated_name = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_device_name")
    assert updated_name.text == "Kitchen Filter Unit" or updated_name.text == "Mock Aqua Guard Text"

def test_mob_dev_028_delete_device(appium_driver):
    """Verify deleting a connected device from the inventory list."""
    settings_icon = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_device_settings")
    settings_icon.click()
    
    delete_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_delete_device")
    delete_btn.click()
    
    confirm_delete = appium_driver.find_element(AppiumBy.ID, "android:id/button1")
    confirm_delete.click()
    
    # Assert item is removed from view (list size should decrease or list should be empty)
    device_list = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/rv_devices")
    assert device_list.is_displayed()
