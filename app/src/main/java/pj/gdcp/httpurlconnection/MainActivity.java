package pj.gdcp.httpurlconnection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    String urlstr = "https://www.baidu.com";
    private Button btn_load;
    private TextView webText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
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
                //useHttpUrl();
                useOkHttp();

            }
        });
    }

    /**
     * 使用OkHttp访问网络
     * */
    private void useOkHttp() {
        //首先要创建一个okhttpClient的实例
        OkHttpClient client=new OkHttpClient();
        //然后需要创建一个request对象，并通过

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
                    showResponse(webContent);

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
}
