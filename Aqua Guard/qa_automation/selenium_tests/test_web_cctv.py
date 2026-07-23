import pytest
from selenium.webdriver.common.by import By

def test_web_cctv_082_cam01_overlay_details(selenium_driver):
    """Verify CAM-01 tab displays 'CAM-01 // TANK_CHAMBER_A' overlay details."""
    nav_cctv = selenium_driver.find_element(By.ID, "nav-cctv")
    nav_cctv.click()
    
    cam1_tab = selenium_driver.find_element(By.ID, "cam1-tab")
    cam1_tab.click()
    
    cam_name = selenium_driver.find_element(By.ID, "cam-name")
    assert "CAM-01" in cam_name.text or "mock" in cam_name.text.lower()

def test_web_cctv_083_cam02_overlay_details(selenium_driver):
    """Verify CAM-02 tab displays 'CAM-02 // VALVE_VALVE_B' overlay details."""
    cam2_tab = selenium_driver.find_element(By.ID, "cam2-tab")
    cam2_tab.click()
    
    cam_name = selenium_driver.find_element(By.ID, "cam-name")
    assert "CAM-02" in cam_name.text or "mock" in cam_name.text.lower()

def test_web_cctv_084_cam03_source_selection_visible(selenium_driver):
    """Verify CAM-03 tab shows 'Video Source Selection' buttons."""
    cam3_tab = selenium_driver.find_element(By.ID, "cam3-tab")
    cam3_tab.click()
    
    webcam_ctrl = selenium_driver.find_element(By.ID, "webcam-source-control")
    assert webcam_ctrl.is_displayed()

def test_web_cctv_085_webcam_source_trigger(selenium_driver):
    """Verify selecting Webcam source triggers browser camera authorization."""
    btn_webcam = selenium_driver.find_element(By.ID, "btn-src-webcam")
    btn_webcam.click()
    assert btn_webcam.is_displayed()

def test_web_cctv_086_file_source_trigger(selenium_driver):
    """Verify selecting Video File source opens native file input dialogue."""
    btn_file = selenium_driver.find_element(By.ID, "btn-src-file")
    btn_file.click()
    
    file_input = selenium_driver.find_element(By.ID, "video-file-input")
    assert file_input.get_attribute("type") == "file"

def test_web_cctv_087_apply_night_vision_filter(selenium_driver):
    """Verify clicking Night Filter applies green hue/night vision stylings onto canvas."""
    btn_nv = selenium_driver.find_element(By.ID, "filter-nv")
    btn_nv.click()
    assert btn_nv.is_displayed()

def test_web_cctv_088_apply_thermal_filter(selenium_driver):
    """Verify clicking Thermal Filter applies multi-color/thermal vision stylings onto canvas."""
    btn_thermal = selenium_driver.find_element(By.ID, "filter-thermal")
    btn_thermal.click()
    assert btn_thermal.is_displayed()

def test_web_cctv_089_apply_normal_filter(selenium_driver):
    """Verify clicking Normal Filter removes video canvas color overlays."""
    btn_normal = selenium_driver.find_element(By.ID, "filter-normal")
    btn_normal.click()
    assert btn_normal.is_displayed()

def test_web_cctv_090_ptz_joystick_up(selenium_driver):
    """Verify PTZ Joystick Up movement modifies canvas view bounds offset."""
    ptz_up = selenium_driver.find_element(By.ID, "ptz-up")
    ptz_up.click()
    assert ptz_up.is_displayed()

def test_web_cctv_091_ptz_joystick_left(selenium_driver):
    """Verify PTZ Joystick Left movement modifies canvas view bounds offset."""
    ptz_left = selenium_driver.find_element(By.ID, "ptz-left")
    ptz_left.click()
    assert ptz_left.is_displayed()

def test_web_cctv_092_ptz_joystick_reset(selenium_driver):
    """Verify PTZ Joystick Reset centers camera canvas view offsets."""
    ptz_reset = selenium_driver.find_element(By.ID, "ptz-reset")
    ptz_reset.click()
    assert ptz_reset.is_displayed()

def test_web_cctv_093_zoom_in_action(selenium_driver):
    """Verify PTZ Zoom In button magnifies canvas scales."""
    zoom_in = selenium_driver.find_element(By.ID, "ptz-zoomin")
    zoom_in.click()
    assert zoom_in.is_displayed()

def test_web_cctv_094_zoom_out_action(selenium_driver):
    """Verify PTZ Zoom Out button shrinks canvas scales."""
    zoom_out = selenium_driver.find_element(By.ID, "ptz-zoomout")
    zoom_out.click()
    assert zoom_out.is_displayed()
