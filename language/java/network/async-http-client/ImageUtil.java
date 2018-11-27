/** 
* @param url 
*            要下载的文件URL 
* @throws Exception 
*/  
public static void downloadFile(String url) throws Exception {  
  
AsyncHttpClient client = new AsyncHttpClient();  
// 指定文件类型  
String[] allowedContentTypes = new String[] { "image/png", "image/jpeg" };  
// 获取二进制数据如图片和其他文件  
client.get(url, new BinaryHttpResponseHandler(allowedContentTypes) {  
  
    @Override  
    public void onSuccess(int statusCode, Header[] headers,  
            byte[] binaryData) {  
        String tempPath = Environment.getExternalStorageDirectory()  
                .getPath() + "/temp.jpg";  
        // TODO Auto-generated method stub  
        // 下载成功后需要做的工作  
        progress.setProgress(0);  
        //  
        Log.e("binaryData:", "共下载了：" + binaryData.length);  
        //  
        Bitmap bmp = BitmapFactory.decodeByteArray(binaryData, 0,  
                binaryData.length);  
  
        File file = new File(tempPath);  
        // 压缩格式  
        CompressFormat format = Bitmap.CompressFormat.JPEG;  
        // 压缩比例  
        int quality = 100;  
        try {  
            // 若存在则删除  
            if (file.exists())  
                file.delete();  
            // 创建文件  
            file.createNewFile();  
            //  
            OutputStream stream = new FileOutputStream(file);  
            // 压缩输出  
            bmp.compress(format, quality, stream);  
            // 关闭  
            stream.close();  
            //  
            Toast.makeText(mContext, "下载成功\n" + tempPath,  
                    Toast.LENGTH_LONG).show();  
  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
  
    }  
  
    @Override  
    public void onFailure(int statusCode, Header[] headers,  
            byte[] binaryData, Throwable error) {  
        // TODO Auto-generated method stub  
        Toast.makeText(mContext, "下载失败", Toast.LENGTH_LONG).show();  
    }  
  
    @Override  
    public void onProgress(int bytesWritten, int totalSize) {  
        // TODO Auto-generated method stub  
        super.onProgress(bytesWritten, totalSize);  
        int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);  
        // 下载进度显示  
        progress.setProgress(count);  
        Log.e("下载 Progress>>>>>", bytesWritten + " / " + totalSize);  
  
    }  
  
    @Override  
    public void onRetry(int retryNo) {  
        // TODO Auto-generated method stub  
        super.onRetry(retryNo);  
        // 返回重试次数  
    }  
  
});  

/** 
* @param path 
*            要上传的文件路径 
* @param url 
*            服务端接收URL 
* @throws Exception 
*/  
public static void uploadFile(String path, String url) throws Exception {  
File file = new File(path);  
if (file.exists() && file.length() > 0) {  
    AsyncHttpClient client = new AsyncHttpClient();  
    RequestParams params = new RequestParams();  
    params.put("uploadfile", file);  
    // 上传文件  
    client.post(url, params, new AsyncHttpResponseHandler() {  
        @Override  
        public void onSuccess(int statusCode, Header[] headers,  
                byte[] responseBody) {  
            // 上传成功后要做的工作  
            Toast.makeText(mContext, "上传成功", Toast.LENGTH_LONG).show();  
            progress.setProgress(0);  
        }  
  
        @Override  
        public void onFailure(int statusCode, Header[] headers,  
                byte[] responseBody, Throwable error) {  
            // 上传失败后要做到工作  
            Toast.makeText(mContext, "上传失败", Toast.LENGTH_LONG).show();  
        }  
  
        @Override  
        public void onProgress(int bytesWritten, int totalSize) {  
            // TODO Auto-generated method stub  
            super.onProgress(bytesWritten, totalSize);  
            int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);  
            // 上传进度显示  
            progress.setProgress(count);  
            Log.e("上传 Progress>>>>>", bytesWritten + " / " + totalSize);  
        }  
  
        @Override  
        public void onRetry(int retryNo) {  
            // TODO Auto-generated method stub  
            super.onRetry(retryNo);  
            // 返回重试次数  
        }  
  
    });  
} else {  
    Toast.makeText(mContext, "文件不存在", Toast.LENGTH_LONG).show();  
}  
