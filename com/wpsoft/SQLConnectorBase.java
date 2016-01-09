//by Luoyou
package com.wpsoft;

import com.sun.rowset.CachedRowSetImpl;
import javax.sql.rowset.CachedRowSet;
import java.sql.*;


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


    public String getLastErrorMessage() { return _LastErrorMessage; }


    public boolean getLastErrorState() { return getLastErrorMessage().equals(""); }


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