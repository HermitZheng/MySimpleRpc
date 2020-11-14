package com.zhuqiu.rpcspringtest.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Calendar;

/**
 * @Author zhengqi
 * @Description
 * @Date 11/11/20 11:31 AM
 */
public class ImageFileUtils {

    /**
     * 文件保存目录，物理路径
     */
    private static final String rootPath = "/home/zhuqiu/rpc_image/";

    private static final String allowSuffix = ".bmp.jpg.jpeg.png";

    public static String receiveFile(MultipartFile file) {
        //1.文件后缀过滤，只允许部分后缀
        //文件的完整名称,如spring.jpeg
        String fileName = file.getOriginalFilename();
        //文件名,如spring
        String name = fileName.substring(0, fileName.indexOf("."));
        //文件后缀,如.jpeg
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        if (allowSuffix.indexOf(suffix) == -1) {
            return "不允许上传该后缀的文件！";
        }
        //2.创建文件目录
        //创建年月文件夹
        Calendar date = Calendar.getInstance();
        File dateDirs = new File(date.get(Calendar.YEAR)
                + File.separator + (date.get(Calendar.MONTH) + 1));

        //目标文件
        File descFile = new File(rootPath + File.separator + dateDirs + File.separator + fileName);
        int i = 1;
        //若文件存在重命名
        String newFilename = fileName;
        while (descFile.exists()) {
            newFilename = name + "(" + i + ")" + suffix;
            String parentPath = descFile.getParent();
            descFile = new File(parentPath + File.separator + newFilename);
            i++;
        }
        //判断目标文件所在的目录是否存在
        if (!descFile.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            descFile.getParentFile().mkdirs();
        }

        //3.存储文件
        //将内存中的数据写入磁盘
        try {
            file.transferTo(descFile);
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败";
        }
        //完整的url
        String fileUrl = File.separator + "uploads" + File.separator + dateDirs + File.separator + newFilename;

        return fileUrl;
    }
}
