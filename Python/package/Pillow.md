# Pillow 

## 1 简介

Pillow 是 `Python` 图像处理函式库 `PIL` 的一个分支。`PIL` 提供了几个操作图像的标准程序，它是一个功能强大的函式库，但自 2011 年以来就没有太多的更新，并且不支持 `Python3`。

`Pillow` 在 `PIL` 的基础上，为 `Python3` 增加了更多功能和支持。它支持一系列图像文件格式，如 `PNG`，`JPEG`，`PPM`，`GIF`，`TIFF` 和 `BMP`。我们将看到如何在图像上执行各种操作，例如裁剪，调整大小，添加文本到图像，旋转，灰阶转换。

## 2 使用

### 2.1 环境

+ python 3.8.9

+ Pillow 9.0.0

### 2.2 资源准备

[图片1](https://github.com/TomatoZ7/project/blob/master/py/test/data/Irelia.jpg)

[图片2](https://github.com/TomatoZ7/project/blob/master/py/test/data/Irelia_o.jpg)

最终目录结构是这样的：

```
- dir/

    - pillow_demp.py

    - data/
     
        - Irelia.jpg

        - Irelia_o.jpg
```

### 2.3 引入相关函数库和设置相关参数

```py
import os
import matplotlib.pyplot as plt  # 图像处理/展现的相关函数库

# 根目录路径
root_path = os.getcwd()
# 图片目录
data_path = os.path.join(root_path, 'data')
# 测试图片
test_image = os.path.join(data_path, 'Irelia.jpg')
```

### 2.4 图像物件

`Python` `Imaging Library` 中的一个关键类别是 `Image`，它定义在 `Image` 模组中。这个类别的一个实例可以通过几种方式来创建：从图像档案加载图像，从头开始创建图像或者处理其他图像。

要从图像档案加载图像，我们使用 `Image` 模块中的 `open()` 函数将路径传递给图像类别。

```py
from PIL import Image

# 加载图像
image = Image.open(test_image)

# 存储图像并转换格式（从文件扩展名获取文件存储格式）
image.save(os.path.join(data_path, 'new_image.png'))

# 显示指定图片格式
image.save(os.path.join(data_path, 'new_image.png'), 'PNG')
```

### 2.5 调整图像大小

```py
# 载入图像
image = Image.open(test_image)

# 调整图像大小
new_image = image.resize((400, 400))
print('原图像大小:', image.size)
print('新图像大小:', new_image.size)
```

输出：

```
原图像大小: (1024, 475)
新图像大小: (400, 400)
```

`resize()` 方法返回一个图像，其宽度和高度完全匹配传入的值。这可能是你想要的，但有时你可能会发现这个参数返回的图像并不理想。这主要是因为该功能没有考虑到图像的长宽的比例，所以你最终可能会看到一个图像，看起来被拉长或挤压。

### 2.6 按比例缩放(thumbnail)

```py
# 载入图像
image = Image.open(test_image)

# 按比例缩放
image.thumbnail((200, 200))
print(image.size)
```

输出：

```
(200, 93)
```

如果要调整图像大小并保持其长宽的比例，则应该使用 `thumbnail()` 函数来调整他们的大小。该函数需要表示缩略图的最大宽度和最大高度的两个整数元组参数。

### 2.7 裁剪

```py
# 载入图像
image = Image.open(test_image)

# 定义要裁剪的边界框坐标
x1 = 0
y1 = 50
x2 = 460
y2 = 320
bbox = (x1, y1, x2, y2)
# 进行裁剪
cropped_image = image.crop(bbox)

plt.imshow(cropped_image)
plt.show()
```

可以使用 `Image` 类的 `crop()` 方法裁剪图像。该方法采用一个**边界框(bounding box)**来定义裁剪区域的位置和大小，并返回一个代表裁剪图像的 `Image` 对象。框的坐标是 `(left, upper, right, lower)` 或（`x1, y1, x2, y2)`。

### 2.8 将图像粘贴在另一个图像上

```py
# 载入图像
image = Image.open(test_image)
# 载入另一张图像
watermark_image = Image.open(os.path.join(data_path, 'Irelia_o.jpg'))

# 改成合适的大小
watermark_image.thumbnail((100, 100))
# 复制图像
image_copy = image.copy()
# 指定要粘贴的右下角坐标
position = ((image_copy.width - watermark_image.width), (image_copy.height - watermark_image.height))
# 进行粘贴
image_copy.paste(watermark_image, position)
```
### 2.7 旋转

```py
# 载入图像
image = Image.open(test_image)

# 逆时针旋转 90°
# expand=True: 使旋转后的图像适应新的图像尺寸
image_rot_90 = image.rotate(90, expand=True)
# 逆时针旋转 180°
image_rot_180 = image.rotate(180)
```

### 2.8 镜像

```py
# 左右镜像
image_flip = image.transpose(Image.FLIP_LEFT_RIGHT)
# 上下镜像
image_flip = image.transpose(Image.FLIP_TOP_BOTTOM)
```

### 2.9 在图像上绘图

#### 2.9.1 案例 1

```py
# 产生一个有 4 个颜色 channels 的空白图像
blank_image = Image.new('RGBA', (400, 300), 'white')
# 在 blank_image 图像上绘图
img_draw = ImageDraw.Draw(blank_image)
# 画一个矩形
img_draw.rectangle((70, 50, 270, 200), outline='red', fill='blue')
# 取得字形物件
fnt = ImageFont.truetype('/System/Library/Fonts/Monaco.ttf', 40)
# 放上文字信息到图像
img_draw.text((70, 250), 'hello world', font=fnt, fill='green')
```

![pillow_1](https://github.com/TomatoZ7/notes-of-tz/blob/master/Python/images/pillow_1.jpg)

在这个例子中，我们用 `new()` 方法创建一个 `Image` 对象，这将返回一个没有加载图像的 `Image` 对象。然后，我们添加一个矩形和一些文本的图像。

需要注意的字体的设定，`PIL` 可以支持 `TrueType` 和 `OpenType` 字体（必须指定字体的完整路径）。

#### 2,9,2 案例 2

```py
# 产生一个有 4 个颜色 channels 的空白图像
blank_image = Image.new('RGBA', (400, 300), 'white')
# 在 blank_image 图像上绘图
img_draw = ImageDraw.Draw(blank_image)
# 在 PIL 中可以用 rectangle 来画一个四方形，但是无法控制框线的粗细
img_draw.rectangle((70, 50, 270, 200), outline=None, fill='pink')
# 通过画线来画一个四方框的框线并控制粗细
img_draw.line([(70, 50), (270, 50), (270, 200), (70, 200), (70, 50)], fill='red', width=4)
# 在 PIL 中要画一个可以控制大小的图要通过以下方式
r = 10  # 设定半径
# 以图的中心点(x, y)来计算框住图的边界框坐标[(x1, y1), (x2, y2)]
img_draw.ellipse((270-r, 200-r, 270+r, 200+r), fill='orange')
# 画一个多边形
img_draw.polygon([(40, 40), (40, 80), (80, 60), (60, 40)], fill='green', outline=None)
```

### 2.10 颜色变换

```py
# 载入图像
image = Image.open(test_image)

# 将彩色转换成灰阶
greyscale_image = image.convert('L')

# 注意要注明cmap='gray'才能够正确秀出灰阶图像
plt.imshow(greyscale_image,cmap='gray')
plt.show()
```

## 传送门

[官方文档](https://pillow.readthedocs.io/en/stable/)