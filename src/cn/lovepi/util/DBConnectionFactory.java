package cn.lovepi.util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Created by icarus on 2016/5/29.
 * 数据库连接工具类
 */
public class DBConnectionFactory {
    private static String driver;
    private static String dburl;
    private static String name;
    private static String passwd;

    //工厂类实例单例
    private static final DBConnectionFactory factory=new DBConnectionFactory();
    //静态代码块加载资源
    static {
        Properties properties=new Properties();
        InputStream in=
                DBConnectionFactory.class.getClassLoader().
                        getResourceAsStream("dbconfiger.properties");
        try {
            properties.load(in);
        } catch (Exception e) {
            System.out.println("配置文件读取错误！！！");
        }
        driver=properties.getProperty("driver");
        dburl=properties.getProperty("dburl");
        name=properties.getProperty("name");
        passwd=properties.getProperty("passwd");
    }

    /**
     * 获取工厂类实例
     * @return
     */
    public static DBConnectionFactory getInstance(){
        return factory;
    }

    /**
     * 获取数据库连接
     * @return
     */
    public Connection getConn(){
        Connection conn=null;
        try {
            Class.forName(driver);
            conn= DriverManager.getConnection(dburl+"?user="+name+"&password="+passwd+"&useUnicode=true&characterEncoding=utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("数据库连接失败！！！");
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     * @param conn
     * @param pstmt
     */
    public void closeConn(Connection conn, PreparedStatement pstmt){
        try {
            if (conn!=null){
                conn.close();
            }
            if (pstmt!=null){
                pstmt.close();
            }
        } catch (SQLException e) {
            System.out.println("数据库关闭失败！！！");;
        }

    }


    /**
     * 关闭数据库连接
     * @param conn
     * @param pstmt
     * @param rs
     */
    public void closeConn(Connection conn, PreparedStatement pstmt, ResultSet rs){
        try{
            if (conn!=null){
                conn.close();
            }
            if (pstmt!=null){
                pstmt.close();
            }
            if (rs!=null){
                rs.close();
            }
        }catch (Exception e){
            System.out.println("数据库关闭失败！！！");
        }
    }
}
