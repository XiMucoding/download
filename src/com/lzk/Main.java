package com.lzk;

import com.lzk.conf.Conf;
import com.lzk.core.Download;
import com.lzk.util.LogUtil;

import java.io.File;
import java.util.Scanner;

/*
 * @Author lzk
 * @Email 1801290586@qq.com
 * @Description <类说明>
 **/
public class Main {
    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);
        String flag;
        while(true){
            System.gc();
            String url=null;
            //正则表达式
            String regex="[a-zA-z]+://[^\\s]*";
            while(url==null){
                LogUtil.info("请正确输入要下载的文件连接...");
                url = scanner.next();
                //正则判断是否为网络链接
                if(!url.matches(regex)){
                    //不是网络链接
                    LogUtil.error("输入的链接不正确！");
                    url=null;
                }
            }
            //下载
            Download download=new Download();
            download.download(url);
            System.out.println("是否继续下载？ y/n");
            flag=scanner.next();
            if (!"y".equalsIgnoreCase(flag))return;
        }
    }
}
