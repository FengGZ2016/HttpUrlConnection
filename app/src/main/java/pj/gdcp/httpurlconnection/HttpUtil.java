package pj.gdcp.httpurlconnection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 作者：国富小哥
 * 日期：2017/6/16
 * Created by Administrator
 * 访问网络的工具类
 */

public class HttpUtil {

    /**
     * 利用HttpURLConnection来访问网络
     * */
    public static void sendHttpRequest(final String address,final HttpCallbackListener callbackListener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try {
                    URL url=new URL(address);
                    //获得connection对象
                    connection= (HttpURLConnection) url.openConnection();
                    //设置请求方式
                    connection.setRequestMethod("GET");
                    //设置超时
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    //开启输入
                    connection.setDoInput(true);
                    //开启输出
                    connection.setDoOutput(true);
                    //得到输入流
                    InputStream in=connection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuffer buffer=new StringBuffer();
                    String line;
                    while ((line=reader.readLine())!=null){
                        buffer.append(line);
                    }
                    //返回响应数据
                   if (callbackListener!=null){
                       callbackListener.onFinish(buffer.toString());
                   }

                } catch (Exception e) {
                    e.printStackTrace();
                    //返回响应数据
                    if (callbackListener!=null){
                        callbackListener.onError(e);
                    }

                }finally {
                    if (connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 运用OKHTTP访问网络
     * @param callback 自带的回调接口回调
     * */
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        //异步方式
        client.newCall(request).enqueue(callback);

    }
}
