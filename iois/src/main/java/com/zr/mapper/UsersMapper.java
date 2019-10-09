package com.zr.mapper;

import com.zr.pojo.Users;
import com.zr.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UsersMapper extends MyMapper<Users> {
 List<Users> selectUsersInfo(@Param("users") Users users);
}