package cn.lovepi.dao;

import cn.lovepi.bean.Animals;
import cn.lovepi.util.DBConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by icarus on 2016/5/30.
 * Animals表的DAO类
 */
public class AnimalsDAO {
    /**
     * 查询animals表中的所有数据
     * @return animals的集合
     */
    public static ArrayList<Animals> getList(){
        ArrayList<Animals> al=new ArrayList<Animals>();
        Connection conn= DBConnectionFactory.getInstance().getConn();
        PreparedStatement ps=null;
        ResultSet rs=null;
        String sql="select * from animals";
        try{
            ps=conn.prepareStatement(sql);
            rs=ps.executeQuery();
            while(rs.next()){
                Animals animals=new Animals();
                animals.setId(rs.getInt("id"));
                animals.setName(rs.getString("name"));
                animals.setAge(rs.getInt("age"));
                animals.setAnid(rs.getInt("anid"));
                al.add(animals);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            DBConnectionFactory.getInstance().closeConn(conn,ps,rs);
        }
        return al;
    }


    /**
     * 连表查询
     * @return
     */
    public static ArrayList<Animals> getList1(){
        ArrayList<Animals> al=new ArrayList<Animals>();
        Connection conn=DBConnectionFactory.getInstance().getConn();
        PreparedStatement ps=null;
        ResultSet rs=null;
        String sql="select * from animals a,antype t where a.anid=t.anid";
        return null;
    }
}
