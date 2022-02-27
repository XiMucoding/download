package com.lzk.core;

import com.lzk.conf.Conf;
import com.lzk.util.HTTPUtil;
import com.lzk.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/*
 * @Author lzk
 * @Email 1801290586@qq.com
 * @Description <callable分块下载任务类>
 **/
public class DownloadTask implements Callable<Boolean> {
    private String url;
    //下载的起始位置
    private long start;
    //下载的结束位置
    private long end;
    //当前下载的部分属于文件中的第几部分
    private int part;
    //线程减法计数器
    private CountDownLatch countDownLatch;
    public DownloadTask(String url,long start,long end,int part,CountDownLatch countDownLatch){
        this.url=url;
        this.start=start;
        this.end=end;
        this.part=part;
        this.countDownLatch=countDownLatch;
    }

    @Override
    public Boolean call() throws Exception {
        //下载的文件名
        String FileName= HTTPUtil.getFileName(url);
        //分块的文件名
        String httpFileName = FileName + ".temp" + part;
        //存储路径
        httpFileName = Conf.PATH+httpFileName;
        //打开的链接返回的对象
        HttpURLConnection httpURLConnection=HTTPUtil.getHttpURLConnection(url,start,end);

        try(
                //获取链接文件的流对象存进内存再读出到磁盘
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedInputStream bis=new BufferedInputStream(inputStream);
                RandomAccessFile accessFile=new RandomAccessFile(httpFileName,"rw");
        ){
            //读取和存储
            int len =-1;
            byte[] buff= new byte[Conf.BUFF_SIZE];
            while((len= bis.read(buff))!=-1){
                //一秒内下载的数据之和
                DownloadInfoThread.downSize.add(len);
                accessFile.write(buff,0,len);
            }
        }catch (FileNotFoundException e){
            LogUtil.error("{}处文件不存在",url);
            return false;
        }catch (Exception e){
            LogUtil.error("{}下载失败",FileName);
            return false;
        }finally {
            //关闭连接
            if (httpURLConnection!=null)
                httpURLConnection.disconnect();
            //线程减1
            countDownLatch.countDown();
        }
        return true;
    }
}
