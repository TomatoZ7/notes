# php + ffmpeg 实战

## 1.获取视频信息

```php
/**
 * @param $video_path 视频路径，如果是本地地址效率比较高
 */
function getVideoInfo(string $video_path)
{
    try {

        // 初始化参数，根据实际情况自定义
        $cmd = 'ffmpeg -i "%s" 2>&1';
        $data = [];

        $command = sprintf($cmd, $video_path);
        ob_start();
        passthru($command);
        $info = ob_get_contents();
        ob_end_clean();

        if (preg_match("/Duration: (.*?), start: (.*?), bitrate: (\d*) kb\/s/", $info, $match)) {
            $data['duration'] = $match[1]; // 播放时间
            $arr_duration = explode(':', $match[1]);
            $data['seconds'] = $arr_duration[0] * 3600 + $arr_duration[1] * 60 + $arr_duration[2]; // 将播放时间转为秒数
            $data['start'] = $match[2]; // 开始时间
            $data['bitrate'] = $match[3]; // 码率(kb/s)
        }

        if (preg_match("/Video: (.*?), (.*?), ([0-9]+)x([0-9]+)[,\s]/", $info, $match)) {
            $data['vcodec'] = $match[1]; // 视频编码格式
            $data['vformat'] = $match[2]; // 视频格式
            $data['resolution'] = $match[3] . "x" . $match[4]; // 视频分辨率
            $data['width'] = $match[3]; // 视频宽度
            $data['height'] = $match[4]; // 视频高度
        }

        if (preg_match("/Audio: (\w*), (\d*) Hz/", $info, $match)) {
            $data['acodec'] = $match[1]; // 音频编码
            $data['asamplerate'] = $match[2]; // 音频采样频率
        }

        return $data;

    } catch (\Exception $e) {
        // 处理异常
    }
}
```

## 2.视频转 GIF

```php
/**
 * @param $video_path 视频路径
 */
function getVideoInfo(string $video_path)
{
	try {

        // 初始化参数，根据实际情况自定义
		$crop_start_time = '00:00:00.000'; // 裁剪开始时间，格式 hh:mm:ss[.xxx]
		$crop_length = '119.99999999999997'; // 裁剪时间
		$fps = 8; // 帧数
		$crop_width = '986.5365853658536'; // 裁剪宽度
		$crop_height = '561.9512195121952'; // 裁剪高度
		$crop_top = '999.0243902439024'; // 裁剪框与顶部距离
		$crop_left = '97.30081300813008'; // 裁剪框与左侧距离
		$play_rate = '1'; // 播放倍速
		$output_path = md5($video_path) . '.gif'; // 输出

		// 视频过滤器
		$crop_filter = "{$crop_width}:{$crop_height}:{$crop_left}:{$crop_top},setpts=PTS/{$play_rate}";

		/**
		 * -y : 覆盖已有文件
		 * -ss : 从指定的时间开始
		 * -t : 是否记录视频时长，1是0否
		 * -i : 输入文件名
		 * -r : 帧数
		 * -vf : 视频过滤器
		 */
		$command = "ffmpeg -y -ss {$crop_start_time} -t {$crop_length} -i {$video_path} -r {$fps} -vf crop={$crop_filter} {$output_path}";

		exec($command, $output, $code);

		// 业务处理

    } catch (\Exception $e) {
        // 处理异常
    }
}
```

## 3.静音检测

```shell
ffmpeg -i xxx.mp4 -af silencedetect=n=-50dB:d=0.5 -f null - 2>&1
```

**参考**

[FFplay文档解读-23-音频过滤器八 - 简书](https://www.jianshu.com/p/e0824a9bac4e)

[如何检测音频文件末尾的静音 - Thinbug](https://www.thinbug.com/q/42507879)