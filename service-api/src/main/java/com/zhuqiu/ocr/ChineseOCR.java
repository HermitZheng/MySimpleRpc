package com.zhuqiu.ocr;

import com.zhuqiu.dto.RpcResult;

import java.util.HashMap;

/**
 * @author zhuqiu
 * @date 2021/3/4
 */
public interface ChineseOCR {

    RpcResult<String> doOCR(String redisKey);

    String testOCR(String redisKey);

    HashMap<String, String> mapOCR(String redisKey);
}
