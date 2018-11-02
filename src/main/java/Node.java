import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * @program: net_360_wangwanqiang_Util
 * @description: Excel实体
 * @author: Mr.Wang
 * @create: 2018-11-02 21:01
 **/


public class Node {
    //源地址描述
    private String sourceDescription;
    private String[] sourceIp;
    private String destDescription;
    private String [] dest;
    private String port[];
    private String description;
    public Node next;//下一个节点的引用
    public void printNode() {
        System.out.println("sourceDescription = "+sourceDescription);
        System.out.println("sourceIp = "+ Arrays.toString(sourceIp));
        System.out.println("desDescription = "+destDescription);
        System.out.println("dest = "+Arrays.toString(dest));
        System.out.println("port = "+Arrays.toString(port));
        System.out.println("description = "+description);

        if (this.next != null) {
            this.next.printNode();
        }
    }


    //增加一个节点
    public void addNode(Node node) {

        if (this.next == null) {//如果下个引用中没有被填充,那么就添加到这里
            this.next = node;
        } else {//如果下个节点已经有了,那么继续进行递归的添加
            this.next.addNode(node);
        }

    }

    public String getSourceDescription() {
        return sourceDescription;
    }

    public void setSourceDescription(String sourceDescription) {
        this.sourceDescription = sourceDescription;
    }

    public String[] getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String[] sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getDestDescription() {
        return destDescription;
    }

    public void setDestDescription(String destDescription) {
        this.destDescription = destDescription;
    }

    public String[] getDest() {
        return dest;
    }

    public void setDest(String[] dest) {
        this.dest = dest;
    }

    public String[] getPort() {
        return port;
    }

    public void setPort(String[] port) {
        this.port = port;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    //是否包含指定节点
//    public boolean contains(String data) {
//        if (this.next == null) {
//            return false;
//        } else {
//            if (this.next.data.equals(data)) {
//                return true;
//            } else {
//                return this.next.contains(data);
//            }
//        }
//    }
}
