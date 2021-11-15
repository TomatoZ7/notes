# php + ffmpeg 实战

## 1 获取视频信息

```php
/**
 * @param $video_path 如果是本地地址效率比较高
 */
function getVideoInfo(string $video_path)
{
    try {

        // 初始化参数，根据实际情况进行修改
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