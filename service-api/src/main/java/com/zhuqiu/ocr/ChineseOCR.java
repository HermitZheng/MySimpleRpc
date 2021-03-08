package com.zhuqiu.ocr;

import com.zhuqiu.dto.RpcResult;

import java.io.File;

/**
 * @author zhuqiu
 * @date 2021/3/4
 */
public interface ChineseOCR {

    RpcResult<String> doOCR(String redisKey);
}
