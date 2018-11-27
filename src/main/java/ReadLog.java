import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @program: net_360_wangwanqiang_Util
 * @description: 读取日志文件
 * @author: Mr.Wang
 * @create: 2018-11-27 09:50
 **/

public class ReadLog {
    public static void main(String[] args) {
        BufferedReader br = null;
        PrintWriter pw = null;
        String str = null;
        ArrayList result = new ArrayList();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\wangwanqiang\\Desktop\\log_root-vsys_20181127000148.txt"),"UTF-8"));
            //pw = new PrintWriter(new FileWriter("C:\\Users\\wangwanqiang\\Desktop\\syslog.log"),true);
            for(str = br.readLine(); str != null; str = br.readLine()){
                if(str.contains("sip=10.5.99.1 ")){
                    String []arr = str.split("c_name");
                    for(int i =0;i<arr.length;i++){
//                        if(arr[i].contains("sip=10.5.99.1 ")&&arr[i].contains("日志抓取策略")){
                        if(arr[i].contains("sip=10.5.99.1 ")){
//                            System.out.println(arr[i]);
                            result = subStr(arr[i],result);
                        }

                    }
                }else if(str.contains("sip=10.5.99.2 ")){
                    String []arr = str.split("c_name");
                    for(int i =0;i<arr.length;i++){
//                        if(arr[i].contains("sip=10.5.99.2 ")&&arr[i].contains("日志抓取策略")){
                        if(arr[i].contains("sip=10.5.99.2 ")){
//                            System.out.println(arr[i]);
                            result = subStr(arr[i],result);
                        }

                    }
                }else if(str.contains("sip=10.5.99.5 ")){
                    String []arr = str.split("c_name");
                    for(int i =0;i<arr.length;i++){
//                        if(arr[i].contains("sip=10.5.99.5 ")&&arr[i].contains("日志抓取策略")){
                        if(arr[i].contains("sip=10.5.99.5 ")){
//                            System.out.println(arr[i]);
                            result = subStr(arr[i],result);
                        }

                    }
                }else if(str.contains("sip=10.5.99.6 ")){
                    String []arr = str.split("c_name");
                    for(int i =0;i<arr.length;i++){
//                        if(arr[i].contains("sip=10.5.99.6 ")&&arr[i].contains("日志抓取策略")){
                        if(arr[i].contains("sip=10.5.99.6 ")){
//                            System.out.println(arr[i]);
                            result = subStr(arr[i],result);
                        }

                    }
                }else if(str.contains("sip=10.5.99.99 ")){
                    String []arr = str.split("c_name");
                    for(int i =0;i<arr.length;i++){
//                        if(arr[i].contains("sip=10.5.99.99 ")&&arr[i].contains("日志抓取策略")){
                        if(arr[i].contains("sip=10.5.99.99 ")){
//                            System.out.println(arr[i]);
                            result = subStr(arr[i],result);
                        }

                    }
                }
            }
            writeExcel(result,"C:\\Users\\wangwanqiang\\Desktop\\output.xlsx");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                //pw.flush();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static ArrayList subStr(String str,ArrayList result){
        ArrayList output = new ArrayList();
        if(str.indexOf("sip")>0){
            str = str.substring(str.indexOf("sip"),str.length());
//            System.out.printf(str.substring(0,str.indexOf(" "))+" ");
            output.add(str.substring(0,str.indexOf(" "))+" ");
        }
        if(str.indexOf("dip")>0){

            str = str.substring(str.indexOf("dip"),str.length());
//            System.out.printf(str.substring(0,str.indexOf(" "))+" ");
            output.add(str.substring(0,str.indexOf(" "))+" ");
        }
        if(str.indexOf("dport")>0){
            str = str.substring(str.indexOf("dport"),str.length());
            if("dport=0".equals(str.substring(0,str.indexOf(" ")))){

                return result;
            }
//            System.out.printf(str.substring(0,str.indexOf(" "))+" ");
            output.add(str.substring(0,str.indexOf(" "))+" ");
        }
        result.add(output);
//        System.out.println();
        return result;
    }
    public static void writeExcel(ArrayList<ArrayList> result, String path){
        /**
         *
         * 功能描述: 写入到Excel中
         *
         * @param: [result 解析出的数据, path excel在本地的位置]
         * @return: void
         * @auther:
         * @date: 2018/11/2 10:45
         */
        if(result == null){
            return;
        }
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("sheet1");
        for(int i = 0 ;i < result.size() ; i++){
            XSSFRow row = sheet.createRow(i);

            for(int j = 0; j < result.get(i).size() ; j ++){
                XSSFCell cell = row.createCell((short)j);
                cell.setCellValue(result.get(i).get(j).toString().split("=")[1]);

            }
//            for(int j=1;j<3;j++){
//                XSSFCell cell = row.createCell((short)j);
//                cell.setCellValue(result.get(i).get(j).toString().replace("[","").replace("]","").replace(",","\n"));
//            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try
        {
            wb.write(os);
        } catch (IOException e){
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        File file = new File(path);//Excel文件生成后存储的位置。
        OutputStream fos  = null;
        try
        {
            fos = new FileOutputStream(file);
            wb.write(fos);
            os.close();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
