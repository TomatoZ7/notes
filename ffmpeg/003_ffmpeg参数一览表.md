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
| -itsoffset time_off | 设置时间偏移（s），该选项影响所有后面的输入文件。<br />该偏移被加到输入文件的时戳，定义一个正偏移意味着相应的流被延迟了 offset 秒。 [-]hh:mm:ss[.xxx] 的格式也支持 |
| -title string | 标题 |
| -timestamp time | 时间戳 |
| -author string | 作者 |
| -copyright string | 版权信息 |
| -comment string | 评论 |
| -album string | album 名 |
| -v verbose | 与 log 相关的 |
| -target type | 设置目标文件类型(`vcd`, `svcd`, `dvd`, `dv`, `dv50`, `pal-vcd`, `ntsc-svcd`, ...) |
| -dframes number | 设置要记录的帧数 |

## 2 视频选项

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
| -padtop size | 顶部补齐尺寸，单位像素 |
| -padbottom size | 底部补齐尺寸，单位像素 |
| -padleft size | 左补齐 |
| -padright size | 右补齐 |
| -vn | 取消视频 |
| -vcodec codec | 强制使用 `codec` 编解码方式 |
| -sameq | 使用同样视频质量作为源 |
| -pass n | 选择处理遍数（1或者2）。两遍编码非常有用。第一遍生成统计信息，第二遍生成精确的请求的码率 |
| -passlogfile file	| 选择两遍的纪录文件名为 `file` |
| -newvideo | 在现在的视频流后面加入新的视频流 |

## 3 高级视频选项

| 参数 | 说明 |
| :--- | :--- |
| -g gop_size | 设置图像组大小 |
| -intra | 仅适用帧内编码 |
| -qscale q | 使用固定的视频量化标度(VBR)，取值 0.01 - 255，越小质量越好 |
| -qmin q | 最小视频量化标度(VBR) |
| -qmax q | 最大视频量化标度(VBR) |
| -qdiff | q 量化标度间最大偏差(VBR) |

## 4 音频选项

| 参数 | 说明 |
| :--- | :--- |
| -g gop_size | 设置图像组大小 |


## 资料

[ffmpeg常用参数一览表](https://www.cnblogs.com/mwl523/p/10856633.html)

[ffmpeg参数中文详细解释](https://blog.csdn.net/leixiaohua1020/article/details/12751349)

[FFmpeg 参数中文详解](https://blog.csdn.net/zhouzhiwengang/article/details/109229698)