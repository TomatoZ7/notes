# windows 安装 ffmpeg

> 由于查看了以前的博文发现下载网站已经丢失，所以自己整理了一篇。    ——2021.11.12


1. 首先，进入官网：[http://ffmpeg.org/download.html](http://ffmpeg.org/download.html)

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/ffmpeg/images/ffmpeg_download_1.jpg)

2. 点击之后跳转到 [https://www.gyan.dev/ffmpeg/builds/](https://www.gyan.dev/ffmpeg/builds/)：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/ffmpeg/images/ffmpeg_download_2.jpg)

3. 点击上图 `ffmpeg-git-full.7z` 后会创建一个下载链接。

4. 下载后解压并将解压后的 `bin` 目录添加到环境变量。

5. 在命令行运行 `ffmpeg -version` 验证：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/ffmpeg/images/ffmpeg_download_3.jpg)