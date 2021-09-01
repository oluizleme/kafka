package br.com.oluizleme.ecommerce;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GenerateOrderServlet extends HttpServlet {

    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher();

    @Override
    public void destroy() {
        super.destroy();
        try {
            userDispatcher.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            for (User user: users) {
                userDispatcher.send("USER_GENERATE_READING_REPORT", user.getUuid(), user);
            }
            System.out.println("Sent generate report to all users.");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("Report requests generated.");

        } catch (ExecutionException e) {
            throw  new ServletException(e);
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }
}
