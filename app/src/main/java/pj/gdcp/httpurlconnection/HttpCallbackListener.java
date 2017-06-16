package pj.gdcp.httpurlconnection;

/**
 * 作者：国富小哥
 * 日期：2017/6/16
 * Created by Administrator
 *回调接口
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
