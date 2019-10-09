package com.zr.pojo;

import java.util.Date;
import javax.persistence.*;

public class Users {
    /**
     * 用户ID
     */
    @Id
    @Column(name = "user_id")
    private String userId;

    /**
     * 用户名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 年龄。默认根据生日计算。没有生日就手动填写
     */
    @Column(name = "user_age")
    private Integer userAge;

    /**
     * 生日，有身份证默认截取身份证。没有身份证就手动填写。
     */
    @Column(name = "user_birthday")
    private Date userBirthday;

    /**
     * 身份证，可以为空
     */
    @Column(name = "id_card")
    private String idCard;

    /**
     * 性别：1、男。2、女。
     */
    private Integer sex;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mailbox;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间（最终面试完成时间）
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 岗位：1.IOS。2.Java。3.安卓。4.C++。5.自动化测试。6.运维。7.前端。
     */
    private String post;

    /**
     * 获取用户ID
     *
     * @return user_id - 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取用户名
     *
     * @return user_name - 用户名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户名
     *
     * @param userName 用户名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取年龄。默认根据生日计算。没有生日就手动填写
     *
     * @return user_age - 年龄。默认根据生日计算。没有生日就手动填写
     */
    public Integer getUserAge() {
        return userAge;
    }

    /**
     * 设置年龄。默认根据生日计算。没有生日就手动填写
     *
     * @param userAge 年龄。默认根据生日计算。没有生日就手动填写
     */
    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }

    /**
     * 获取生日，有身份证默认截取身份证。没有身份证就手动填写。
     *
     * @return user_birthday - 生日，有身份证默认截取身份证。没有身份证就手动填写。
     */
    public Date getUserBirthday() {
        return userBirthday;
    }

    /**
     * 设置生日，有身份证默认截取身份证。没有身份证就手动填写。
     *
     * @param userBirthday 生日，有身份证默认截取身份证。没有身份证就手动填写。
     */
    public void setUserBirthday(Date userBirthday) {
        this.userBirthday = userBirthday;
    }

    /**
     * 获取身份证，可以为空
     *
     * @return id_card - 身份证，可以为空
     */
    public String getIdCard() {
        return idCard;
    }

    /**
     * 设置身份证，可以为空
     *
     * @param idCard 身份证，可以为空
     */
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    /**
     * 获取性别：1、男。2、女。
     *
     * @return sex - 性别：1、男。2、女。
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 设置性别：1、男。2、女。
     *
     * @param sex 性别：1、男。2、女。
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 获取电话
     *
     * @return phone - 电话
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置电话
     *
     * @param phone 电话
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取邮箱
     *
     * @return mailbox - 邮箱
     */
    public String getMailbox() {
        return mailbox;
    }

    /**
     * 设置邮箱
     *
     * @param mailbox 邮箱
     */
    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取修改时间（最终面试完成时间）
     *
     * @return update_time - 修改时间（最终面试完成时间）
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置修改时间（最终面试完成时间）
     *
     * @param updateTime 修改时间（最终面试完成时间）
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取岗位：1.IOS。2.Java。3.安卓。4.C++。5.自动化测试。6.运维。7.前端。
     *
     * @return post - 岗位：1.IOS。2.Java。3.安卓。4.C++。5.自动化测试。6.运维。7.前端。
     */
    public String getPost() {
        return post;
    }

    /**
     * 设置岗位：1.IOS。2.Java。3.安卓。4.C++。5.自动化测试。6.运维。7.前端。
     *
     * @param post 岗位：1.IOS。2.Java。3.安卓。4.C++。5.自动化测试。6.运维。7.前端。
     */
    public void setPost(String post) {
        this.post = post;
    }
}