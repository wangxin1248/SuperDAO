package cn.lovepi.util;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by icarus on 2016/5/31.
 * 升级版万能数据库查询DAO类
 */
public class SuperDAOPro {

    /**
     * 使用ResultSetMetaData来获取数据库中查询到的表的所有列名
     * 可以查询到查到返回数据的所有列名，即可以查询到多表返回的列名
     * @param tableName 查询表名
     * @return 表中列名的集合
     */
    public static ArrayList<String> getColumnsByRSMD(String tableName){
        ArrayList<String> al=new ArrayList<String>();
        Connection conn=DBConnectionFactory.getInstance().getConn();
        PreparedStatement ps=null;
        ResultSet rs=null;
        //sql语句，不会查询到任何数据，只会返回查询到的列信息
        String sql="select * from "+tableName+" where 1=2";
        try{
            ps=conn.prepareStatement(sql);
            rs=ps.executeQuery();
            //获取ResultSetMetaData对象
            ResultSetMetaData msd=rs.getMetaData();
            //获取查询到的列的总数
            int n=msd.getColumnCount();
            //获取所有类名并加入到集合当中去,列号从1开始
            for (int i=1;i<=n;i++){
                al.add(msd.getColumnName(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps,rs);
        }
        return al;
    }

    /**
     * 使用DataBaseMetaData来获取数据库中查询到的表的所有列名
     * DataBaseMetaData数据库元数据对象，也可理解为数据库数据对象
     * DataBaseMetaData值可以获取到当前查询表的相关数据
     * 即单表属性获取
     * @param tableName
     * @return
     */
    public static ArrayList<String> getColumnsByDBMD(String tableName){
        ArrayList<String> al=new ArrayList<String>();
        Connection conn=DBConnectionFactory.getInstance().getConn();
        try{
            DatabaseMetaData dmd=conn.getMetaData();
            //获取表中所有类的属性信息集合
            ResultSet rs=dmd.getColumns(null,"%",tableName,"%");
            //遍历集合，获取数据
            while (rs.next()){
                al.add(rs.getString("COLUMN_NAME"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return al;
    }
    /**
     * 查询表中所有数据
     * select * 效率太低，改为select具体列名
     * @param cl 表对象实体类Class对象
     * @return 查询到的表中数据集合
     */
    public static ArrayList getList(Class cl){
        ArrayList al=new ArrayList();
        Connection conn=DBConnectionFactory.getInstance().getConn();
        PreparedStatement ps=null;
        ResultSet rs=null;
        //获取表中的列名集合
        ArrayList<String> cols=getColumnsByRSMD(cl.getSimpleName());
        //获取属性集合
        Field[] fields=cl.getDeclaredFields();
        //拼接sql字符串
        StringBuilder sb=new StringBuilder();
        sb.append("select ");
        for (int i=0;i<cols.size();i++){
            sb.append(cols.get(i));
            if (i!=cols.size()-1){
                sb.append(",");
            }
        }
        sb.append(" from "+cl.getSimpleName());
        try{
            ps=conn.prepareStatement(sb.toString());
            rs=ps.executeQuery();
            while (rs.next()){
                Object ob=cl.newInstance();
                for (String str:cols) {
                    for (Field f:fields){
                        if (f.getName().equals(str)){
                            f.setAccessible(true);
                            f.set(ob,rs.getObject(str));
                            break;
                        }
                    }
                }
                al.add(ob);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps,rs);
        }
        return al;
    }

    /**
     * 根据id来查询对应的数据
     * @param cl 查询的表的POJO类对象
     * @param id 查询对象的id
     * @return 查询到的id
     */
    public static Object getlistById(Class cl,int id){
        //获取数据库连接
        Connection conn=DBConnectionFactory.getInstance().getConn();
        //查询到的对象是Object类型的
        Object ob=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        ArrayList<String> cols=getColumnsByRSMD(cl.getSimpleName());
        //获取属性列表
        Field[] fields=cl.getDeclaredFields();
        //获取id的列名，id设置一般都是属性的第一位
        Field idName=fields[0];
        StringBuilder sb=new StringBuilder();
        //数据库查询语句
        sb.append("select ");
        for (int i=0;i<cols.size();i++){
            sb.append(cols.get(i));
            if (i!=cols.size()-1){
                sb.append(",");
            }
        }
        sb.append(" from "+cl.getSimpleName()+" where "+idName.getName()+" = "+id);
        try{
            ps=conn.prepareStatement(sb.toString());
            rs=ps.executeQuery();
            //查询到数据
            if (rs.next()){
                ob=cl.newInstance();
                for (String str:cols){
                    for (Field f:fields){
                        if (f.getName().equals(str)){
                            f.setAccessible(true);
                            f.set(ob,rs.getObject(str));
                            break;
                        }
                    }
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
     * @param object 列名所对应的值
     * @return 查询到符合要求的对象集合
     */
    public static ArrayList getListBySome(Class cl,String str,Object object){
        //对象集合
        ArrayList al=new ArrayList();
        //获取连接
        Connection conn=DBConnectionFactory.getInstance().getConn();
        PreparedStatement ps=null;
        ResultSet rs=null;
        //查询到的数据是Object类型的
        Object ob=null;
        Field[] fields=cl.getDeclaredFields();
        ArrayList<String> cols=getColumnsByRSMD(cl.getSimpleName());
        StringBuilder sb=new StringBuilder();
        //数据库查询sql语句
        sb.append("select ");
        for (int i=0;i<cols.size();i++){
            sb.append(cols.get(i));
            if (i!=cols.size()-1){
                sb.append(",");
            }
        }
        sb.append(" from "+cl.getSimpleName()+" where "+str+" = '"+ob+"'");
        try{
            //查询数据库
            ps=conn.prepareStatement(sb.toString());
            rs=ps.executeQuery();
            //查询到数据
            while(rs.next()){
                ob=cl.newInstance();
                for (String s:cols){
                    for (Field f:fields) {
                        if (f.getName().equals(s)){
                            f.setAccessible(true);
                            f.set(ob,rs.getObject(s));
                            break;
                        }
                    }
                }
                al.add(ob);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps,rs);
        }
        return al;
    }

    /**
     * 数据库模糊查询操作
     * @param cl 实体类对象
     * @param str 查询字段属性名
     * @param object 查询字段属性值对应值
     * @return 查询到的对象集合
     */
    public static ArrayList getListByLike(Class cl,String str,Object object){
        ArrayList al=new ArrayList();
        Connection conn=DBConnectionFactory.getInstance().getConn();
        PreparedStatement ps=null;
        ResultSet rs=null;
        Field[] fields=cl.getDeclaredFields();
        ArrayList<String> cols=getColumnsByRSMD(cl.getSimpleName());
        StringBuilder sb=new StringBuilder();
        sb.append("select ");
        for (int i=0;i<cols.size();i++){
            sb.append(cols.get(i));
            if (i!=cols.size()-1){
                sb.append(",");
            }
        }
        //模糊查询不支持占位符操作
        sb.append(" from "+cl.getSimpleName()+" where "+str+" like '%"+object+"%'");
        try{
            ps=conn.prepareStatement(sb.toString());
            rs=ps.executeQuery();
            while (rs.next()){
                Object ob=cl.newInstance();
                for (String col:cols){
                    for (Field f:fields){
                        if (f.getName().equals(col)){
                            f.setAccessible(true);
                            f.set(ob,rs.getObject(col));
                            break;
                        }
                    }
                }
                al.add(ob);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps,rs);
        }
        return al;
    }

    /**
     * 多表查询操作
     * @param cl 查询的对象类
     * @param sql 多表查询的sql语句
     * @param objects sql语句中的占位符对象数组
     * @return 查询到的对象集合
     */
    public static ArrayList getListBySql(Class cl,String sql,Object[] objects){
        ArrayList al=new ArrayList();
        ArrayList<String> cols=new ArrayList<String>();
        Connection conn=DBConnectionFactory.getInstance().getConn();
        Field[] fields=cl.getDeclaredFields();
        PreparedStatement ps=null;
        ResultSet rs=null;
        try{
            ps=conn.prepareStatement(sql);
            //对占位符进行赋值
            for (int i=0;i<objects.length;i++){
                ps.setObject(i+1,objects[i]);
            }
            rs=ps.executeQuery();
            //获取返回表中的所有列名
            ResultSetMetaData data=rs.getMetaData();
            int columes=data.getColumnCount();
            for (int i=1;i<=columes;i++){
                cols.add(data.getColumnName(i));
            }
            while (rs.next()){
                Object object=cl.newInstance();
                for (String str:cols){
                    for (Field f:fields){
                        if (f.getName().equals(str)){
                            f.setAccessible(true);
                            f.set(object,rs.getObject(str));
                        }
                    }
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
     * 插入方法
     * @param object 插入的对象类
     * @return 是否插入成功
     */
    public static Boolean insert(Object object){
        boolean b=false;
        Connection conn=DBConnectionFactory.getInstance().getConn();
        PreparedStatement ps=null;
        Class cl=object.getClass();
        Field[] fields=cl.getDeclaredFields();
        ArrayList<String> cols=getColumnsByRSMD(cl.getSimpleName());
        StringBuilder sb=new StringBuilder();
        StringBuilder sb1=new StringBuilder();
        sb.append("insert into "+cl.getSimpleName()+" ( ");
        for (int i=1;i<cols.size();i++){
            sb.append(cols.get(i));
            sb1.append("?");
            if (i!=cols.size()-1){
                sb.append(",");
                sb1.append(",");
            }
        }
        sb.append(" ) values ( "+sb1.toString()+" )");
        //System.out.println(sb.toString());
        try{
            ps=conn.prepareStatement(sb.toString());
            for (int i=1;i<cols.size();i++){
                for (Field f:fields){
                    if (cols.get(i).equals(f.getName())){
                        f.setAccessible(true);
                        ps.setObject(i,f.get(object));
                        break;
                    }
                }
            }
            int a=ps.executeUpdate();
            if (a>0){
                b=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps);
        }
        return b;
    }


    /**
     * 获取到插入数据的主键id信息
     * 有些项目中订单表和订单信息表之间要先插入订单表，
     * 然后根据订单表的主键来插入订单信息表中的数据。
     * @param object 插入数据的对象
     * @return 插入到数据库中表的主键信息
     */
    public static int insertGetGeneratedKey(Object object){
        int id=0;
        Connection conn=DBConnectionFactory.getInstance().getConn();
        PreparedStatement ps=null;
        ResultSet rs=null;
        Class cl=object.getClass();
        Field[] fields=cl.getDeclaredFields();
        ArrayList<String> cols=getColumnsByRSMD(cl.getSimpleName());
        StringBuilder sb=new StringBuilder();
        StringBuilder sb1=new StringBuilder();
        sb.append("insert into "+cl.getSimpleName()+" ( ");
        for (int i=1;i<cols.size();i++){
            sb.append(cols.get(i));
            sb1.append("?");
            if (i!=cols.size()-1){
                sb.append(",");
                sb1.append(",");
            }
        }
        sb.append(" ) values ( "+sb1.toString()+" )");
        //System.out.println(sb.toString());
        try{
            ps=conn.prepareStatement(sb.toString(),Statement.RETURN_GENERATED_KEYS);
            for (int i=1;i<cols.size();i++){
                for (Field f:fields){
                    if (cols.get(i).equals(f.getName())){
                        f.setAccessible(true);
                        ps.setObject(i,f.get(object));
                        break;
                    }
                }
            }
            int a=ps.executeUpdate();
            if (a>0) {
                //使用条件：
                //1.只有在insert的语句中使用
                //2.只有在表中的主键是自增时使用
                //BUG：JDBC驱动5.1.17之后出现的新问题
                //java.sql.SQLException: Generated keys not requested.
                // You need to specify Statement.RETURN_GENERATED_KEYS to Statement.executeUpdate(),
                // Statement.executeLargeUpdate() or Connection.prepareStatement().
                //处理方式：prepareStatement方法中添加Statement.RETURN_GENERATED_KEYS参数
                rs=ps.getGeneratedKeys();
                if (rs.next()){
                    id=rs.getInt(1);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps,rs);
        }
        return id;
    }


    /**
     * 更新方法
     * @param object 待更新的对象类型
     * @return 是否更新成功
     */
    public static boolean update(Object object){
        boolean b=false;
        Connection conn=DBConnectionFactory.getInstance().getConn();
        Class cl=object.getClass();
        Field[] fields=cl.getDeclaredFields();
        ArrayList<String> cols=getColumnsByRSMD(cl.getSimpleName());
        PreparedStatement ps=null;
        StringBuilder sb=new StringBuilder();
        sb.append("update "+cl.getSimpleName()+" set ");
        for (int i=1;i<cols.size();i++){
            sb.append(cols.get(i)+" = ?");
            if (i!=cols.size()-1){
                sb.append(",");
            }
        }
        sb.append(" where "+fields[0].getName()+" =?");
        try{
            ps=conn.prepareStatement(sb.toString());
            for (int i=1;i<cols.size();i++){
                for (Field f:fields){
                    if (cols.get(i).equals(f.getName())){
                        f.setAccessible(true);
                        ps.setObject(i,f.get(object));
                        break;
                    }
                }
            }
            fields[0].setAccessible(true);
            ps.setObject(cols.size(),fields[0].get(object));
            int a=ps.executeUpdate();
            if (a>0){
                b=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps);
        }
        return b;
    }

    /**
     * 删除方法
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
}
