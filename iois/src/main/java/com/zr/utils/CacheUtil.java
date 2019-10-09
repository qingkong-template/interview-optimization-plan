package com.zr.utils;


import org.springframework.stereotype.Component;

/**
 * google Guava Cache工具类
 *
 * @author jiangweiwei
 * @create 2018/11/22
 */
@Component
public class CacheUtil{

    /*private static Cache<String, List<Users>> User_CACHAE =
        CacheBuilder.newBuilder().maximumSize(5000).expireAfterWrite(3, TimeUnit.MINUTES).build();*/

    /**
     *
     * @param users
     * @return
     */
    /*public List<Users> getUsersDate(Users users) {

        final String key = users.getIdCard()+ "_" + users.getUserId() + "_" + DateFormatUtils.format(new Date(), "yyyyMMdd");
        List<Users> UsersList = User_CACHAE.getIfPresent(key);
        if (UsersList != null) {
            return UsersList;
        }

        *//*查出来的数据放到缓存中*//*
        User_CACHAE.put(key, UsersList);
        return UsersList;
    }*/

}