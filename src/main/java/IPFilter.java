
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
/**
 * @program: net_360_wangwanqiang_Util
 * @description: 过滤IP
 * @author: Mr.Wang
 * @create: 2018-11-02 21:03
 **/

public class IPFilter {

    public static void main(String[] args) {
        //excel文件路径
        String excelPath = "C:\\Users\\wangwanqiang\\Desktop\\test.xlsx";

        //IPV4 24位子网地址的正则表达式
        String IPV4SubNetRegularExpression = "/^(25[0-5]|2[0-4]/d|[0-1]?/d?/d)(/.(25[0-5]|2[0-4]/d|[0-1]?/d?/d)){3}$/";
        //解析出来地址和端口放在addressPort里
        HashMap addresssPortMap = new HashMap();
        //解析Excel的端口放在这里
        Set addressSet = new HashSet();
        //地址组放这里
        List addressGroup = new ArrayList();
        Integer count = 0;

        try {
            //String encoding = "GBK";
            File excel = new File(excelPath);
            if (excel.isFile() && excel.exists()) {   //判断文件是否存在

                String[] split = excel.getName().split("\\.");  //.是特殊字符，需要转义！！！！！
                Workbook wb;
                //根据文件后缀（xls/xlsx）进行判断
                if ( "xls".equals(split[1])){
                    FileInputStream fis = new FileInputStream(excel);   //文件流对象
                    wb = new HSSFWorkbook(fis);
                }else if ("xlsx".equals(split[1])){
                    wb = new XSSFWorkbook(excel);
                }else {
                    System.out.println("文件类型错误!");
                    return;
                }

                //开始解析
                Sheet sheet = wb.getSheetAt(0);     //读取sheet 0
                // TODO: 2018/10/24
                int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
                int lastRowIndex = sheet.getLastRowNum();
                System.out.println("firstRowIndex: "+firstRowIndex);
                System.out.println("lastRowIndex: "+lastRowIndex);

                for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
                    Row row = sheet.getRow(rIndex);
                    if (row != null) {
                        int firstCellIndex = row.getFirstCellNum();
                        int lastCellIndex = row.getLastCellNum();
                        Cell cell = row.getCell(0);
                        addressSet.add(row.getCell(0).toString());
                    }
                }
                Iterator iterator = addressSet.iterator();
                while(iterator.hasNext()){
                    System.out.println(iterator.next());
                    count++;
                }
                System.out.println("count = "+count);
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
