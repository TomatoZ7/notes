# macOS selenium 打开 Chrome 浏览器

背景：python3 + selenium

## 1 查看 Chrome 版本

右上角 -> 帮助 -> 关于 Google Chrome

![selenium_chrome_1](https://github.com/TomatoZ7/notes-of-tz/blob/master/Python/images/selenium_chrome_1.jpg)

## 2 安装 chromedriver

下载地址：[https://chromedriver.storage.googleapis.com/index.html](https://chromedriver.storage.googleapis.com/index.html)

选择跟自己浏览器对应的版本，下载后解压得到 `chromedriver.exe`，把它放入你的目录。

## 3 代码

```python
from selenium import webdriver

driver = webdriver.Chrome(executable_path=r'/Users/test/drivers/chromedriver') # 你的 chromedriver 安装路径

driver.maximize_window()
driver.get('https://www.baidu.com/')
```

如果 Chrome 浏览器没有安装在默认路径下，需要在代码里指明 Chrome 安装的路径。

```python
...

options = webdriver.ChromeOptions()
options.binary_location = '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome'
driver = webdriver.Chrome(executable_path=r'/Users/tz7/Tools/chromedriver', chrome_options=options)

...
```