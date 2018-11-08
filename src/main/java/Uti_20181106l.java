import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: net_360_wangwanqiang_Util
 * @description: 20181106解析ExcelCode
 * @author: Mr.Wang
 * @create: 2018-11-06 16:18
 **/

public class Uti_20181106l {
    public static void main(String[] args) {
        for(int i=1;i<8;i++){
            readExcel(getFWInfo(i));
            System.out.println("-----------------------------------------------------------------------");
        }
    }
    public static void readExcel(Map ngfwInfo){
        /**
        * @Description:
        * @Param: [ngfwInfo]
        * @return: void
        * @Author: Wangwanqiang
        * @Date: 2018/11/6
        */
        //excel文件路径
        String excelPath = "C:\\Users\\wangwanqiang\\Desktop\\demo.xlsx";
        Map ngfw111_68 = new HashMap();
        //IPV4 24位子网地址的正则表达式
        String IPV4SubNetRegularExpression = "/^(25[0-5]|2[0-4]/d|[0-1]?/d?/d)(/.(25[0-5]|2[0-4]/d|[0-1]?/d?/d)){3}$/";
        Map objectMap = new HashMap<String,String>();

        try {
            //String encoding = "GBK";
            File excel = new File(excelPath);
            if (excel.isFile() && excel.exists()) {   //判断文件是否存在

                String[] split = excel.getName().split("\\.");  //.是特殊字符，需要转义！！！！！
                Workbook wb = null;
                //根据文件后缀（xls/xlsx）进行判断
                if ("xls".equals(split[1])) {
                    FileInputStream fis = new FileInputStream(excel);   //文件流对象
                    wb = new HSSFWorkbook(fis);
                } else if ("xlsx".equals(split[1])) {
                    wb = new XSSFWorkbook(excel);
                } else {
                    System.out.println("文件类型错误!");
                }
                //开始解析
                Sheet sheet = wb.getSheetAt(0);     //读取sheet 0

                int firstRowIndex = sheet.getFirstRowNum() + 3;   //前三行不读
                int lastRowIndex = sheet.getLastRowNum();
                System.out.println("firstRowIndex: " + firstRowIndex);
                System.out.println("lastRowIndex: " + lastRowIndex);
                for(int i=8;i<=sheet.getLastRowNum();i++){
                    Row row = sheet.getRow(i);
                    //object network address 20180119-海南销售-11.209.0.90
                    //network-object network 11.209.0.90 255.255.255.255
                    //exit
                    //如果源在防火墙里
                    if(ngfwInfo.containsKey(row.getCell(3).toString().trim().substring(0,row.getCell(3).toString().trim().lastIndexOf(".")))){
                        if(!objectMap.containsKey(row.getCell(3).toString().trim())){
                            objectMap.put(row.getCell(3).toString().trim(),"");
                            System.out.println("object network address "+row.getCell(2).toString()+"-"+row.getCell(3).toString().trim());
                            System.out.println("network-object network "+row.getCell(3).toString().trim()+" 255.255.255.255");
                            System.out.println("exit");
                        }else{
                        }
                        if(!objectMap.containsKey(row.getCell(5).toString().trim())){
                            objectMap.put(row.getCell(5).toString().trim(),"");
                            System.out.println("object network address "+row.getCell(4).toString()+"-"+row.getCell(5).toString().trim());
                            System.out.println("network-object network "+row.getCell(5).toString().trim()+" 255.255.255.255");
                            System.out.println("exit");
                        }else{
                        }

                    }else{
                        //如果目的在防火墙里
                        if(ngfwInfo.containsKey(row.getCell(5).toString().trim().substring(0,row.getCell(5).toString().trim().lastIndexOf(".")))){
                            if(!objectMap.containsKey(row.getCell(3).toString().trim())){
                                objectMap.put(row.getCell(3).toString().trim(),"");
                                System.out.println("object network address "+row.getCell(2).toString()+"-"+row.getCell(3).toString().trim());
                                System.out.println("network-object network "+row.getCell(3).toString().trim()+" 255.255.255.255");
                                System.out.println("exit");
                            }else{
                            }
                            if(!objectMap.containsKey(row.getCell(5).toString().trim())){
                                objectMap.put(row.getCell(5).toString().trim(),"");
                                System.out.println("object network address "+row.getCell(4).toString()+"-"+row.getCell(5).toString().trim());
                                System.out.println("network-object network "+row.getCell(5).toString().trim()+" 255.255.255.255");
                                System.out.println("exit");
                            }else{
                            }
                        }
                    }
                }


            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public static Map getFWInfo(int index) {
        /**
         *
         * 功能描述: 获取防火墙管理的地址
         *
         * @param: index 防火墙地址
         * @return: java.util.Map
         * @auther:
         * @date: 2018/11/2 10:48
         */
        //excel文件路径
        String excelPath = "C:\\Users\\wangwanqiang\\Desktop\\NGFW.xlsx";
        Map ngfwInfo = new HashMap();
        //IPV4 24位子网地址的正则表达式
        String IPV4SubNetRegularExpression = "/^(25[0-5]|2[0-4]/d|[0-1]?/d?/d)(/.(25[0-5]|2[0-4]/d|[0-1]?/d?/d)){3}$/";


        try {
            //String encoding = "GBK";
            File excel = new File(excelPath);
            if (excel.isFile() && excel.exists()) {   //判断文件是否存在

                String[] split = excel.getName().split("\\.");  //.是特殊字符，需要转义！！！！！
                Workbook wb;
                //根据文件后缀（xls/xlsx）进行判断
                if ("xls".equals(split[1])) {
                    FileInputStream fis = new FileInputStream(excel);   //文件流对象
                    wb = new HSSFWorkbook(fis);
                } else if ("xlsx".equals(split[1])) {
                    wb = new XSSFWorkbook(excel);
                } else {
                    System.out.println("文件类型错误!");
                    return null;
                }
                //开始解析
                Sheet sheet = wb.getSheetAt(0);     //读取sheet 0

                int firstRowIndex = sheet.getFirstRowNum() + 3;   //前三行不读
                int lastRowIndex = sheet.getLastRowNum();
                System.out.println("firstRowIndex: " + firstRowIndex);
                System.out.println("lastRowIndex: " + lastRowIndex);
                Row row = sheet.getRow(3);

                //第几列　即读取哪一个防火墙管理的地址范围
                Cell cell = row.getCell(index);
                for (String address : cell.toString().split("\n")) {

                    ngfwInfo.put(address.substring(0, address.lastIndexOf(".")),"");
                }
                return ngfwInfo;
            } else {
                System.out.println("找不到指定的文件");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Test
    public void getGroup(){
        String excelPath = "C:\\Users\\wangwanqiang\\Desktop\\demo.xlsx";
        Map ngfw111_68 = new HashMap();
        //IPV4 24位子网地址的正则表达式
        String IPV4SubNetRegularExpression = "/^(25[0-5]|2[0-4]/d|[0-1]?/d?/d)(/.(25[0-5]|2[0-4]/d|[0-1]?/d?/d)){3}$/";
        Map objectMap = new HashMap<String,String>();

        try {
            //String encoding = "GBK";
            File excel = new File(excelPath);
            if (excel.isFile() && excel.exists()) {   //判断文件是否存在

                String[] split = excel.getName().split("\\.");  //.是特殊字符，需要转义！！！！！
                Workbook wb = null;
                //根据文件后缀（xls/xlsx）进行判断
                if ("xls".equals(split[1])) {
                    FileInputStream fis = new FileInputStream(excel);   //文件流对象
                    wb = new HSSFWorkbook(fis);
                } else if ("xlsx".equals(split[1])) {
                    wb = new XSSFWorkbook(excel);
                } else {
                    System.out.println("文件类型错误!");
                }
                //开始解析
                Sheet sheet = wb.getSheetAt(0);     //读取sheet 0

                int firstRowIndex = sheet.getFirstRowNum() + 3;   //前三行不读
                int lastRowIndex = sheet.getLastRowNum();
                System.out.println("firstRowIndex: " + firstRowIndex);
                System.out.println("lastRowIndex: " + lastRowIndex);
                System.out.println("object network address-group hq和pr3组");
                for(int i=8;i<=36;i++){
                    Row row = sheet.getRow(i);
                    //object network  address-group 报税公网地址组
                    //group-object address 1-1广州报税
                    System.out.println("group-object address "+row.getCell(2).toString()+"-"+row.getCell(3).toString().trim());
                }
                System.out.println("exit");


            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
