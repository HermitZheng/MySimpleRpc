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

    /**
     * 新增 和 修改 相同
     */
    RpcResult saveOne(UserDTO user);

    RpcResult deleteById(Long id);

    RpcResult delete(UserDTO user);
}
