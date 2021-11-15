# ffmpeg 参数一览表

## 1 基本选项

| <div style="300pt">参数</div> | 说明 |
| :--- | :--- |
| -formats | 输出所有可用格式 |
| --f fmt | 指定格式（音频或视频格式） |
| -i filename | 指定输入文件名(linux 下可指定屏幕录制或摄像头) |
| -y | 覆盖已有文件 |
| -t duration | 记录时长为 t |
| -fs limit_size | 设置文件上限大小 |
| -ss time_off | 从指定的时间(s)开始， [-]hh:mm:ss[.xxx] 的格式也支持 |
| -itsoffset time_off | 设置时间偏移（s），该选项影响所有后面的输入文件。该偏移被加到输入文件的时戳，定义一个正偏移意味着相应的流被延迟了 offset 秒。 [-]hh:mm:ss[.xxx] 的格式也支持 |
| -title string | 标题 |
| -timestamp time | 时间戳 |
| -author string | 作者 |
| -copyright string | 版权信息 |
| -comment string | 评论 |
| -album string | album 名 |
| -v verbose | 与 log 相关的 |
| -target type | 设置目标文件类型(`vcd`, `svcd`, `dvd`, `dv`, `dv50`, `pal-vcd`, `ntsc-svcd`, ...) |
| -dframes number | 设置要记录的帧数 |

## 2 

| 参数 | 说明 |
| :--- | :--- |
| -b bitrate | 设置比特率，默认 200kb/s |
| -r fps | 设置帧数 默认 25 |
| -s size | 设置分辨率，默认 160*128 |
| -aspect aspect | 视频长宽比，4:3, 16:9 或 1.3333, 1.7777 |
| -croptop size | 顶部切除尺寸，单位像素 |
| -cropbottom size | 底部切除尺寸，单位像素 |
| -cropleft size | 左侧切除尺寸，单位像素 |
| -cropright size | 右侧切除尺寸，单位像素 |

## 资料

[ffmpeg常用参数一览表](https://www.cnblogs.com/mwl523/p/10856633.html)

[ffmpeg参数中文详细解释](https://blog.csdn.net/leixiaohua1020/article/details/12751349)

[FFmpeg 参数中文详解](https://blog.csdn.net/zhouzhiwengang/article/details/109229698)