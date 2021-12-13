# 文件相关方法

### 获取视频某一帧

```js
/**
 * @param {int} 视频播放时间
 * @param {string} 视频 Url
 * @return {string} 返回 base64 图片
 */
function getVideoImage(time, videoUrl) {
  return new Promise((resolve, reject) => {
    let videoElem = document.createElement("video");
    videoElem.src = videoUrl;
    videoElem.crossOrigin = "Anonymous";
    videoElem.currentTime = time > 0 ? time : 0.01; // 如果为 0 则取 0.01s

    // 画图
    videoElem.oncanplay = () => {
      const { videoHeight, videoWidth } = videoElem;

      let canvas = document.createElement("canvas");
      canvas.width = videoWidth;
      canvas.height = videoHeight;
      canvas
        .getContext("2d")
        .drawImage(videoElem, 0, 0, canvas.width, canvas.height);

      resolve(canvas.toDataURL("image/png"));
    };
  });
}
```

### base64 转文件

```js
/**
 * @param {string} base64 编码
 * @param {filename} 文件名
 * @return {File}
 */
function base64ToFile(base, filename) {
  var arr = base.split(",");
  var mime = arr[0].match(/:(.*?);/)[1];
  var bstr = atob(arr[1]);
  var n = bstr.length;
  var u8arr = new Uint8Array(n);
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n);
  }
  //转换成file对象
  return new File([u8arr], filename, { type: mime });
}
```
