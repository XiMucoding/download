package com.lzk.core;


import com.lzk.conf.Conf;
import com.lzk.util.HTTPUtil;
import com.lzk.util.LogUtil;


import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.concurrent.*;

/*
 * @Author lzk
 * @Email 1801290586@qq.com
 * @Description <下载文件>
 **/
public class Download {
    //打印下载信息的线程
    public ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    //线程池
    public ThreadPoolExecutor poolExecutor= new ThreadPoolExecutor(Conf.THREAD_NUM,
            Conf.THREAD_NUM,
            0,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(3),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );
    //减法计数器
    public CountDownLatch countDownLatch=new CountDownLatch(Conf.THREAD_NUM);
    /**
     * 下载链接文件
     * @param url
     */
    public void download(String url){
        //下载的文件名
        String FileName=HTTPUtil.getFileName(url);
        //存储路径
        String PathName = Conf.PATH+FileName;
        //获取该文件已经下载的大小
        File file = new File(PathName);
        long localhostSize=file.exists()&&file.isFile()?file.length():0;

        //打开的链接返回的对象
        HttpURLConnection httpURLConnection=null;
        //输出下载信息的线程
        DownloadInfoThread downloadInfoThread=null;

        try {
            //获取下载链接对象
             httpURLConnection= HTTPUtil.getHttpURLConnection(url);
            //获取将要下载的文件的总大小
            long fileSize=httpURLConnection.getContentLength();
            //判断是否下载过
            if(localhostSize>=fileSize){
                LogUtil.info("{}已下载,地址在{}",FileName,PathName);
                return;
            }

            //打印下载信息线程对象
            downloadInfoThread = new DownloadInfoThread(fileSize);
            //将任务交给线程执行，每隔1秒执行一次
            executorService.scheduleAtFixedRate(downloadInfoThread,1,1, TimeUnit.SECONDS);

            //切分任务
            ArrayList<Future> list = new ArrayList<>();
            spilt(url, list);

            //阻塞所有线程，等待所有线程结束再释放
            countDownLatch.await();

            //合并临时文件和清楚临时文件
            if(merge(PathName)){
                if (clearTemp(PathName)){
                    LogUtil.info("{}的临时文件删除完毕！",FileName);
                }else{
                    LogUtil.error("{}的临时文件删除出错，需手动删除！",FileName);
                }
            }

        }catch (FileNotFoundException e) {
            LogUtil.error("下载文件{}没有找到",url);
        }
        catch (Exception e) {
            LogUtil.error("{}下载失败",FileName);
        }finally {
            //关闭链接
            if (httpURLConnection!=null){
                httpURLConnection.disconnect();
            }
            //关闭executorService线程
            executorService.shutdownNow();
            //关闭线程池
            poolExecutor.shutdown();
        }
        System.out.println();
        System.out.println(FileName+"下载完毕!");
    }

    /**
     * 文件切分
     * @param url
     * @param futureList
     */
    public void spilt(String url, ArrayList<Future> futureList){
        try{
            //文件总大小
            long fileContentSize = HTTPUtil.getFileContentSize(url);
            //文件分块后大小
            long size=fileContentSize/Conf.THREAD_NUM;
            //计算分块个数
            for(int i=0;i<Conf.THREAD_NUM;i++){
                //下载的起始位置
                long start=i*size;
                //下载的结束位置
                long end;

                if (i==Conf.THREAD_NUM-1){
                    //下载最后一块，下载剩余的部分
                    end=0;
                }else{
                    end=start+size;
                }
                //如果不是第一块，起始位置要+1
                if(start!=0){
                    start++;
                }
                //创建任务对象
                DownloadTask downloadTask=new DownloadTask(url,start,end,i,countDownLatch);
                //将任务提交到线程池中
                Future<Boolean> future= poolExecutor.submit(downloadTask);
                futureList.add(future);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     *  文件合并
     * @param fileName
     * @return
     */
    public boolean merge(String fileName) throws IOException {
        System.out.print("\r");
        LogUtil.info("{}开始合并...",fileName);
        BufferedInputStream bis=null;
        try(
            RandomAccessFile accessFile=new RandomAccessFile(fileName,"rw");
        ){
            //读取和存储
            int len =-1;
            byte[] buff= new byte[Conf.BUFF_SIZE];
            for (int i = 0; i < Conf.THREAD_NUM ; i++){
                FileInputStream fileInputStream = new FileInputStream(fileName + ".temp" + i);
                bis = new BufferedInputStream(fileInputStream);
                while((len= bis.read(buff))!=-1){
                    accessFile.write(buff,0,len);
                }
                fileInputStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            bis.close();
        }
        System.out.print("\r");
        LogUtil.info("{}文件合并完毕",fileName);
        return true;
    }

    /**
     * 清楚临时文件
     * @param fileName
     * @return
     */
    public boolean clearTemp(String fileName) {
        try{
            for (int i = 0; i < Conf.THREAD_NUM; i++) {
                File file = new File(fileName + ".temp" + i);
                file.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
