//by Luoyou
package com.wpsoft;

import com.sun.rowset.CachedRowSetImpl;
import javax.sql.rowset.CachedRowSet;
import java.sql.*;

/**
 * 数据访问基础类
 * @author WinUP
 * @version 1.0
 * @since 1.7
 */
public class SQLConnectorBase
{
    private static Connection _CONN = null;
    private static String _LastErrorMessage = "";

    public SQLConnectorBase()
    {
        GetConnect();
    }

    protected void finalize() throws Throwable
    {
        Disconnect();
        super.finalize();
    }

    /**
     * 获取上次查询的错误信息
     * @return 上次查询的错误信息
     */
    public String getLastErrorMessage() { return _LastErrorMessage; }

    /**
     * 获取上次查询的错误状态
     * @return 上次查询的错误状态
     */
    public boolean getLastErrorState() { return getLastErrorMessage().equals(""); }

    /**
     * 和数据库建立连接
     */
    private void GetConnect()
    {
        if(_CONN!=null) return;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //_CONN = DriverManager.getConnection("jdbc:mysql://localhost:3306/thubd?user=root&password=meiminger123");
            _CONN = DriverManager.getConnection("jdbc:mysql://localhost:3306/testi?user=kangshen&password=123456");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 根据查询返回结果中第一行第一列的数据
     * @param Expression 查询字符串
     * return 结果中第一行第一列的数据
     */
    public Object ExecuteScalar(String Expression)
    {
        try
        {
            PreparedStatement ps = _CONN.prepareStatement(Expression);
            ResultSet rs=ps.executeQuery();
            _LastErrorMessage = "";
            Object rtn = null;
            if(rs.next()) rtn = rs.getObject(1);
            return rtn;
        }catch(Exception ex)
        {
            _LastErrorMessage = ex.getMessage();
        }
        return null;
    }

    /**
     * 根据查询返回结果表
     * @param Expression 查询字符串
     * @return 结果表
     */
    public CachedRowSet ExecuteAdapter(String Expression) throws SQLException
    {
        CachedRowSet cachedRS = new CachedRowSetImpl();
        try
        {
            Statement stmt = _CONN.createStatement();
            ResultSet rs = stmt.executeQuery(Expression);
            cachedRS.populate(rs);
            _LastErrorMessage = "";
        }catch(Exception ex)
        {
            _LastErrorMessage = ex.getMessage();

        }
        return cachedRS;
    }

    /**
     * 根据查询返回本次查询受影响的行数
     * 如果你只用了SELECT语句，由于没有对数据造成影响，所以不计算在内
     * @param Expression 查询字符串
     * @return 本次查询受影响的行数
     */
    public int ExecuteNonQuery(String Expression)
    {
        int iResult;
        try
        {
            PreparedStatement ps = _CONN.prepareStatement(Expression);
            iResult = ps.executeUpdate();
            _LastErrorMessage = "";
        }catch(Exception ex)
        {
            _LastErrorMessage = ex.getMessage();
            return -1;
        }
        return iResult;
    }

    /**
     * 断开和数据库的连接
     */
    private void Disconnect()
    {
        try {
            _CONN.close();
            _CONN = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            _CONN=null;
        }
    }
}