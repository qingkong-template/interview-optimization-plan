package com.zr.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: emrinspection
 * @Date: 2018/12/26 14:02
 * @Author: Ss.Yan
 * @Description: Excel注解，用以生成Excel表格文件
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE})
/**
 * @Documented
 * @Inherited
 */
public @interface Excel {

    //列名
    String name() default "";

    //宽度
    //int width() default 20;

    //忽略该字段
    boolean isExport() default true;

    //序号
    String orderBy();
}
