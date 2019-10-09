package com.zr.api;

import com.zr.excel.ExcelUtil;
import com.zr.pojo.Users;
import com.zr.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * @author YAN
 * @program: iois
 * @Date: 2019/8/26 18:43
 * @Author: Ss.Yan
 * @Mail: a37work@aliyun.com
 * @Description:
 */
@Controller
@RequestMapping("/api")
public class ExportController {

 @Autowired
 private UserService userService;



 @GetMapping("/index")
 public String getIndex(){

  return "index";
 }

 @GetMapping("/export")
 public void exportUserInterviewFeedback(Users users, HttpServletResponse response) {

  /*通过时间区间统计出来我需要的数据通过Excel导出模板*/
  users.setUserName("你是谁");
  List<Users> usersList = null;
  try {
   usersList = userService.selecetUserInfo(users);
   ExcelUtil.exportExcel("sheet名","21312", "文件名", usersList, response);
  }catch (Exception e){
   e.printStackTrace();
  }

 }
 @PostMapping("/importExcel")
 @ResponseBody
 public String importExcel(@RequestParam(value = "excel") MultipartFile excel){

  try {
   ExcelUtil<Users> util = new ExcelUtil<Users>(Users.class);// 创建excel工具类
   List<Users> list = util.importExcel(excel.getOriginalFilename(),"sheet名", excel.getInputStream());// 导出

   /* if(list.size() > 0){
if(userService.batchUpdateImportUser(list) > 0){
     return "success";
    }

   }*/
  } catch (Exception e) {
   e.printStackTrace();
  }

  return "error";
 }


}
