package com.lzk.conf;

/*
 * @Author lzk
 * @Email 1801290586@qq.com
 * @Description <配置类>
 **/
public class Conf {
    //用户代理
    public static final String USER_AGENT="Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.82 Mobile Safari/537.36";
    //线程数
    public static final String PATH="E:/Downloads/";
    //线程数
    public static final int THREAD_NUM=Runtime.getRuntime().availableProcessors()-2;
    //MB
    public static final double MB=1024d*1024d;
    //桶大小
    public static final int BUFF_SIZE=1024*800;
}
