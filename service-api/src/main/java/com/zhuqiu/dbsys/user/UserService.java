package com.zhuqiu.dbsys.user;

import com.zhuqiu.dto.RpcResult;

import java.util.List;

/**
 * @author zhuqiu
 * @date 2021/3/15
 */
public interface UserService {

    RpcResult<List<UserDTO>> findAllUser();

    RpcResult<List<UserDTO>> findAllByName(String name);

    RpcResult saveOne(UserDTO user);

    RpcResult updateOne(UserDTO user);

    RpcResult deleteById(Long id);

    RpcResult delete(UserDTO user);
}
