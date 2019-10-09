package com.zr.service.impl;

import com.zr.mapper.UsersMapper;
import com.zr.pojo.Users;
import com.zr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author YAN
 * @program: iois
 * @Date: 2019/8/26 18:32
 * @Author: Ss.Yan
 * @Mail: a37work@aliyun.com
 * @Description:
 */
@Service
public class UserServiceImpl implements UserService {

 @Autowired
 private UsersMapper usersMapper;

 @Override
 public List<Users> selecetUserInfo(Users users) {

  List<Users>  usersList = usersMapper.selectUsersInfo(users);
  return usersList;
 }

}
