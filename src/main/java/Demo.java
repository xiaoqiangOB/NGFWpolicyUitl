import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;
import java.util.*;
/**
 * @program: net_360_wangwanqiang_Util
 * @description:
 * @author: Mr.Wang
 * @create: 2018-11-02 21:00
 **/

public class Demo {

    private static int serviceCount = 0;
    private static int policyCount = 0;
    private static int policyEqualCount = 1;
    public static void main(String[] args) {
        //excel文件路径
        String excelPath = "C:\\Users\\wangwanqiang\\Desktop\\test2.xlsx";

        //防火墙地址信息
        // TODO: 2018/10/31
        Map ngfw111_58 = getFWInfo();
        Node addressNodeList = new Node();
        Node noInAnyFwNodeList = new Node();
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
                int firstRowIndex = sheet.getFirstRowNum();   //第一行是列名，所以不读
                int lastRowIndex = sheet.getLastRowNum();
                System.out.println("firstRowIndex: "+firstRowIndex);
                System.out.println("lastRowIndex: "+lastRowIndex);

                for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
                    Row row = sheet.getRow(rIndex);
                    if (row != null) {
                        //存放一行记录
                        Node node = new Node();
                        Cell cell = row.getCell(2);
                        node.setSourceDescription(cell.toString().replaceAll(" ",""));

                        node.setDestDescription(row.getCell(4).toString().replaceAll(" ",""));


                        node.setPort(row.getCell(6).toString().split("："));
                        node.setDescription(row.getCell(7).toString().replaceAll(" ",""));
                        List sourceIPArray = new ArrayList();
                        List destIPArray = new ArrayList();
                        boolean flag = false;
                        for(String sourceIp:row.getCell(3).toString().split("\n")){
                            //System.out.println("IP = "+sourceIp);
                            if(!"".equals(sourceIp)){
                                String Ip = sourceIp.substring(0,sourceIp.lastIndexOf("."));
                                if(ngfw111_58.containsKey(sourceIp.substring(0,sourceIp.lastIndexOf(".")))){
                                    sourceIPArray.add(sourceIp);
                                    flag = true;
                                }
                            }
                        }
                        if(flag){
                            node.setSourceIp(sourceIPArray.toString().split("\\[")[1].split("]")[0].split(","));
                            node.setDest(row.getCell(5).toString().split("\n"));
                            addressNodeList.addNode(node);
                            count++;
                        }else{
                            //如果源不在墙里，则看目的地址是否在墙里
                            for(String destIp:row.getCell(5).toString().split("\n")){
                                //System.out.println("IP = "+destIp);
                                if(!"".equals(destIp)){
                                    String Ip = destIp.substring(0,destIp.lastIndexOf("."));
                                    if(ngfw111_58.containsKey(destIp.substring(0,destIp.lastIndexOf(".")))){
                                        destIPArray.add(destIp);
                                        flag = true;
                                    }
                                }
                            }
                            if(flag){
                                node.setDest(destIPArray.toString().split("\\[")[1].split("]")[0].split(","));
                                node.setSourceIp(row.getCell(3).toString().split("\n"));
                                addressNodeList.addNode(node);
                                count++;
                            }else{
                                node.setSourceIp(row.getCell(3).toString().split("\n"));
                                node.setDest(row.getCell(5).toString().split("\n"));
                                noInAnyFwNodeList.addNode(node);
                            }
                        }
                    }
                }
//                addressNodeList.printNode();
                //noInAnyFwNodeList.printNode();
//                ArrayList<ArrayList> result = getResult(addressNodeList);
                ArrayList<ArrayList> result = getResult(noInAnyFwNodeList);
                //writeExcel(result,"C:\\Users\\wangwanqiang\\Desktop\\demo5.xlsx");
                autoCommandLine(addressNodeList);
                System.out.println("policyEqulaCount = "+policyEqualCount);
                System.out.println("count = "+count);
                System.out.println("policyCount = "+policyCount);
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map getFWInfo() {
        /**
         *
         * 功能描述: 获取防火墙管理的地址
         *
         * @param: []
         * @return: java.util.Map
         * @auther:
         * @date: 2018/11/2 10:48
         */
        //excel文件路径
        String excelPath = "C:\\Users\\wangwanqiang\\Desktop\\NGFW.xlsx";
        Map ngfw111_68 = new HashMap();
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
//                for(int i=1;i<8;i++){
//                    Cell cell = row.getCell(1);
//                    for (String address : cell.toString().split("\n")) {
//                        System.out.println(address);
//                        ngfw111_68.put(address.substring(0, address.lastIndexOf(".")),"");
//                    }
//                }
                //第几列　即读取哪一个防火墙管理的地址范围
                Cell cell = row.getCell(3);
                for (String address : cell.toString().split("\n")) {
                    System.out.println(address);
                    ngfw111_68.put(address.substring(0, address.lastIndexOf(".")),"");
                }
                return ngfw111_68;
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
    public  void test() {
        //excel文件路径
        String excelPath = "C:\\Users\\wangwanqiang\\Desktop\\NGFW.xlsx";
        List fw111_68 = new ArrayList();
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
                    return;
                }
                //开始解析
                Sheet sheet = wb.getSheetAt(0);     //读取sheet 0
                int firstRowIndex = sheet.getFirstRowNum() + 3;   //前三行不读
                int lastRowIndex = sheet.getLastRowNum();
                System.out.println("firstRowIndex: " + firstRowIndex);
                System.out.println("lastRowIndex: " + lastRowIndex);
                Row row = sheet.getRow(3);
                Cell cell = row.getCell(1);
                for (String address : cell.toString().split("\n")) {
                    System.out.println(address);
                    fw111_68.add(address.substring(0, address.lastIndexOf(".")));
                }

                System.out.println(fw111_68.size());
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void run(){
        List list = new ArrayList();
        list.add("111");
        list.add("222");
        list.add("333");
        System.out.println(list.toString());
        String string = "中国石化油田大集中ERP系统10.246.108.36toSAProuter10.246.166.139";
        System.out.println(string.substring(string.getBytes().length-63,string.length()));

    }
    public static void writeExcel(ArrayList<ArrayList> result,String path){
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
        for(int i = 1 ;i < result.size() ; i++){
            XSSFRow row = sheet.createRow(i);
            for(int j = 0; j < 6 ; j +=2){
                XSSFCell cell = row.createCell((short)j);
                cell.setCellValue(result.get(i).get(j).toString());

            }
            for(int j=1;j<6;j+=2){
                XSSFCell cell = row.createCell((short)j);
                cell.setCellValue(result.get(i).get(j).toString().replace("[","").replace("]","").replace(",","\n"));
            }
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
    public static ArrayList<ArrayList> getResult(Node addressNode) {
        /**
         *
         * 功能描述: 解析出的数据由链表结构转换为ArrayLists数据
         *
         * @param: [addressNode 链表结构的数据]
         * @return: java.util.ArrayList<java.util.ArrayList>
         * @auther:
         * @date: 2018/11/2 10:47
         */
        ArrayList nodeList = new ArrayList();
        while(addressNode!=null){
            ArrayList node = new ArrayList();
            node.add(addressNode.getSourceDescription());
            node.add(Arrays.toString(addressNode.getSourceIp()));
            node.add(addressNode.getDestDescription());
            node.add(Arrays.toString(addressNode.getDest()));
            node.add(addressNode.getDescription());
            node.add(Arrays.toString(addressNode.getPort()));
            nodeList.add(node);
            addressNode = addressNode.next;
        }
        return nodeList;
    }
    public static void autoCommandLine(Node addressNode){
        /**
         *
         * 功能描述: 转换成命令行
         *
         * @param: [addressNode 防火墙五元组信息]
         * @return: void
         * @auther:
         * @date: 2018/11/2 10:49
         */
        int count = 0;
        Map objectMap = new HashMap();
        Map portMap = new HashMap();
        Map policyMap = new HashMap();
        addressNode = addressNode.next;
        while(addressNode!=null){
            //count++;
            //System.out.println("count = "+(++count));
            // TODO: 2018/11/2

            //源地址
            if(addressNode.getSourceIp().length>1){
                String group = "object network address-group "+addressNode.getSourceDescription()+"\n";
                String object = "";
                for(int i=0;i<addressNode.getSourceIp().length;i++){
                    addressNode.getSourceIp()[i] = addressNode.getSourceIp()[i].trim();
                    //object network address  BODS系统-10.5.86.203
                    //network-object network  10.5.86.203  network-object network
                    //exit
                    group += "group-object address "+addressNode.getSourceDescription()+"-"+addressNode.getSourceIp()[i]+"\n";
                    if(objectMap.containsKey("object network address "+addressNode.getSourceDescription()+"-"+addressNode.getSourceIp()[i])){
                    }else{
                        objectMap.put("object network address "+addressNode.getSourceDescription()+"-"+addressNode.getSourceIp()[i],"");
                        object +="object network address "+addressNode.getSourceDescription()+"-"+addressNode.getSourceIp()[i]+"\n";
                        if(addressNode.getSourceIp()[i].contains("-")){
                            object += "network-object range "+addressNode.getSourceIp()[i].split("-")[0]+"-"+addressNode.getSourceIp()[i].split("-")[0].substring(0,addressNode.getSourceIp()[i].split("-")[0].lastIndexOf("."))+"."+addressNode.getSourceIp()[i].split("-")[1]+"\n";
                        }else{
                            object +="network-object network "+addressNode.getSourceIp()[i]+" 255.255.255.255\n";
                        }
                        object +="exit\n";
                    }
                }
                group += "exit\n";
                System.out.println(object+group);
            }else{
                addressNode.getSourceIp()[0] = addressNode.getSourceIp()[0].trim();
                //SAProuter
                if("10.246.166.139".equals(addressNode.getSourceIp()[0].trim())){
                    if(objectMap.containsKey("object network address "+addressNode.getSourceDescription())){

                    }else {
                        objectMap.put("object network address " + addressNode.getSourceDescription(), "");
                        System.out.println("object network address " + addressNode.getSourceDescription());
                        if(addressNode.getSourceIp()[0].contains("-")){
                            System.out.println("network-object range "+addressNode.getSourceIp()[0].split("-")[0]+"-"+addressNode.getSourceIp()[0].split("-")[0].substring(0,addressNode.getSourceIp()[0].split("-")[0].lastIndexOf("."))+"."+addressNode.getSourceIp()[0].split("-")[1]);
                        }else{

                            System.out.println("network-object network " + addressNode.getSourceIp()[0]+" 255.255.255.255" );
                        }
                        System.out.println("exit");
                    }
                }else{
                    if(objectMap.containsKey("object network address "+addressNode.getSourceDescription()+"-"+addressNode.getSourceIp()[0])){

                    }else{
                        objectMap.put("object network address "+addressNode.getSourceDescription()+"-"+addressNode.getSourceIp()[0],"");
                        System.out.println("object network address "+addressNode.getSourceDescription()+"-"+addressNode.getSourceIp()[0]);
                        if(addressNode.getSourceIp()[0].contains("-")){
                            System.out.println("network-object range "+addressNode.getSourceIp()[0].split("-")[0]+"-"+addressNode.getSourceIp()[0].split("-")[0].substring(0,addressNode.getSourceIp()[0].split("-")[0].lastIndexOf("."))+"."+addressNode.getSourceIp()[0].split("-")[1]);
                        }else{

                            System.out.println("network-object network "+addressNode.getSourceIp()[0] +"  255.255.255.255");
                        }
                        System.out.println("exit");
                    }
                }
            }
            //目的地址
            if(addressNode.getDest().length>1){
                String group = "object network address-group "+addressNode.getDestDescription()+"\n";
                String object = "";
                for(int i=0;i<addressNode.getDest().length;i++){
                    //object network address  BODS系统-10.5.86.203
                    //network-object network  10.5.86.203  network-object network
                    //exit
                    addressNode.getDest()[i] = addressNode.getDest()[i].trim();
                    group += "group-object address "+addressNode.getDestDescription()+"-"+addressNode.getDest()[i]+"\n";
                    if(objectMap.containsKey("object network address "+addressNode.getDestDescription()+"-"+addressNode.getDest()[i])){
                    }else{
                        objectMap.put("object network address "+addressNode.getDestDescription()+"-"+addressNode.getDest()[i],"");
                        object +="object network address "+addressNode.getDestDescription()+"-"+addressNode.getDest()[i]+"\n";
                        if(addressNode.getDest()[0].contains("-")){
                            System.out.println("network-object range "+addressNode.getDest()[0].split("-")[0]+"-"+addressNode.getDest()[0].split("-")[0].substring(0,addressNode.getDest()[0].split("-")[0].lastIndexOf("."))+"."+addressNode.getDest()[0].split("-")[1]+"\n");
                        }else{

                            object +="network-object network "+addressNode.getDest()[i]+"  255.255.255.255\n";
                        }
                        object +="exit\n";
                    }
                }
                group += "exit\n";
                System.out.println(object+group);
            }else{
                addressNode.getDest()[0] = addressNode.getDest()[0].trim();
                if("10.246.166.139".equals(addressNode.getDest()[0].trim())){
                    if(objectMap.containsKey("object network address "+addressNode.getDestDescription())){

                    }else {
                        objectMap.put("object network address " + addressNode.getDestDescription(), "");
                        System.out.println("object network address " + addressNode.getDestDescription());
                        if(addressNode.getDest()[0].contains("-")){
                            System.out.println("network-object range "+addressNode.getDest()[0].split("-")[0]+"-"+addressNode.getDest()[0].split("-")[0].substring(0,addressNode.getDest()[0].split("-")[0].lastIndexOf("."))+"."+addressNode.getDest()[0].split("-")[1]);
                        }else {
                            System.out.println("network-object network " + addressNode.getDest()[0] + "  255.255.255.255");

                        }
                        System.out.println("exit");
                    }
                }else{
                    if(objectMap.containsKey("object network address "+addressNode.getDestDescription()+"-"+addressNode.getDest()[0])){

                    }else{
                        objectMap.put("object network address "+addressNode.getDestDescription()+"-"+addressNode.getDest()[0],"");
                        System.out.println("object network address "+addressNode.getDestDescription()+"-"+addressNode.getDest()[0]);
                        if(addressNode.getDest()[0].contains("-")){
                            System.out.println("network-object range "+addressNode.getDest()[0].split("-")[0]+"-"+addressNode.getDest()[0].split("-")[0].substring(0,addressNode.getDest()[0].split("-")[0].lastIndexOf("."))+"."+addressNode.getDest()[0].split("-")[1]);
                        }else {
                            System.out.println("network-object network "+addressNode.getDest()[0] +"  255.255.255.255");

                        }
                        System.out.println("exit");
                    }
                }

            }


            portMap = autoService(portMap,addressNode);


            if(addressNode.getSourceIp().length>1){
                if(addressNode.getDest().length>1){
                    policyMap = autoPolicy(policyMap,addressNode.getSourceDescription(),addressNode.getDestDescription(),(String)portMap.get(Arrays.toString(addressNode.getPort())),addressNode.getDescription());
                }else{
                    if("10.246.166.139".equals(addressNode.getDest()[0].trim())){

                        policyMap = autoPolicy(policyMap,addressNode.getSourceDescription(),addressNode.getDestDescription(),(String)portMap.get(Arrays.toString(addressNode.getPort())),addressNode.getDescription());
                    }else{
                        policyMap = autoPolicy(policyMap,addressNode.getSourceDescription(),addressNode.getDestDescription()+"-"+addressNode.getDest()[0],(String)portMap.get(Arrays.toString(addressNode.getPort())),addressNode.getDescription());
                    }
                }
            }else{
                if(addressNode.getDest().length>1){
                    if("10.246.166.139".equals(addressNode.getSourceIp()[0].trim())){

                        policyMap = autoPolicy(policyMap,addressNode.getSourceDescription(),addressNode.getDestDescription(),(String)portMap.get(Arrays.toString(addressNode.getPort())),addressNode.getDescription());
                    }else{

                        policyMap = autoPolicy(policyMap,addressNode.getSourceDescription()+"-"+addressNode.getSourceIp()[0],addressNode.getDestDescription(),(String)portMap.get(Arrays.toString(addressNode.getPort())),addressNode.getDescription());
                    }
                }else {
                    if ("10.246.166.139".equals(addressNode.getSourceIp()[0].trim()) && "10.246.166.139".equals(addressNode.getDest()[0].trim())) {

                        policyMap = autoPolicy(policyMap, addressNode.getSourceDescription(), addressNode.getDestDescription(), (String) portMap.get(Arrays.toString(addressNode.getPort())), addressNode.getDescription());
                    } else if (!"10.246.166.139".equals(addressNode.getSourceIp()[0].trim()) && "10.246.166.139".equals(addressNode.getDest()[0].trim())) {

                        policyMap = autoPolicy(policyMap, addressNode.getSourceDescription() +"-"+ addressNode.getSourceIp()[0], addressNode.getDestDescription(), (String) portMap.get(Arrays.toString(addressNode.getPort())), addressNode.getDescription());
                    } else if ("10.246.166.139".equals(addressNode.getSourceIp()[0].trim()) && !"10.246.166.139".equals(addressNode.getDest()[0].trim())) {
                        policyMap = autoPolicy(policyMap, addressNode.getSourceDescription() , addressNode.getDestDescription()+"-"+addressNode.getDest()[0], (String) portMap.get(Arrays.toString(addressNode.getPort())), addressNode.getDescription());

                    } else {
                        policyMap = autoPolicy(policyMap, addressNode.getSourceDescription() +"-"+ addressNode.getSourceIp()[0], addressNode.getDestDescription()+"-"+addressNode.getDest()[0], (String) portMap.get(Arrays.toString(addressNode.getPort())), addressNode.getDescription());

                    }
                }
            }

            //吓一条记录
            addressNode = addressNode.next;
        }
    }
    public static Map autoService(Map portMap,Node addressNodeList){
        /**
         *
         * 功能描述: 生成服务的Command
         *
         * @param: [portMap 端口组， addressNodeList 对象链表]
         * @return: java.util.Map 返回已经生成命令的端口组
         * @auther:
         * @date: 2018/11/2 14:28
         */
        if(portMap.containsKey(Arrays.toString(addressNodeList.getPort()))){

        }else{
            portMap.put(Arrays.toString(addressNodeList.getPort()),"20181031_"+(++serviceCount)+"_端口");
            //object service custom BTX_BDX系统端口
            //service-item tcp src-port 1 65535 dst-port 3200 3299
            //service-item tcp src-port 1 65535 dst-port 3300 3399
            //service-item tcp src-port 1 65535 dst-port 4800 4899
            //service-item tcp src-port 1 65535 dst-port 4700 4799
            //service-item tcp src-port 1 65535 dst-port 8000 8099
            //service-item tcp src-port 1 65535 dst-port 3600 3699
            //exit
            //System.out.println(Arrays.toString(addressNodeList.getPort()));
            System.out.println("object service custom "+"20181031_"+serviceCount+"_端口");
            System.out.println("Service = "+Arrays.toString(addressNodeList.getPort()));

            if(addressNodeList.getPort().length>1){
                String  [] port = addressNodeList.getPort()[1].replace("TCP","").replace(":","").split(",");
                for(int i =0;i<port.length;i++){
                    if(port[i].split("-").length>1) {
                        System.out.println("service-item tcp src-port 1 65535 dst-port " + port[i].split("-")[0] + " " + port[i].split("-")[1]);
                    }else{
                        System.out.println("service-item tcp src-port 1 65535 dst-port " + port[i]+ " " + port[i]);
                    }
                }
            }else{
                String  [] port = addressNodeList.getPort()[0].replace("TCP","").replace(":","").split(",");
                for(int i =0;i<port.length;i++){
                    if(port[i].split("-").length>1) {
                        System.out.println("service-item tcp src-port 1 65535 dst-port " + port[i].split("-")[0] + " " + port[i].split("-")[1]);
                    }else{
                        System.out.println("service-item tcp src-port 1 65535 dst-port " + port[i]+ " " + port[i]);
                    }
                }
            }

            System.out.println("exit");
        }
        return portMap;
    }
    public static Map autoPolicy(Map policyMap,String sourceDescription,String destDescription,String service,String description){
        /**
         *
         * 功能描述: 生成策略Command
         *
         * @param: [sourceDescription, destDescription, service, description]
         * @return: void
         * @auther:
         * @date: 2018/11/2 14:36
         */
        //security policy BODS系统to测试系统BTX  sip BODS系统-10.5.86.203 dip  测试系统BTX-10.246.115.23 szone any dzone any service  BTX_BDX系统端口  action permit enable
        //security policy BODS系统to测试系统BTX  schedule  20181001-20190401
        //String policyName = sourceDescription+"to"+destDescription+"by"+service;
        String policyName = sourceDescription+"to"+destDescription;
        if(policyName.getBytes().length>63){
            policyName = policyName.substring(policyName.getBytes().length-63,policyName.length());
            //policyName = sourceDescription.substring(sourceDescription.length()/2,sourceDescription.length())+"to"+destDescription.substring(destDescription.length()/2,destDescription.length())+"by"+service.substring(service.length()/2,service.length());
        }
        if(policyMap.containsKey(policyName)){
            policyName = policyName+"_"+policyEqualCount++;
            System.out.println("重名策略");
        }else {
            policyMap.put(policyName,"");
            policyCount++;
            //System.out.println("policyCount = "+(++policyCount));
        }
        System.out.println("security policy "+policyName+" sip "+sourceDescription+" dip "+destDescription+" szone any dzone any service "+service+" action permit enable");
        System.out.println("security policy "+policyName+" description "+description);
        return policyMap;
    }
}
