from appium import webdriver
from appium.webdriver.common.appiumby import AppiumBy
from appium.options.android import UiAutomator2Options
import time
import sys, os

class TestAppium():  
    def __init__(self, appLocation, appium_server_url = 'http://127.0.0.1:4723') -> None:
        options = UiAutomator2Options()
        options.platform_name = 'Android'
        options.automation_name = 'UiAutomator2'
        options.app = appLocation   # path to the app package or use app_package and app_activity instead
        #options.allow_test_packages = True
        options.enforce_app_install = True
        options.uiautomator2_server_install_timeout = 20000
        options.adb_exec_timeout = 20000
        options.language = 'en'
        options.locale = 'US'
        options.auto_grant_permissions = True

        self.driver = webdriver.Remote(appium_server_url, options=options)
            
        self.driver.implicitly_wait(10)
        
        
    def uninstall_app(self):
        pkg = self.driver.current_package
        print(pkg)
        self.driver.remove_app(pkg)
 

    def press_home(self):
        self.driver.press_keycode(3) # keycode HOME
        
        
    def press_back(self):
        self.driver.press_keycode(4) # keycode Back
        
        
    def find_widget(self, id_str):
        try:
            w = self.driver.find_element(AppiumBy.ID, id_str)
        except:
            w = f'ID가 {id_str}인 위젯을 찾을 수 없음'
        return w
    
    def check_list_items(self, items, recyclerview, tvID):
        try:
            elements = recyclerview.find_elements(AppiumBy.ID, tvID)
            e_text = [e.text for e in elements]
            items_len = len(items)
            result = set(items[:items_len]) == set(e_text[:items_len])
            if result == False:
                return False, f'Repository 가져온 결과가 다름 {items}, {e_text}'
            else:
                return True, 'OK'
        except:
            return False, f'ID {tvID}인 위젯을 찾을 수 없음'


    def test_lab(self, username, repos):
        w_ids = ['editUsername', 'buttonQuery', 'recyclerview']
        widgets = [self.find_widget(id_str) for id_str in w_ids]
        widgets_err = [x for x in widgets if isinstance(x, str)]
        if len(widgets_err) > 0:
            return ','.join(widgets_err)
    
        editUsername, buttonQuery, recyclerview = widgets

        # username 입력
        editUsername.clear()
        editUsername.send_keys(username)
        buttonQuery.click()
        time.sleep(5)  # wait until receiving repos
        
        ok, err = self.check_list_items(repos, recyclerview, 'textView')
        
        if ok:
            return 'OK'
        else:
            return err


if __name__ == '__main__':
    if len(sys.argv) >= 2:
        app_path = sys.argv[1]
        if app_path[-13:] != 'app-debug.apk':
            print('app_path must be ended with app-debug.apk.')
            exit(-1)
        if os.path.isfile(app_path) == False:
            print(f'{app_path} does not exist!')
            exit(-1)
    else:
        print('Usage: python internet-test.py app_path')
        exit(-1)
    
    print('''
    1. Appium 서버는 실행 했나요?
    2. 에뮬레이터를 실행 했나요?
    3. 에뮬레이터는 정상적으로 동작 중인가요? 에뮬레이터가 멈춰있다면 cold boot하세요.
    ''')

    chw = TestAppium(app_path)
    r = chw.test_lab("jyheo-st", ["JavaExercise", "jyheo.github.io"])  # 테스트용 입력을 바꿔가면서 해볼 것. 
    if r == 'OK':
        score = 100
    else:
        score = 0
    print(score, r)

