//by Luoyou
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.http.*;


public abstract class HttpJSONServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        request.setCharacterEncoding("utf-8");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String parameter = sb.toString();
        try {
            doServlet(new JSONObject(parameter), request, response);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        doPost(request, response);
    }

    protected abstract void doServlet(JSONObject parameter, HttpServletRequest request, HttpServletResponse response);
}
