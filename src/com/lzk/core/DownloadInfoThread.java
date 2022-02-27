package com.lzk.core;

import com.lzk.conf.Conf;

import java.util.concurrent.atomic.LongAdder;

/*
 * @Author lzk
 * @Email 1801290586@qq.com
 * @Description <文件下载信息线程>
 **/
public class DownloadInfoThread implements Runnable {
    //下载文件的总大小
    private long httpFileContentSize;
    //本地已下载的文件大小
    public static LongAdder finishSize=new LongAdder();
    //本次累计下载的文件大小
    public static volatile LongAdder downSize=new LongAdder();
    //前一次下载的大小
    private double prevSize;

    public DownloadInfoThread(long httpFileContentSize){
        this.httpFileContentSize=httpFileContentSize;
    }

    @Override
    public void run() {
        //计算文件总大小/MB
        String httpFileSize=String.format("%.2f",httpFileContentSize/ Conf.MB);

        //计算每秒下载速度 /kb/s
        int speed = (int) ((downSize.doubleValue() - prevSize) / 1024d);
        prevSize=downSize.doubleValue();

        //文件剩余的大小
        double remainSize =httpFileContentSize-finishSize.doubleValue()-downSize.doubleValue();
        //计算下载完毕所需的剩余时间
        String remainTime=String.format("%.1f",remainSize/1024d/speed);
        //若是剩余时间太大则转换为"-"
        if ("Infinity".equalsIgnoreCase(remainTime)){
            remainTime="-";
        }
        //已经下载的大小
        String currentFileSize=String.format("%.2f",(downSize.doubleValue()-finishSize.doubleValue())/Conf.MB);

        String downInfo=String.format("已下载 %smb/%smb,速度 %skb/s,剩余时间 %ss",currentFileSize,httpFileSize,speed,remainTime);

        System.out.print("\r");
        System.out.print(downInfo);
    }
}
