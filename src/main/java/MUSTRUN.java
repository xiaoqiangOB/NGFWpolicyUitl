import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class MUSTRUN {
    //IPV4正则表达式
    public static String IPV4RegularExpression = "(?<=(\\b|\\D))(((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))(?=(\\b|\\D))";
    public static void main(String[] args) {

        //excel文件路径
        String excelPath = "C:\\Users\\wangwanqiang\\Desktop\\test.xlsx";

        //IPV4 24位子网地址的正则表达式
        String IPV4SubNetRegularExpression = "/^(25[0-5]|2[0-4]/d|[0-1]?/d?/d)(/.(25[0-5]|2[0-4]/d|[0-1]?/d?/d)){3}$/";
        //解析出来地址和端口放在addressPort里
        HashMap addresssPortMap = new HashMap();
        //解析Excel的端口放在这里
        Set portSet = new HashSet();
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
                    System.out.println("rIndex: " + rIndex);
                    Row row = sheet.getRow(rIndex);
                    if (row != null) {
                        int firstCellIndex = row.getFirstCellNum();
                        int lastCellIndex = row.getLastCellNum();
                        //遍历第二列和第四列
//                        for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {   //遍历列
////                            Cell cell = row.getCell(cIndex);
////                            if (cell != null) {
////                                System.out.print(cell.toString());
////                            }
////                        }
                        //遍历第二列和第四列

                        Cell cell = row.getCell(1);
                        if(addresssPortMap.containsKey(row.getCell(1).toString())){

                        }else{
                            //IP + "_" + PORT
                            addresssPortMap.put(row.getCell(1).toString()+"_"+(int)Double.parseDouble(row.getCell(3).toString()),(int)Double.parseDouble(row.getCell(3).toString()));
                            portSet.add((int)Double.parseDouble(row.getCell(3).toString()));
                        }

                        System.out.println();
                    }
                }
                //将端口放入数据 去重后
                String[]  portArray = portSet.toString().replace("[","").replace("]","").split(",");
                //1、创建自定义服务
                autoService(portArray);

                //解析Excel数据后，开始拼接命令行
                for(String port:portArray){
                    //2、地址对象
                    autoObject(port,addresssPortMap);
                    //3、地址组
                    addressGroup.add(autoAddressGroup(port,addresssPortMap));
                }
                //4、安全策略
                autoPolicy(addressGroup);
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void autoService(String [] portArray){
        /**
         *
         * 功能描述: 生成自定义服务
         *
         * @param: [portArray 端口数组]
         * @return: void
         * @auther: wangwanqiang
         * @date: 2018/10/19 13:14
         */
        //1、创建自定义服务
        System.out.println("object service custom 20181017-服务器端口");

        for(String port:portArray){
            System.out.println("service-item tcp src-port  1 65535 dst-port "+port.trim()+" " +port.trim());
        }
        System.out.println("exit");
    }


    public static void autoObject(String port,HashMap addresssPortMap){
        /**
         *
         * 功能描述:生成地址对象
         *
         * @param: [port 端口, addresssPortMap 地址和端口集合]
         * @return: void
         * @auther:wangwanqiang
         * @date: 2018/10/19 13:13
         */
        //计数，有多少地址对象
        int count = 0;
        Iterator map1it = addresssPortMap.entrySet().iterator();
        while(map1it.hasNext())
        {
            Map.Entry<String, Integer> entry=(Map.Entry<String, Integer>) map1it.next();
            //根据端口分组遍历
            if(port.trim().equals(entry.getValue()+"")){
                if(entry.getKey().split("_")[0].trim().matches(IPV4RegularExpression)){
                    System.out.println("object network address 销售公司服务器-"+entry.getKey());
                    System.out.println("network-object network "+entry.getKey().split("_")[0]+" 255.255.255.255");
                    System.out.println("exit");
                    count++;
                }
            }
        }
    }

    public static String autoAddressGroup(String port,HashMap addresssPortMap){
        /**
         *
         * 功能描述:生成地址组命令,并将地址组对象放到数组里返回
         *
         * @param: [port port 端口, addresssPortMap 地址端口Map]
         * @return: java.util.String 返回地址组对象
         * @auther:wangwanqiang
         * @date: 2018/10/19 13:12
         */
        ArrayList addressGroup = new ArrayList();
        Iterator map1itGroup = addresssPortMap.entrySet().iterator();
        System.out.println("object network  address-group 销售公司服务器PT"+port.trim());
        while(map1itGroup.hasNext())
        {
            Map.Entry<String, Integer> entry=(Map.Entry<String, Integer>) map1itGroup.next();
            //根据端口分组遍历
            if(port.trim().equals(entry.getValue()+"")){
                if(entry.getKey().split("_")[0].trim().matches(IPV4RegularExpression)){
                    System.out.println("group-object address  销售公司服务器-"+entry.getKey().split("_")[0]);
                }
            }
        }
        System.out.println("exit");
        return "销售公司服务器PT"+port.trim();
    }

    public static void autoPolicy(List addressGroup){
        /**
         *
         * 功能描述:生成安全策略
         *
         * @param: [addressGroup 地址组对象集合]
         * @return: void
         * @auther: wangwanqiang
         * @date: 2018/10/19 13:11
         */
        for(int i=0;i<addressGroup.size();i++){
            System.out.println("security policy 堡垒机-"+addressGroup.get(i)+"  sip  销售公司堡垒机 dip "+addressGroup.get(i)+" szone any dzone any  service  20181017-服务器端口  action  permit  enable");
            System.out.println("security policy 堡垒机-"+addressGroup.get(i)+" top");
        }
    }
    @Test
    public  void testMap(){
        Map ipaddress = new HashMap();

        ipaddress.put("192.168.1.1","22");
        ipaddress.put("192.168.1.2","22");
        ipaddress.put("192.168.1.3","22");
        ipaddress.put("192.168.1.1","1521");

        Iterator map1it = ipaddress.entrySet().iterator();
        while(map1it.hasNext()){
            Map.Entry<String, Integer> entry=(Map.Entry<String, Integer>) map1it.next();

            System.out.println("Key = "+entry.getKey()+" Value = "+entry.getValue());
        }
    }

}
