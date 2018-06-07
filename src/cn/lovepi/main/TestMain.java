package cn.lovepi.main;

import cn.lovepi.bean.Animals;
import cn.lovepi.bean.Antype;
import cn.lovepi.util.SuperDAOPro;

import java.util.ArrayList;
import java.util.Scanner;


/**
 * Created by icarus on 2016/5/30.
 * 测试类
 */
public class TestMain {
    public static void main(String[] args){
        SuperDAOPro dao=new SuperDAOPro();
        Scanner scanner=new Scanner(System.in);
        ArrayList<Animals> animalses=new ArrayList<Animals>();
        ArrayList<Antype> antypes=new ArrayList<Antype>();
        System.out.println("~~~欢迎进入动物园管理系统~~~");
        while(true){
            System.out.println("1.查看所有动物信息");
            System.out.println("2.增加动物信息");
            System.out.println("3.查看所有动物类别信息");
            System.out.println("4.增加动物类别信息");
            System.out.println("5.退出系统");
            int n=scanner.nextInt();
            if (n==1){
                String sql="SELECT * from animals a,antype an WHERE a.anid=an.anid";
                Object[] objects={};
                animalses=dao.getListBySql(Animals.class,sql,objects);
                System.out.println("动物ID\t动物名称\t动物年龄\t动物类别ID\t动物类别名称\t");
                for (Animals animal:animalses){
                    System.out.println(animal.getId()+"   "+animal.getName()+"   "+animal.getAge()+"   "+animal.getAnid()+"   "+animal.getAnname());
                }
            }else if (n==2){
                Animals am=new Animals();
                antypes=dao.getList(Antype.class);
                System.out.println("所有的动物类别为：");
                for (Antype an:antypes){
                    System.out.println("类别号 ："+an.getAnid()+"  类名名称 ："+an.getAnname());
                }
                System.out.println("请输入对应动物名称，年龄，类别号");
                am.setName(scanner.next());
                am.setAge(scanner.nextInt());
                am.setAnid(scanner.nextInt());
                if (dao.insert(am)){
                    System.out.println("增加成功！");
                }else{
                    System.out.println("增加失败！");
                }
            }else if (n==3){
                antypes=dao.getList(Antype.class);
                System.out.println("所有的动物类别为：");
                for (Antype an:antypes){
                    System.out.println("类别号 ："+an.getAnid()+"  类名名称 ："+an.getAnname());
                }
            }else if (n==4){
                Antype an=new Antype();
                System.out.println("请输入新的动物类别名称");
                an.setAnname(scanner.next());
                if (dao.insert(an)){
                    System.out.println("增加成功！");
                }else{
                    System.out.println("增加失败！");
                }
            }else {
                break;
            }

        }
    }
}
