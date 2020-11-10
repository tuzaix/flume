package org.apache.flume.source.taildir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

public class TaildirPathRegex {
    private static final Logger logger = LoggerFactory.getLogger(TaildirPathRegex.class);

    /**
     * 判断是否包含正则的路径
     * [], {}, *, +
     */
    private boolean isContainRegex(String line) {
        if(line.contains("[") || line.contains("]") || line.contains("{") || line.contains("}") || line.contains("*") || line.contains("+") || line.contains("\\")) {
            return true;
        }
        return false;
    }

    /**
     * 根据正则获取相关的路径
     * @param filePattern
     * @return
     */
    public ArrayList<String> getRegexPaths(String filePattern) {
       String prePath = "";
       ArrayList<String> preDirPaths = new ArrayList<String>();
       for(String fp : filePattern.split("/")) {
           if(fp.equals("") && prePath.equals("")) {
               preDirPaths.add("/");
               continue;
           }
           ArrayList<String> currentDirFiles = null;
           if(this.isContainRegex(fp)) {
               // 包含正则表达的情况
               currentDirFiles = this.getListDir(preDirPaths, fp, true);
           } else {
               // 不包含正则表达的情况
               currentDirFiles = this.getListDir(preDirPaths, fp, false);
           }
           preDirPaths = currentDirFiles;
       }

        return preDirPaths;
    }

    /**
     * 获取对象
     * @param filePattern
     * @return
     */
    public ArrayList<File> getRegexFiles(String filePattern) {
        ArrayList<String> arrayList = this.getRegexPaths(filePattern);
        logger.info("regex for file count: " + arrayList.size());
        ArrayList<File> arrayList1 = new ArrayList<File>();
        for(Iterator<String> it = arrayList.iterator(); it.hasNext(); ) {
            String fileName = it.next();
            logger.info("file: " + fileName);
            arrayList1.add(new File(fileName));
        }
        return arrayList1;
    }

    /**
     * 遍历目录下的文件或目录
     * @param dirs
     * @param regex
     * @param isRegex
     * @return
     */
    private ArrayList<String> getListDir(ArrayList<String> dirs, String regex, boolean isRegex) {
        ArrayList<String> files = new ArrayList<String>();
        for(Iterator<String> it = dirs.iterator(); it.hasNext();) {
            String dir = it.next();
            String[] subFiles = new File(dir).list();

            for (String subFile : subFiles) {
                if (!isRegex) {
                    if (regex.equals(subFile)) {
                        String matchFile;
                        if (dir.equals("/")) {
                            matchFile = dir + subFile;
                        } else {
                            matchFile = dir + "/" + subFile;
                        }
                        files.add(matchFile);
                    }
                } else {
                    if (Pattern.matches(regex, subFile)) {
                        String matchFile;
                        if (dir.equals("/")) {
                            matchFile = dir + subFile;
                        } else {
                            matchFile = dir + "/" + subFile;
                        }
                        files.add(matchFile);
                    }
                }
            }
        }
        return files;
    }

    /**
     * 判断是否为目录
     * @param path
     * @return
     */
    private boolean isDir(String path) {
        File file = new File(path);
        if(file.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        String filePattern = "/tmp/swan-[\\d]+/swan[\\d]+/haha-[\\d]+";
        //String filePattern = "/tmp/swan-[\\d]+/swan[\\d]+";

        File file = new File(filePattern);
        System.out.println(file.getParent());
//        TaildirPathRegex tpr = new TaildirPathRegex();
//
//        for(Iterator<String> it = tpr.getRegexPaths(filePattern).iterator(); it.hasNext(); ) {
//            System.out.println(it.next());
//        }
    }
}



