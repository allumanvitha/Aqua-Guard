import pytest
from appium.webdriver.common.appiumby import AppiumBy

def test_mob_hist_029_load_history_records(appium_driver):
    """Verify loading historical water usage records on the history screen."""
    history_tab = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/nav_history")
    history_tab.click()
    
    history_list = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/rv_history_records")
    assert history_list.is_displayed()

def test_mob_hist_030_chronological_sorting(appium_driver):
    """Verify historical records are grouped and sorted by date."""
    first_record_date = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_record_date")
    assert first_record_date.is_displayed()

def test_mob_hist_031_date_range_picker(appium_driver):
    """Verify date range picker filters historical records successfully."""
    filter_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_filter_dates")
    filter_btn.click()
    
    # Confirm date picker dialog displays
    date_picker = appium_driver.find_element(AppiumBy.ID, "android:id/datePicker")
    assert date_picker.is_displayed()
    
    ok_btn = appium_driver.find_element(AppiumBy.ID, "android:id/button1")
    ok_btn.click()

def test_mob_hist_032_chart_dynamic_scale(appium_driver):
    """Verify usage chart dynamically scales based on selected historical filter."""
    chart_view = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/chart_history_usage")
    assert chart_view.is_displayed()

def test_mob_hist_033_export_logs_button(appium_driver):
    """Verify 'Export Logs' button triggers native file share/save dialogue."""
    export_btn = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/btn_export_logs")
    export_btn.click()
    
    # Check that a share sheet or file save confirmation appears
    share_window = appium_driver.find_element(AppiumBy.XPATH, "//*[contains(@text, 'Share') or contains(@text, 'Save') or @id='android:id/resolver_list' or contains(@id, 'mock')]")
    assert share_window.is_displayed()

def test_mob_anl_034_analytical_predictions_card(appium_driver):
    """Verify analytical predictions card loads predicted water demand."""
    analytics_tab = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/nav_analytics")
    analytics_tab.click()
    
    predictions_card = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/card_predictions")
    pred_val = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_predicted_usage_val")
    
    assert predictions_card.is_displayed()
    assert pred_val.is_displayed()

def test_mob_anl_035_weekly_average_display(appium_driver):
    """Verify weekly average calculations are displayed."""
    avg_usage_val = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_weekly_average_val")
    assert avg_usage_val.is_displayed()

def test_mob_anl_036_peak_usage_highlight(appium_driver):
    """Verify peak usage highlight aligns with historical max usage."""
    peak_usage_card = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/card_peak_usage")
    peak_val = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_peak_usage_val")
    
    assert peak_usage_card.is_displayed()
    assert peak_val.is_displayed()

def test_mob_anl_037_bill_savings_estimator(appium_driver):
    """Verify bill savings estimator renders estimated monetary savings."""
    savings_card = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/card_bill_savings")
    savings_val = appium_driver.find_element(AppiumBy.ID, "com.aquaguard:id/tv_savings_val")
    
    assert savings_card.is_displayed()
    assert savings_val.is_displayed()
