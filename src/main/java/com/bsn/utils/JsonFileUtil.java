package com.bsn.utils;



import com.bsn.model.OpbWalletFile;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 *
 * @Author
 * @Created by 2019-11-11 10:37
 */
public class JsonFileUtil {


    public static OpbWalletFile readJsonFile(File file) throws IOException {
        String input = FileUtils.readFileToString(file, "UTF-8");

        OpbWalletFile opbWalletFile = JsonUtils.jsonToPojo(input, OpbWalletFile.class);

        return opbWalletFile;
    }


    public static OpbWalletFile readJsonFile(String filePath) throws IOException {

        return readJsonFile(new File(filePath));
    }


}
