package com.zhuqiu.utils.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Calendar;

/**
 * @Author zhengqi
 * @Description
 * @Date 11/11/20 11:31 AM
 */
@Slf4j
public class ImageFileUtils {

    /**
     * 文件保存目录，物理路径
     */
    private static final String rootPath = "/home/zhuqiu/rpc_image/";

    private static final String allowSuffix = ".bmp.jpg.jpeg.png";

    public static String receiveFile(File file) {
        String fileName = file.getName();
        String fileUrl = saveLocal(fileName);

        try (FileInputStream in = new FileInputStream(file);
             FileOutputStream out = new FileOutputStream(new File(fileUrl))) {
            int len;    // 每次读取的字节长度
            byte[] bytes = new byte[1024];  // 存储每次读取的内容
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);   // 将读取的内容，写入到输出流当中
            }
        } catch (IOException e) {
            log.error("上传失败");
        }
        return fileUrl;
    }

    public static String receiveFile(MultipartFile file) {
        //文件的完整名称,如spring.jpeg
        String fileName = file.getOriginalFilename();

        String fileUrl = saveLocal(fileName);
        //将内存中的数据写入磁盘
        try {
            file.transferTo(new File(fileUrl));
        } catch (Exception e) {
            log.error("上传失败");
        }
        //完整的url
        return fileUrl;
    }

    private static String saveLocal(String fileName) throws IllegalArgumentException {
        // 1.文件后缀过滤，只允许部分后缀
        // 文件名,如spring
        String name = fileName.substring(0, fileName.indexOf("."));
        // 文件后缀,如.jpeg
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        if (allowSuffix.indexOf(suffix) == -1) {
            log.error("不允许上传该后缀的文件！ message:{} ", suffix);
        }
        // 2.创建文件目录
        // 创建年月文件夹
        Calendar date = Calendar.getInstance();
        File dateDirs = new File(date.get(Calendar.YEAR)
                + File.separator + (date.get(Calendar.MONTH) + 1));

        // 目标文件
        File localFile = new File(rootPath + File.separator + dateDirs + File.separator + fileName);
        int i = 1;
        // 若文件存在重命名
        String newFilename = fileName;
        while (localFile.exists()) {
            newFilename = name + "(" + i + ")" + suffix;
            String parentPath = localFile.getParent();
            localFile = new File(parentPath + File.separator + newFilename);
            i++;
        }
        // 判断目标文件所在的目录是否存在
        if (!localFile.getParentFile().exists()) {
            // 如果目标文件所在的目录不存在，则创建父目录
            localFile.getParentFile().mkdirs();
        }
        // "/home/zhuqiu/rpc_image/2020/11/filename.jpg"
        String fileUrl = rootPath + File.separator + dateDirs + File.separator + newFilename;
        return fileUrl;
    }
}
