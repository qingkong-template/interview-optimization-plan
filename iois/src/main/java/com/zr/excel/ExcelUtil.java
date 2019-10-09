package com.zr.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: emrinspection
 * @Date: 2018/12/26 11:15
 * @Author: Ss.Yan
 * @Description: 导出excel相关工具
 */
public class ExcelUtil<T> {

 private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

 /**
  * 对list数据源将其里面的数据导入到excel表单
  *
  * @param
  * @param
  * @return 结果
  */
 public static <T> void exportExcel(String sheetName,String title, String fileName, List<T> dataList, HttpServletResponse response) throws IOException {

  try {
   //生成一个工作簿
   HSSFWorkbook workbook = new HSSFWorkbook();
   //生成一个表格
   HSSFSheet sheet = workbook.createSheet((null == sheetName || "" == sheetName)? " " : sheetName);
   //创建标题第一行
   HSSFRow row = sheet.createRow(0);
   HSSFCell cellTiltle = row.createCell(0);
   //设置标题和单元格样式
   HSSFCellStyle columnTopStyle = getColumnTopStyle(workbook);  //获取列头样式对象
   HSSFCellStyle style = getStyle(workbook);                    //单元格样式对象

   // 定义所需列数
   HSSFRow rowRowName = sheet.createRow((null != title && " " != title && "" != title) ? 2 : 0);// 在索引2的位置创建行(最顶端的行开始的第二行)
   int nameLength = 0;
   //初始化随着列的长度自适应集合
   List<Integer> list = null;
   //是否有标题
   Boolean isHasTitle = false;
   Object obj = null;
   //判断数据库查出的数据是否为空。如果为空为了防止报错进行了处理。直接输出空的Excel
   if (null != dataList && !dataList.isEmpty()) {
    for (int i = 0; i < dataList.size(); i++) {
     //数据从第二行开始
     HSSFRow rowBatch = sheet.createRow(i + ((null != title && " " != title && "" != title) ? 3 : 1));
     obj = dataList.get(i);
     Field[] allFields = obj.getClass().getDeclaredFields();
     // 得到所有定义字段
     list = fieldMode(title,allFields, columnTopStyle, cellTiltle, sheet, isHasTitle, list, nameLength, rowRowName);

     for (Field field : allFields) {
      // 设置实体类私有属性可访问
      field.setAccessible(true);
      if (field.isAnnotationPresent(Excel.class)) {
       Excel columnConfig = field.getAnnotation(Excel.class);
       if (columnConfig.isExport()) {
        HSSFCell cell = rowBatch.createCell(Integer.valueOf(columnConfig.orderBy().toString()) - 1);
        cell.setCellValue(field.get(obj) == null ? "" : field.get(obj).toString());
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellStyle(style);
       }
      }
     }
    }
   } else {
    Field[] allFields = obj.getClass().getDeclaredFields();
    list = fieldMode(title,allFields, columnTopStyle, cellTiltle, sheet, isHasTitle, list, nameLength, rowRowName);
   }
   //让列宽随着导出的列长自动适应
   for (int colNum = 0; colNum < list.size(); colNum++) {
    int columnWidth = sheet.getColumnWidth(colNum) / 256;//表格的宽度
    for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
     HSSFRow currentRow;
     //当前行未被使用过
     if (sheet.getRow(rowNum) == null) {
      currentRow = sheet.createRow(rowNum);
     } else {
      currentRow = sheet.getRow(rowNum);
     }
     if (currentRow.getCell(colNum) != null) {
      //取得当前的单元格
      HSSFCell currentCell = currentRow.getCell(colNum);
      //如果当前单元格类型为字符串
      if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
       int length = currentCell.getStringCellValue().getBytes().length;
       if (columnWidth < length) {
        //将单元格里面值大小作为列宽度
        columnWidth = length;
       }
      }
     }
    }

    if (colNum == 0) {
     //如果单元格数据太大崩溃就改(columnWidth - 2) * 500
     sheet.setColumnWidth(colNum, (columnWidth - 2) * 500);//这里可以调试Excel第一列的表格宽度
    } else {
     //如果单元格数据太大崩溃就改columnWidth < 3000 ? 6000 : (columnWidth + 2) * 256
     sheet.setColumnWidth(colNum, columnWidth < 3000 ? 6000 : (columnWidth + 2) * 256);
    }
   }

   outPutExcel(workbook,response,fileName);
  } catch (Exception e) {
   e.printStackTrace();
  }
 }

 Class<T> clazz;

 public ExcelUtil(Class<T> clazz) {
  this.clazz = clazz;
 }

 public List importExcel(String fileName, String sheetName, InputStream input) {
  int maxCol = 0;
  List list = new ArrayList();
  try {
   Workbook workbook = null;
   String fileType = fileName.substring(fileName.lastIndexOf("."), fileName.length());
   if (".xls".equals(fileType.trim().toLowerCase())) {
    workbook = new HSSFWorkbook(input);// 创建 Excel 2003 工作簿对象
   } else if (".xlsx".equals(fileType.trim().toLowerCase())) {
    workbook = new XSSFWorkbook(input);//创建 Excel 2007 工作簿对象
   }//解决Excel导入的兼容2003和2007版本的问题
   Sheet sheet = workbook.getSheet(sheetName);
   if (!sheetName.trim().equals("")) {
    sheet = workbook.getSheet(sheetName);// 如果指定sheet名,则取指定sheet中的内容.
   }
   if (sheet == null) {
    sheet = workbook.getSheetAt(0); // 如果传入的sheet名不存在则默认指向第1个sheet.
   }
   int rows = sheet.getPhysicalNumberOfRows();

   if (rows > 0) {// 有数据时才处理
    // Field[] allFields = clazz.getDeclaredFields();// 得到类的所有field.
    List<Field> allFields = getMappedFiled(clazz, null);

    Map<Integer, Field> fieldsMap = new HashMap<Integer, Field>();// 定义一个map用于存放列的序号和field.
    for (Field field : allFields) {
     // 将有注解的field存放到map中.
     if (field.isAnnotationPresent(Excel.class)) {
      Excel attr = field.getAnnotation(Excel.class);
      int col = Integer.valueOf(attr.orderBy().toString());
      maxCol = Math.max(col, maxCol);
      System.out.println(col + "====" + field.getName());
      field.setAccessible(true);// 设置类的私有字段属性可访问.
      fieldsMap.put(col, field);
     }
    }
    for (int i = 1; i < rows; i++) {// 从第2行开始取数据,默认第一行是表头.
     Row row = sheet.getRow(i);
     // int cellNum = row.getPhysicalNumberOfCells();
     // int cellNum = row.getLastCellNum();
     int cellNum = maxCol;
     T entity = null;
     for (int j = 0; j < cellNum; j++) {
      Cell cell = row.getCell(j);
      if (cell == null) {
       continue;
      }
      int cellType = cell.getCellType();
      String c = "";
      if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
       c = String.valueOf(cell.getNumericCellValue());
      } else if (cellType == HSSFCell.CELL_TYPE_BOOLEAN) {
       c = String.valueOf(cell.getBooleanCellValue());
      } else {
       c = cell.getStringCellValue();
      }
      if (c == null || c.equals("")) {
       continue;
      }
      entity = (entity == null ? clazz.newInstance() : entity);// 如果不存在实例则新建.
      // System.out.println(cells[j].getContents());
      Field field = fieldsMap.get(j + 1);// 从map中得到对应列的field.
      if (field == null) {
       continue;
      }
      // 取得类型,并根据对象类型设置值.
      Class<?> fieldType = field.getType();
      if (String.class == fieldType) {
       field.set(entity, String.valueOf(c));
      } else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
       field.set(entity, Integer.parseInt(c));
      } else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
       field.set(entity, Long.valueOf(c));
      } else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
       field.set(entity, Float.valueOf(c));
      } else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
       field.set(entity, Short.valueOf(c));
      } else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
       field.set(entity, Double.valueOf(c));
      } else if (Character.TYPE == fieldType) {
       if ((c != null) && (c.length() > 0)) {
        field.set(entity, Character.valueOf(c.charAt(0)));
       }
      }

     }
     if (entity != null) {
      list.add(entity);
     }
    }
   }

  } catch (IOException e) {
   e.printStackTrace();
  } catch (InstantiationException e) {
   e.printStackTrace();
  } catch (IllegalAccessException e) {
   e.printStackTrace();
  } catch (IllegalArgumentException e) {
   e.printStackTrace();
  }
  return list;
 }

 /**
  * 得到实体类所有通过注解映射了数据表的字段
  *
  * @return
  */
 @SuppressWarnings("rawtypes")
 private List<Field> getMappedFiled(Class clazz, List<Field> fields) {
  if (fields == null) {
   fields = new ArrayList<Field>();
  }

  Field[] allFields = clazz.getDeclaredFields();// 得到所有定义字段
  // 得到所有field并存放到一个list中.
  for (Field field : allFields) {
   if (field.isAnnotationPresent(Excel.class)) {
    fields.add(field);
   }
  }
  if (clazz.getSuperclass() != null
   && !clazz.getSuperclass().equals(Object.class)) {
   getMappedFiled(clazz.getSuperclass(), fields);
  }

  return fields;
 }








 private static void cellTitle(String title, HSSFSheet sheet, int nameLength, HSSFCellStyle columnTopStyle, HSSFCell cellTiltle) {
  if (null != title && " " != title && "" != title) {
   //合并单元格
   sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, (nameLength - 1)));
   cellTiltle.setCellStyle(columnTopStyle);
   cellTiltle.setCellValue(title);
  }
 }

 private static List fieldMode(String title,Field[] allFields, HSSFCellStyle columnTopStyle, HSSFCell cellTiltle, HSSFSheet sheet, Boolean isHasTitle, List<Integer> list, int nameLength, HSSFRow rowRowName) {

  // 得到所有定义字段
  if (!isHasTitle) {
   list = new ArrayList<Integer>();
   for (Field field : allFields) {
    //使private方法可以被调用
    field.setAccessible(true);
    if (field.isAnnotationPresent(Excel.class)) {
     //获取字段注解
     Excel columnConfig = field.getAnnotation(Excel.class);
     if (columnConfig.isExport()) {
      //获取列
      HSSFCell cell = rowRowName.createCell(Integer.valueOf(columnConfig.orderBy().toString()) - 1);
      //设置名称
      cell.setCellValue(columnConfig.name().toString());
      cell.setCellType(HSSFCell.CELL_TYPE_STRING);                //设置列头单元格的数据类型
      cell.setCellStyle(columnTopStyle);                          //设置列头单元格样式

      nameLength++;

      list.add(nameLength);
     }
    }
   }
   isHasTitle = true;
   //合并单元格
   cellTitle(title, sheet, nameLength, columnTopStyle, cellTiltle);
  }
  return list;
 }

 /**
  * @param //[workbook]
  * @Description: //弹出保存框方式
  * @Return: void
  * @Author: Ss.Yan
  * @Date: 2018/12/28 14:21
  */
 private static void outPutExcel(HSSFWorkbook workbook,HttpServletResponse response,String fileName) throws Exception {
  ByteArrayOutputStream os = new ByteArrayOutputStream();
  try {
   workbook.write(os);
  } catch (IOException e) {
   e.printStackTrace();
  }
  byte[] content = os.toByteArray();
  InputStream is = new ByteArrayInputStream(content);
  // 设置response参数，可以打开下载页面
  response.reset();
  response.setContentType("application/vnd.ms-excel;charset=utf-8");
  response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "ISO8859-1") + ".xls");

  ServletOutputStream out = response.getOutputStream();
  BufferedInputStream bis = null;
  BufferedOutputStream bos = null;
  try {
   bis = new BufferedInputStream(is);
   bos = new BufferedOutputStream(out);
   byte[] buff = new byte[2048];
   int bytesRead;
   // Simple read/write loop.
   while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
    bos.write(buff, 0, bytesRead);
   }
  } catch (Exception e) {
   e.printStackTrace();
  } finally {
   if (bis != null) {
    bis.close();
   }
   if (bos != null) {
    bos.close();
   }
  }
 }

 /***
  * @Description: 标题行的单元格样式
  * @param workbook
  * @Return: org.apache.poi.hssf.usermodel.HSSFCellStyle
  * @Author: Ss.Yan
  * @Date: 2018/12/28 12:07
  */
 private static HSSFCellStyle getColumnTopStyle(HSSFWorkbook workbook) {

  // 设置字体
  HSSFFont font = workbook.createFont();
  //设置字体大小
  font.setFontHeightInPoints((short) 11);
  //字体加粗
  font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
  //设置字体名字
  font.setFontName("Courier New");
  //设置样式;
  HSSFCellStyle style = workbook.createCellStyle();
  style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
  //设置底边框;
  style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
  //设置底边框颜色;
  style.setBottomBorderColor(HSSFColor.BLACK.index);
  //设置左边框;
  style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
  //设置左边框颜色;
  style.setLeftBorderColor(HSSFColor.BLACK.index);
  //设置右边框;
  style.setBorderRight(HSSFCellStyle.BORDER_THIN);
  //设置右边框颜色;
  style.setRightBorderColor(HSSFColor.BLACK.index);
  //设置顶边框;
  style.setBorderTop(HSSFCellStyle.BORDER_THIN);
  //设置顶边框颜色;
  style.setTopBorderColor(HSSFColor.BLACK.index);
  //在样式用应用设置的字体;
  style.setFont(font);
  //设置自动换行;
  style.setWrapText(false);
  //设置水平对齐的样式为居中对齐;
  style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
  //设置垂直对齐的样式为居中对齐;
  style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
  return style;
 }

 /***
  * @Description: 列数据信息单元格样式
  * @param //[workbook]
  * @Return: org.apache.poi.hssf.usermodel.HSSFCellStyle
  * @Author: Ss.Yan
  * @Date: 2018/12/28 12:08
  */
 private static HSSFCellStyle getStyle(HSSFWorkbook workbook) {
  // 设置字体
  HSSFFont font = workbook.createFont();
  //设置字体大小
  //font.setFontHeightInPoints((short)10);
  //字体加粗
  //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
  //设置字体名字
  font.setFontName("Courier New");
  //设置样式;
  HSSFCellStyle style = workbook.createCellStyle();
  //设置底边框;
  style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
  //设置底边框颜色;
  style.setBottomBorderColor(HSSFColor.BLACK.index);
  //设置左边框;
  style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
  //设置左边框颜色;
  style.setLeftBorderColor(HSSFColor.BLACK.index);
  //设置右边框;
  style.setBorderRight(HSSFCellStyle.BORDER_THIN);
  //设置右边框颜色;
  style.setRightBorderColor(HSSFColor.BLACK.index);
  //设置顶边框;
  style.setBorderTop(HSSFCellStyle.BORDER_THIN);
  //设置顶边框颜色;
  style.setTopBorderColor(HSSFColor.BLACK.index);
  //在样式用应用设置的字体;
  style.setFont(font);
  //设置自动换行;
  style.setWrapText(false);
  //设置水平对齐的样式为居中对齐;
  style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
  //设置垂直对齐的样式为居中对齐;
  style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
  return style;
 }

}