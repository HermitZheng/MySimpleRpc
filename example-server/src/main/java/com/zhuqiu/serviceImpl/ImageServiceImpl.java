package com.zhuqiu.serviceImpl;

import com.zhuqiu.annotation.RpcService;
import com.zhuqiu.time.ImageService;
import com.zhuqiu.utils.file.ImageFileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author zhengqi
 * @Description Web(MultipartFile) -> Controller(File) -> RpcService(File)
 * @Date 11/11/20 2:16 PM
 */
@Slf4j
@RpcService
public class ImageServiceImpl implements ImageService {

    @Override
    public String recognizeImage(File file) {

        String fileUrl = ImageFileUtils.receiveFile(file);

        try {
            String[] args = new String[] {"python", "/home/PythonProject/imgRecognize.py", fileUrl};
            Process process = Runtime.getRuntime().exec(args);

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = in.readLine();

            in.close();
            process.destroy();

            return line;
        } catch (IOException e) {
            log.error("调用Python程序失败!");
        }
        return "";
    }
}
