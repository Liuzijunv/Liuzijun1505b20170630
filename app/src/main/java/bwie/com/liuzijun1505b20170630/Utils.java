package bwie.com.liuzijun1505b20170630;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * 类描述：
 * 创建人：Liuzijun
 * 创建时间：2017/6/30 14:56
 */
public class Utils {
    /**
     * HttpURLConnection的post请求
     * @param urlPath
     * @param map
     * @return
     */
    public static String postUrlConnect(String urlPath, Map<String,Object> map){
        StringBuffer sbRequest =new StringBuffer();
        if(map!=null&&map.size()>0){
            for (String key:map.keySet()){
                sbRequest.append(key+"="+map.get(key)+"&");
            }
        }
        String request = sbRequest.substring(0,sbRequest.length()-1);
        try {
            //创建URL
            URL url = new URL(urlPath);
            //由URL的openConnection方法得到一个HttpURLConnection（需要强转）
            HttpURLConnection httpURLConnection =
                    (HttpURLConnection) url.openConnection();
            //设置post提交
            httpURLConnection.setRequestMethod("POST");
            //设置超时时间
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.setReadTimeout(30000);

            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            //把请求正文通过OutputStream发出去
            OutputStream os =httpURLConnection.getOutputStream();
            os.write(request.getBytes());
            os.flush();

            //判断响应码  200 代表成功
            if(httpURLConnection.getResponseCode()==200){
                //由HttpURLConnection拿到输入流
                InputStream in=httpURLConnection.getInputStream();
                StringBuffer sb=new StringBuffer();
                //根据输入流做一些IO操作
                byte [] buff =new byte[1024];
                int len=-1;
                while((len=in.read(buff))!=-1){
                    sb.append(new String(buff,0,len,"utf-8"));
                }

                in.close();
                os.close();
                httpURLConnection.disconnect();
                return  sb.toString();
            }else{
                return null;
            }

        }catch (Exception e){
            Log.e("post","code:"+e.getMessage());
            return null;
        }
    }

    /**
     * HttpURLConnection的get请求
     * @param urlPath
     * @return
     */
    public static String getUrlConnect(String urlPath){

        try {
            //创建URL
            URL url = new URL(urlPath);
            //由URL的openConnection方法得到一个HttpURLConnection（需要强转）
            HttpURLConnection httpURLConnection =
                    (HttpURLConnection) url.openConnection();
            //设置连接
            httpURLConnection.connect();
            //判断响应码  200 代表成功
            if(httpURLConnection.getResponseCode()==200){
                //由HttpURLConnection拿到输入流
                InputStream in=httpURLConnection.getInputStream();
                StringBuffer sb=new StringBuffer();
                //根据输入流做一些IO操作
                byte [] buff =new byte[1024];
                int len=-1;
                while((len=in.read(buff))!=-1){
                    sb.append(new String(buff,0,len,"utf-8"));
                }
                in.close();
                httpURLConnection.disconnect();
                return  sb.toString();
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
