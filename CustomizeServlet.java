//by Luoyou
import com.wpsoft.SQLConnectorBase;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.CachedRowSet;
import java.text.DecimalFormat;

public class CustomizeServlet extends HttpJSONServlet {
    @Override
    protected void doServlet(JSONObject parameter, HttpServletRequest request, HttpServletResponse response) {
        try {
            String country = parameter.getString("country");
            String caixi = parameter.getString("caixi");
            String level = parameter.getString("level");
            SQLConnectorBase connector = new SQLConnectorBase();
            CachedRowSet list = null;
            list = connector.ExecuteAdapter("SELECT convert(avg(dishcount),signed) AS A FROM (SELECT hotelinfo.subname,SUM(bill.cost)/hotelinfo.tablecount AS total,hotelinfo.dishcount\n" +
                    "FROM fundamental\n" +
                    "JOIN hotelinfo ON fundamental.hotelid=hotelinfo.hotelid\n" +
                    "JOIN bill ON fundamental.hotelid=bill.hotelid\n" +
                    "WHERE fundamental.caixi=\"" + caixi + "\"\n" +
                    "GROUP BY fundamental.hotelid\n" +
                    "ORDER BY total DESC LIMIT 0,2) AS B");

            list.next();
            int N = list.getInt(1);

            list = connector.ExecuteAdapter("select dishtype.subname,dishtype.hotelid\n" +
                    "from fundamental\n" +
                    "join dishtype on fundamental.hotelid=dishtype.hotelid\n" +
                    "join bill on fundamental.hotelid=bill.hotelid\n" +
                    "\n" +
                    "where  fundamental.caixi=\"" + caixi + "\" \n" +
                    "group by fundamental.hotelid\n" +
                    "order by sum(bill.cost)/sum(bill.peoplecount) DESC\n" +
                    "\n");
            int start, end = list.size() / 3;
            start = (parameter.getInt("level") - 1) * end;
            end = start + list.size() / 3;
            if (end > list.size()) end = list.size();

            String hid = "";
            for (int i = 0; i <= end; i++) {
                list.next();
                if (i < start) continue;
                hid += "," + list.getString(2);
            }
            hid = hid.substring(1);


            list = connector.ExecuteAdapter("SELECT menu.unified,avg(dishlist.price)\n" +
                    "FROM menu\n" +
                    "JOIN dishlist ON dishlist.name=menu.original\n" +
                    "WHERE menu.unified IN (SELECT unified FROM (SELECT menu.unified\n" +
                    "\tFROM fundamental\n" +
                    "\tJOIN billdetail ON fundamental.hotelid=billdetail.hotelid\n" +
                    "\tJOIN menu ON billdetail.dishname=menu.original\n" +
                    "\n" +
                    "\tWHERE fundamental.caixi=\"" + caixi + "\" \n" +
                    "\tGROUP BY menu.unified\n" +
                    "\tORDER BY sum(billdetail.dishcount) DESC limit 0," + N + ") AS TEMP_A)\n" +
                    "    AND dishlist.hotelid IN("+hid+")\n" +
                    "GROUP BY menu.unified\n" +
                    "\n" +
                    "\n");
            System.out.println(connector.getLastErrorMessage());
            JSONArray answer = new JSONArray();
            DecimalFormat df = new DecimalFormat( "0.00 ");
            int i = 1;
            while (list.next()) {
                JSONObject temp = new JSONObject();
                temp.put("name", list.getString(1));
                temp.put("price", df.format(list.getDouble(2)));
                temp.put("num", i++);
                answer.put(temp);
            }
            response.getWriter().write(answer.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
