package pj.gdcp.httpurlconnection;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private final String Tag="MainActivity";
    String urlstr = "http://a1.gdcp.cn/DocHtml/2390/2017/4/27/2078824639837.html";
    private Button btn_load;
    private TextView webText;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initHandler();
        initView();
    }

    private void initHandler() {
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //在这里更新ui
            if(msg.what==1){
                String webContent= (String) msg.obj;
                analyseHTML(webContent);
               // showResponse(webContent);
            }
            }
        };
    }

    /**
     * 解析HTML
     *
     * @param webContent*/
    private void analyseHTML(String webContent) {
        if(webContent!=null){
            Document document= Jsoup.parse(webContent);
           Element element= document.body();
            String elementStr=element.text();
            webText.setText(elementStr);
        }

    }

    private void initView() {
        btn_load= (Button) findViewById(R.id.button);
        webText= (TextView) findViewById(R.id.webText);
        /**
         * 点击事件
         * */
        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useHttpUrl();
               // useOkHttp();

            }
        });
    }
    /**
     * 调用工具类的sendHttpRequest方法
     * */
    private void sendHttpRequest(){

        HttpUtil.sendHttpRequest("", new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //访问网络成功，response为响应消息

            }

            @Override
            public void onError(Exception e) {
                //访问网络失败
            }
        });
    }

    /**
     * 调用工具类的sendOkHttpRequest方法
     * */
    private void sendOkHttpRequest(){

        HttpUtil.sendOkHttpRequest("", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //访问网络失败
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //访问网络成功，response为响应消息
            }
        });
    }

    /**
     * 使用OkHttp访问网络
     *  ①、call.execute()，非异步方式，会阻塞线程，等待返回结果。
     *  ②、call.enqueue(Callback)，异步方式。
     * */
    private void useOkHttp() {
        //Callexecute();
        Callenqueue();


    }

    /**
     * 异步方式
     * */
    private void Callenqueue() {
        //首先要创建一个okhttpClient的实例
        OkHttpClient client=new OkHttpClient();
        //然后需要创建一个request对象，并通过url（）的方法来设置目标的网络地址
        Request request=new Request.Builder().url(urlstr).build();
        //new Call
        Call call=client.newCall(request);
        //调用client的enqueue方法请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(Tag,"onFailure"+e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取响应消息体
                 String result=response.body().string();
                //调用更新UI的方法
                showResponse(result);

            }
        });
    }

    /**
     * 非异步方式，会阻塞线程，等待返回结果
     * */
    private void Callexecute() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //首先要创建一个okhttpClient的实例
                OkHttpClient client=new OkHttpClient();
                //然后需要创建一个request对象，并通过url（）的方法来设置目标的网络地址
                Request request=new Request.Builder().url(urlstr).build();
                //之后调用OkHttpClient的newCall()方法来创建一个Call对象，
                // 并调用它的execute方法发送请求，获得服务器返回的数据
                try {
                    Response response=client.newCall(request).execute();
                    //根据服务器返回的response对象得到消息体body
                    String responseData=response.body().string();
                    //调用更新UI的方法
                    showResponse(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 使用HttpUrl访问网络
     * */
    private void useHttpUrl() {
        //访问网络需要开启子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader=null;
                HttpURLConnection conn=null;
                try {
                    URL url = new URL(urlstr);
                    //获得connection对象
                    conn = (HttpURLConnection) url.openConnection();
                    //设置请求方式
                    conn.setRequestMethod("GET");
                    //设置超时
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(8000);
                    //得到输入流
                    InputStream in = conn.getInputStream();
                    //BufferedWriter 和 BufferedReader 为带有默认缓冲的字符输出输入流
                    //InputStreamReader 将字节流转换为字符流。是字节流通向字符流的桥梁
                    reader = new BufferedReader(new InputStreamReader(in));
                    //字符缓冲区
                    StringBuffer buffer = new StringBuffer();
                    //读取一个文本行
                    String line = reader.readLine();
                    while(line != null){
                        buffer.append(line);
                        line = reader.readLine();
                    }
                    //把字符缓冲区里的内容转换为字符串
                    final String webContent = buffer.toString();
                    Message msg=new Message();
                    msg.obj=webContent;
                    msg.what=1;
                    //到主线程去解析数据，更新UI
                    mHandler.sendMessage(msg);


                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(reader!=null){
                        try {
                            //关闭字符输入流
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(conn!=null){
                        //关闭http连接
                        conn.disconnect();
                    }
                }
            }


        }).start();
    }



    /**
     *  更新UI的方法
     * */
    private void showResponse(final String webContent) {
        /**
         * 知识补充：
         * 更新UI采用Handle+Thread，需要发送消息，
         * 接受处理消息（在回调方法中处理），比较繁琐。
         * 除此之外，还可以使用runOnUiThread方法。
         * 利用Activity.runOnUiThread(Runnable)
         * 把更新ui的代码创建在Runnable中，
         * 然后在需要更新ui时，把这个Runnable对象传给Activity.
         * runOnUiThread(Runnable)。
         * */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //更新UI
                webText.setText(webContent);
            }
        });
    }

    /**
     * 检查网络是否可用
     *
     */
    private boolean isNetworkAvailable(Activity activity){
        Context context=activity.getApplicationContext();
        ConnectivityManager manager= (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (manager==null){
            return false;
        }else {
            // 获取所有NetworkInfo对象
            NetworkInfo[] networkinfos=manager.getAllNetworkInfo();
            if (networkinfos!=null&&networkinfos.length>0){
                for (int i=0;i<networkinfos.length;i++){
                    if (networkinfos[i].getState()==NetworkInfo.State.CONNECTED){
                        //存在可用的网络
                        return true;
                    }
                }
            }
            return false;
        }

    }


}
