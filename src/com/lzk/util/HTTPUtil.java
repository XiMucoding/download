package com.lzk.util;

import com.lzk.conf.Conf;

import java.net.HttpURLConnection;
import java.net.URL;

/*
 * @Author lzk
 * @Email 1801290586@qq.com
 * @Description <HTTPURL工具类>
 **/
public class HTTPUtil {

    /**
     * 获取文件大小
     * @param url
     * @return
     * @throws Exception
     */
    public static long getFileContentSize(String url) throws Exception {
        long contentLength=0l;
        HttpURLConnection httpURLConnection=null;
        try{
            httpURLConnection = getHttpURLConnection(url);
            contentLength = httpURLConnection.getContentLength();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (httpURLConnection!=null)
            {
                httpURLConnection.disconnect();
            }
        }
        return contentLength;
    }
    /**
     * 分块下载
     * @param url
     * @param start 起始位置
     * @param end 结束位置
     * @return
     */
    public static HttpURLConnection getHttpURLConnection(String url,long start,long end) throws Exception {
        //调用下面的方法获得链接对象
        HttpURLConnection httpURLConnection=getHttpURLConnection(url);
        LogUtil.info("线程的下载区间：{}-{}",start,end);
        if (end!=0){
            httpURLConnection.setRequestProperty("RANGE","bytes="+start+"-"+end);
        }else{
            //最后一段无需结束位置
            httpURLConnection.setRequestProperty("RANGE","bytes="+start+"-");
        }
        return httpURLConnection;
    }
    /**
     * 设置请求用户代理和打开链接并返回
     * @param url
     * @return
     * @throws Exception
     */
    public static HttpURLConnection getHttpURLConnection(String url) throws Exception {
        URL httpUrl = new URL(url);
        HttpURLConnection httpURLConnection=(HttpURLConnection)httpUrl.openConnection();
        //设置用户代理
        httpURLConnection.setRequestProperty("User-Agent",Conf.USER_AGENT);
        return httpURLConnection;
    }

    /**
     * 返回文件名
     * @param url
     * @return
     */
    public static String getFileName(String url){
        //获取/后的字符串当做文件名
        return url.substring(url.lastIndexOf("/")+1);
    }

}
