package com.bsn.utils;

import java.io.File;

/**
 * @author wxq
 * @create 2023/2/14 17:15
 * @description
 */
public class ResourcesUtil {
    public static final String resourcesPath = ResourcesUtil.class.getResource("/").getPath() + File.separator;

    public static String fileName(String filename) {
        return ResourcesUtil.class.getResource(filename).getPath();
    }
}
