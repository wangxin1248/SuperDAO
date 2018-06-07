package cn.lovepi.dao;

import cn.lovepi.bean.Antype;
import cn.lovepi.util.DBConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by icarus on 2016/5/30.
 * antype表的dao类
 */
public class AntypeDAO {
    /**
     * 查询antype表中的所有数据
     * @return antype的集合
     */
    public static ArrayList<Antype> getList(){
        ArrayList<Antype> al=new ArrayList<Antype>();
        Connection conn= DBConnectionFactory.getInstance().getConn();
        PreparedStatement ps=null;
        ResultSet rs=null;
        String sql="select * from antype";
        try{
            ps=conn.prepareStatement(sql);
            rs=ps.executeQuery();
            while(rs.next()){
                Antype antype=new Antype();
                antype.setAnid(rs.getInt("anid"));
                antype.setAnname(rs.getString("anname"));
                al.add(antype);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps,rs);
        }
        return al;
    }
}
