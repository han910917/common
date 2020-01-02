package com.springboot.common.utils;

import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @Description
 * @Author hangaoming
 * @Time 2019/12/31 14:35
 **/
public class FileUtil {

    /**
     * 文件地址如果不为空，进行清空处理
     * @author HanGaoMing
     * @Time 2019/12/31 14:39
     * @param path
     * @return java.lang.String
     */
    public static boolean deleteFile(String path){

        // 判断所传地址信息是否为空
        if(StringUtils.isEmpty(path)) throw new NullPointerException("索引文件地址可能为空,请检查所传值是否为空");

        File file = new File(path);
        // 索引文件为空的话，创建文件夹
        if( !file.exists() || null == file ){
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File[] files = file.listFiles();
        Arrays.stream(files).forEach( f -> f.deleteOnExit());
        return true;
    }
}
