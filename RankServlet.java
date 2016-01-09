//by Luoyou
import org.json.JSONArray;
import org.json.JSONObject;
import com.wpsoft.SQLConnectorBase;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.CachedRowSet;
import java.text.DecimalFormat;



public class RankServlet extends HttpJSONServlet {
    @Override
    protected void doServlet(JSONObject parameter, HttpServletRequest request, HttpServletResponse response) {

        try {
            String country = parameter.getString("country");
            String caixi = parameter.getString("caixi");
            String season=parameter.getString("season");

            SQLConnectorBase connector = new SQLConnectorBase();
            CachedRowSet list = null;

            if(parameter.has("byDish")){
                String byDish = parameter.getString("byDish");
                //销售数目
                if(byDish.equals("quantity")){
                    list = connector.ExecuteAdapter("SELECT menu.unified,sum(billdetail.dishcount) AS total\n" +
                            "\n" +
                            "FROM fundamental\n" +
                            "JOIN billdetail ON fundamental.hotelid=billdetail.hotelid\n" +
                            "JOIN menu ON billdetail.dishname=menu.original\n" +
                            "\n" +
                            "WHERE fundamental.country="+country+" AND  fundamental.caixi=\""+caixi+"\" \n" +
                            "\tAND billdetail.season = \""+season+"\"\n" +
                            "GROUP BY menu.unified\n" +
                            "ORDER BY total DESC");

                }
                //销售金额
                else if(byDish.equals("salecount")){
                    list = connector.ExecuteAdapter("SELECT menu.unified,sum(billdetail.dishcount*billdetail.dishprice) AS total\n" +
                            "FROM fundamental\n" +
                            "JOIN billdetail ON fundamental.hotelid=billdetail.hotelid\n" +
                            "JOIN menu ON billdetail.dishname=menu.original\n" +
                            "\n" +
                            "WHERE fundamental.country="+country+" AND  fundamental.caixi=\""+caixi+"\" \n" +
                            "\tAND billdetail.season = \""+season+"\"\n" +
                            "GROUP BY menu.unified\n" +
                            "ORDER BY total DESC\n");



                }
            }
            else if(parameter.has("byHotel")){
                String byHotel = parameter.getString("byHotel");
                //餐厅营业额
                if(byHotel.equals("turnover")){
                    list = connector.ExecuteAdapter("SELECT dishtype.subname,sum(billdetail.dishcount*billdetail.dishprice) AS total\n" +
                            "FROM fundamental\n" +
                            "JOIN dishtype ON fundamental.hotelid=dishtype.hotelid\n" +
                            "JOIN billdetail ON fundamental.hotelid=billdetail.hotelid\n" +
                            "JOIN menu ON billdetail.dishname=menu.original\n" +
                            "WHERE fundamental.country="+country+" AND  fundamental.caixi=\""+caixi+"\" \n" +
                            "\tAND billdetail.season = \""+season+"\"\n" +
                            "GROUP BY fundamental.hotelid\n" +
                            "ORDER BY total DESC\n" +
                            "\n");


                }
                //餐厅总客流量
                else if(byHotel.equals("traffic")){
                    list = connector.ExecuteAdapter("select dishtype.subname,sum(bill.peoplecount) as total " +
                            "from fundamental " +
                            "join dishtype on fundamental.hotelid=dishtype.hotelid " +
                            "join bill on fundamental.hotelid=bill.hotelid " +
                            "WHERE fundamental.country="+country+" AND  fundamental.caixi=\""+caixi+"\" " +
                            "and bill.season = \""+season+"\" " +
                            "group by fundamental.hotelid " +
                            "order by total DESC");

                }
                //餐厅客单价
                else if(byHotel.equals("customerprice")){
                    list = connector.ExecuteAdapter("SELECT dishtype.subname,sum(bill.cost)/sum(bill.peoplecount) AS total\n" +
                            "FROM fundamental\n" +
                            "JOIN dishtype ON fundamental.hotelid=dishtype.hotelid\n" +
                            "JOIN bill ON fundamental.hotelid=bill.hotelid\n" +
                            "\n" +
                            "WHERE fundamental.country="+country+" AND  fundamental.caixi=\""+caixi+"\" " +
                            "and bill.season = \""+season+"\" " +
                            "GROUP BY fundamental.hotelid\n" +
                            "ORDER BY total DESC\n" +
                            "\n");

                }
                //餐厅绩效
                else if(byHotel.equals("performance")){
                    list = connector.ExecuteAdapter("SELECT hotelinfo.subname,sum(bill.cost)/hotelinfo.tablecount AS total\n" +
                            "FROM fundamental\n" +
                            "JOIN hotelinfo ON fundamental.hotelid=hotelinfo.hotelid\n" +
                            "JOIN bill ON fundamental.hotelid=bill.hotelid\n" +
                            "\n" +
                            "WHERE fundamental.country="+country+" AND  fundamental.caixi=\""+caixi+"\" " +
                            "and bill.season = \""+season+"\" " +
                            "GROUP BY fundamental.hotelid\n" +
                            "ORDER BY total DESC\n" +
                            "\n");

                }

            }


            JSONArray answer = new JSONArray();
            int i = 1;
            DecimalFormat df = new DecimalFormat( "0.00 ");
            while(list.next())
            {
                JSONObject line = new JSONObject();
                line.put("num", i++);
                line.put("name", list.getString(1));
                line.put("count", df.format(list.getDouble(2)));
                answer.put(line);
            }
            response.getWriter().write(answer.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
