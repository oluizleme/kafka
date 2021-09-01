package br.com.oluizleme.ecommrce;

import javax.servlet.http.HttpServlet;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher();
}
