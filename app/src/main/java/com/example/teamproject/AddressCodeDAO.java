package com.example.teamproject;

import android.util.Log;
import java.sql.*;
import java.util.*;

public class AddressCodeDAO {
    // 연결
    private Connection conn;
    // 문장 전송 => SQL
    private PreparedStatement ps;
    //연결 => 오라클 주소
    // jdbc:oracle:thin:@localhost:1521:XE
    // IP 집 : 172.30.1.35 핫스팟 : 172.20.10.6
        private final String URL="jdbc:oracle:thin:@172.20.10.6:1521:XE";
        private final String userID = "C##qwerry";
        private final String userPW = "2345";

    // 드라이버 등록
    public AddressCodeDAO()
    {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    // 연결
    public void getConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            // 개인 Oracle 계정 사용
            conn=DriverManager.getConnection(URL,userID, userPW);
            Log.d("oracle login", "success");
            //conn hr/happy
        }catch(Exception ex) {
            Log.e("oracle login", "fail "+ ex.getMessage());
        }
    }
    //닫기
    public void disConnection() {
        try {
            if(ps!=null)ps.close();
            if(conn!=null)conn.close();
            //exit
        }catch(Exception ex) {}
    }
    // 우편번호 찾기
    public ArrayList<AddressCodeVO> postfind(String si_do){
        ArrayList<AddressCodeVO> list=
                new ArrayList<>();
        try {
            // 연결
            getConnection();
            // SQL 문장 전송
            String sql="SELECT * FROM Address_Table WHERE " + si_do + " LIKE '%'	?	'%'";

            ps=conn.prepareStatement(sql);
            ps.setString(1, si_do);
            ResultSet rs=ps.executeQuery();//실행
            while(rs.next()){
                AddressCodeVO vo=new AddressCodeVO();
                vo.setZipcode(rs.getString(1));
                vo.setSido(rs.getString(2));
                vo.setGugun(rs.getString(3));
                vo.setDong(rs.getString(4));
                vo.setBunji(rs.getString(5));

                list.add(vo);
            }
        }catch(Exception ex) {
            System.out.println(ex.getMessage());
        }finally {
            disConnection();
        }return list;
    }
}