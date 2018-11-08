import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;
import java.util.*;

/**
 * @program: net_360_wangwanqiang_Util
 * @description: 解析CVS文件
 * @author: Mr.Wang
 * @create: 2018-11-08 11:21
 **/

public class CVSReader {
    public static void main(String[] args) {
        test(getFileName());
    }
    @Test
    public static void test(ArrayList files) {
        try {

            File csv = new File("C:\\Users\\wangwanqiang\\Desktop\\write.csv"); // CSV数据文件
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv),"GB2312"));
            for(int i=0;i<files.size();i++){
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files.get(i).toString()),"gb2312"));//换成你的文件名
                reader.readLine();//第一行信息，为标题信息，不用,如果需要，注释掉
                System.out.println(files.get(i));


                String line = null;
                while((line=reader.readLine())!=null) {


                    String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                    if ("611244788".equals(item[4].substring(0, item[4].lastIndexOf(".")))) {
                        //System.out.println(Arrays.toString(item));
                        bw.write(line);
                        bw.newLine();
                    }
                }
            }
            bw.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    @Test
    public static ArrayList getFileName(){
        /**
        * @Description:  读取指定文件夹下的文件
        * @Param: []
        * @return: void
        * @Author: Wangwanqiang
        * @Date: 2018/11/8
        */
        String path = "C:\\Users\\wangwanqiang\\Desktop\\traffic_log";
        ArrayList<String> files = new ArrayList<String>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
              //System.out.println("文     件：" + tempList[i]);
                files.add(tempList[i].toString());
            }
            if (tempList[i].isDirectory()) {
              System.out.println("文件夹：" + tempList[i]);
            }
        }
        return files;
    }
}
