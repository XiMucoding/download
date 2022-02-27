package com.lzk.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/*
 * @Author lzk
 * @Email 1801290586@qq.com
 * @Description <日志输出工具类>
 **/
public class LogUtil {
    /**
     * 提示
     * @param msg
     * @param args
     */
    public static void info(String msg,Object...args){
        print(msg,"【info】",args);
    }

    /**
     * 报错
     * @param msg
     * @param args
     */
    public static void error(String msg,Object...args){
        print(msg,"【error】",args);
    }
    /**
     * 打印信息
     * @param msg
     * @param level
     * @param args
     */
    private static void print(String msg,String level,Object... args){
        if (args!=null && args.length>0){
            msg=String.format(msg.replace("{}","%s"),args);
        }
        String name = Thread.currentThread().getName();
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss"))+" "+ name + level + msg);
    }
}
