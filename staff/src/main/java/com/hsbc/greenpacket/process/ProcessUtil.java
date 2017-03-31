package com.hsbc.greenpacket.process;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.hsbc.greenpacket.util.FileUtil;
import com.hsbc.greenpacket.util.StringUtil;

public class ProcessUtil {
    private static final String TAG = "ProcessUtil";

    public static String getFileNameByURL(String url) {
        if (url != null) {
            int position = url.lastIndexOf(File.separator);
            if (position != -1) {
                return url.substring(position + 1);
            }
        }
        return null;
    }

    public static boolean isExists(String path, String fileName) {
        String pathName = path + File.separator + fileName;
        File file = new File(pathName);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isAllExists(String path, List<String> fileList) {
        for (int i = 0; i < fileList.size(); i++) {
            String fileName = fileList.get(i);
            if (!isExists(path, fileName)) {
                return false;
            }
        }
        return true;
    }


   

    

    public static String getPathName(String resourceDir, String fileName) {
        StringBuffer pathName = new StringBuffer(resourceDir);
        return pathName.append(File.separator).append(fileName).toString();
    }

    public static List<String> isExist(String folder, String type) {
        File f = new File(folder);
        String[] fileList = f.list();
        List<String> nameList = new ArrayList<String>();
        if (fileList != null) {
            for (String str : fileList) {
                File file = new File(str);
                if (file.getName().endsWith(type)) {
                    nameList.add(file.getName());
                }
            }
        }
        return nameList;
    }

    public static void deleteAll(String folder, List<String> fileNameByURLList) {
        File f = new File(folder);
        String[] fileList = f.list();
        if (fileList != null) {
            for (String str : fileList) {
                if (!fileNameByURLList.contains(str)) {
                    File file = new File(folder + File.separator + str);
                    file.delete();
                }
            }
        }
    }

    public static String localUrlIntercept(String url, String webResourcePath) {

        if (!StringUtil.IsNullOrEmpty(url) && !StringUtil.IsNullOrEmpty(webResourcePath)) {
            String lowCaseURL = url.toLowerCase();
            if (lowCaseURL.startsWith(ProcessConstants.HTTP) || lowCaseURL.startsWith(ProcessConstants.HTTPS)
                || lowCaseURL.startsWith(ProcessConstants.FILE_DATA)) {
                return url;
            } else if (lowCaseURL.startsWith(ProcessConstants.FILE)) {
                String path = url.substring(ProcessConstants.FILE.length());
                StringBuffer urlSbu = new StringBuffer(ProcessConstants.FILE);
                urlSbu.append(webResourcePath).append(path);
                return urlSbu.toString();
            }
        }
        return url;
    }

}
