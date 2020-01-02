package com.springboot.common.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author hangaoming
 * @Time 2019/12/31 14:27
 **/
@Component
public class SystemParams {
    private static String path;

    public static String getPath() {
        return path;
    }

    @Value("${system.path}")
    public void setPath(String path) {
        this.path = path;
    }
}
