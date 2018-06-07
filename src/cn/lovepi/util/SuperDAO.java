package cn.lovepi.util;

import cn.lovepi.bean.Animals;
import cn.lovepi.bean.Antype;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
/**
 * Created by icarus on 2016/5/30.
 * 万能DAO类
 * 缺点：多表查询报错，多表查询会多出属性
 * 当表实体类添加一个属性值，但数据库中却没有对应的列名
 */
public class SuperDAO {
    /**
     * 万能DAO查询类，根据传递的Class对象判断所要查询的具体表
     * 返回表中的所有数据
     * @param cl 传递的所要查询的表的POJO类对象
     * @return 查询到的数据库中的Object对象的集合
     */
    public static ArrayList getList(Class cl){
        //无泛型集合，存储查询到的数据
        ArrayList al=new ArrayList();
        //获取数据库连接
        Connection conn=DBConnectionFactory.getInstance().getConn();
        //初始化参数准备
        PreparedStatement ps=null;
        ResultSet rs=null;
        //拼接数据库查询语句，getSimpleName返回Class对象的类名
        String sql="select * from "+cl.getSimpleName();
        //获取Class对象的所有属性
        Field[] fields=cl.getDeclaredFields();
        //查询到的是对象类型是Object的
        Object ob=null;
        try{
            //开始进行数据库查询
            ps=conn.prepareStatement(sql);
            rs=ps.executeQuery();
            //获取查询数据
            while (rs.next()){
                //实例化Object对象
                ob=cl.newInstance();
                //对属性进行赋值
                for (Field fl:fields
                     ) {
                    fl.setAccessible(true);
                    fl.set(ob,rs.getObject(fl.getName()));
                }
                //将赋好值的对象添加到集合中去
                al.add(ob);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps,rs);
        }
        //返回集合
        return al;
    }


    /**
     * 根据id来查询对应的数据
     * @param cl 查询的表的POJO类对象
     * @param id 查询对象的id
     * @return 查询到的id
     */
    public static Object getListById(Class cl,int id){
        //获取数据库连接
        Connection conn=DBConnectionFactory.getInstance().getConn();
        //查询到的对象是Object类型的
        Object ob=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        //获取属性列表
        Field[] fields=cl.getDeclaredFields();
        //获取id的列名，id设置一般都是属性的第一位
        Field idName=fields[0];
        //数据库查询语句
        String sql="select * from "+cl.getSimpleName()+" where "+idName.getName()+"="+id;
        try{
            ps=conn.prepareStatement(sql);
            rs=ps.executeQuery();
            //查询到数据
            if (rs.next()){
                ob=cl.newInstance();
                //为查询到的Object对象的属性赋值
                for (Field f:fields){
                    f.setAccessible(true);
                    f.set(ob,rs.getObject(f.getName()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps,rs);
        }
        return ob;
    }


    /**
     * 根据某些特定值来查询符号要求的对象集合
     * @param cl 查询的对象的Class类型
     * @param str 所要查询的列名
     * @param ob 列名所对应的值
     * @return 查询到符合要求的对象集合
     */
    public static ArrayList getListBySome(Class cl,String str,Object ob){
        //对象集合
        ArrayList al=new ArrayList();
        //获取连接
        Connection conn=DBConnectionFactory.getInstance().getConn();
        PreparedStatement ps=null;
        ResultSet rs=null;
        //查询到的数据是Object类型的
        Object object=null;
        Field[] fields=cl.getDeclaredFields();
        //数据库查询sql语句
        String sql="select * from "+cl.getSimpleName()+" where "+str+" ='"+ob+"'";
        System.out.println(sql);
        try{
            //查询数据库
            ps=conn.prepareStatement(sql);
            rs=ps.executeQuery();
            //查询到数据
            while(rs.next()){
                object=cl.newInstance();
                for (Field f:fields
                     ) {
                    f.setAccessible(true);
                    f.set(object,rs.getObject(f.getName()));
                }
                al.add(object);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps,rs);
        }
        return al;
    }


    /**
     * 万能插入语句
     * @param object 插入的对象
     * @return 是否插入成功
     */
    public static boolean insert(Object object){
        //定义标志字
        boolean flag=false;
        //获取连接
        Connection conn=DBConnectionFactory.getInstance().getConn();
        PreparedStatement ps=null;
        //插入sql语句拼接
        StringBuilder sb=new StringBuilder();
        //获取Class对象
        Class cl=object.getClass();
        //获取Field属性数组
        Field[] fields=cl.getDeclaredFields();
        //开始拼接插入sql字符串
        sb.append("insert into "+cl.getSimpleName()+" (");
        StringBuilder sbf=new StringBuilder();
        for (int i=1;i<fields.length;i++){
            sb.append(fields[i].getName());
            sbf.append("?");
            if (i!=fields.length-1){
                sb.append(",");
                sbf.append(",");
            }
        }
        sb.append(") values ("+sbf.toString()+")");
        System.out.println(sb.toString());
        try {
            ps=conn.prepareStatement(sb.toString());
            for (int i=1;i<fields.length;i++){
                fields[i].setAccessible(true);
                ps.setObject(i,fields[i].get(object));
            }
            int a=ps.executeUpdate();
            if (a>0){
                flag=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps);
        }
        return flag;
    }


    /**
     * 万能更新方法
     * @param object 待更新的对象
     * @return 是否更新成功
     */
    public static boolean update(Object object){
        //定义标志字
        boolean flag=false;
        //获取连接
        Connection conn=DBConnectionFactory.getInstance().getConn();
        PreparedStatement ps=null;
        //获取Class对象
        Class cl=object.getClass();
        //获取Class对象属性数组
        Field[] fields=cl.getDeclaredFields();
        //更新sql字符串拼接
        ////update animals set name = ?,age = ?,anid = ? where id = ?
        StringBuilder sb=new StringBuilder();
        sb.append("update "+cl.getSimpleName()+" set ");
        for (int i=1;i<fields.length;i++){
            sb.append(fields[i].getName()+" = ?");
            if (i!=fields.length-1){
                sb.append(",");
            }
        }
        sb.append(" where "+fields[0].getName()+" = ?");
        System.out.println(sb.toString());
        try{
            ps=conn.prepareStatement(sb.toString());
            for (int i=1;i<fields.length;i++){
                fields[i].setAccessible(true);
                ps.setObject(i,fields[i].get(object));
            }
            //对id进行赋值
            fields[0].setAccessible(true);
            ps.setObject(fields.length,fields[0].get(object));
            int a=ps.executeUpdate();
            if (a>0){
                flag=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps);
        }
        return flag;
    }


    /**
     * 万能删除方法
     * @param object 所要删除的数据的POJO类对象
     * @param id 所要删除的数据的id
     * @return 删除是否成功
     */
    public static boolean delete(Object object,int id){
        boolean flag=false;
        Connection conn=DBConnectionFactory.getInstance().getConn();
        PreparedStatement ps=null;
        StringBuilder sb=new StringBuilder();
        Class cl=object.getClass();
        Field[] fields=cl.getDeclaredFields();
        sb.append("delete from "+cl.getSimpleName()+" where "+fields[0].getName()+" = "+id);
        try {
            ps=conn.prepareStatement(sb.toString());
            int a=ps.executeUpdate();
            if (a>0){
                flag=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps);
        }
        return flag;
    }


    /**
     * 根据具体值删除数据库记录
     * @param cl 删除记录的对象类的类值
     * @param name 删除记录的列名，即对应类的属性值
     * @param value 删除记录列的值
     * @return 是否删除成功
     */
    public static boolean deleteBySome(Class cl,String name,Object value){
        boolean flag=false;
        Connection conn=DBConnectionFactory.getInstance().getConn();
        PreparedStatement ps=null;
        Field[] fields=cl.getDeclaredFields();
        StringBuilder sb=new StringBuilder();
        sb.append("delete from "+cl.getSimpleName()+" where "+name+" = "+"'"+value+"'");
        try {
            ps=conn.prepareStatement(sb.toString());
            int a=ps.executeUpdate();
            if (a>0){
                flag=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps);
        }
        return flag;
    }


    /**
     * 测试方法
     * @param args
     */
    public static void main(String[] args){
        ArrayList al=new ArrayList();
        SuperDAO dao=new SuperDAO();
//        al=dao.getListBySome(Animals.class,"name","阿宝");
//        for (Object ob:al){
//            Animals an= (Animals) ob;
//            System.out.println(an);
//        }

       // System.out.println(dao.getById(Antype.class,2));

        Animals an=new Animals();
        an.setName("鬼刀");
        //an.setAge(21);
        an.setAge(121);
        an.setAnid(3);
        an.setId(3);
        //System.out.println(dao.update(an));
        //System.out.println(dao.insert(an));
        //System.out.println(dao.delete(an,4));
        System.out.println(dao.deleteBySome(Animals.class,"id",5));
        Antype antype=new Antype();
        antype.setAnid(3);
        antype.setAnname("食尸鬼");
        //System.out.println(dao.update(antype));
        //System.out.println(dao.insert(antype));
    }
}


