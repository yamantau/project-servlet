package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int cellPosition = getCellPosition(req);
        HttpSession httpSession = req.getSession();
        Field field = extractField(httpSession);

        if (field.getFieldData().get(cellPosition) != Sign.EMPTY){
            ServletContext servletContext = this.getServletContext();
            RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher("/index.jsp");
            requestDispatcher.forward(req, resp);
            return;
        }

        field.getField().put(cellPosition, Sign.CROSS);
        if (checkWinner(req, resp, field)) {
            return;
        }

        int emptyCellPosition = field.getEmptyFieldIndex();

        if (emptyCellPosition >= 0){
            field.getField().put(emptyCellPosition, Sign.NOUGHT);
            if (checkWinner(req, resp, field)) {
                return;
            }
        } else {
            httpSession.setAttribute("draw", true);
            httpSession.setAttribute("data", field.getFieldData());
            resp.sendRedirect("/index.jsp");
            return;
        }


        httpSession.setAttribute("field", field);
        httpSession.setAttribute("data", field.getFieldData());

        resp.sendRedirect("/index.jsp");

    }

    private int getCellPosition(HttpServletRequest req){
        int tdNumber = -1;
        try {
            tdNumber = Integer.parseInt(req.getParameter("click"));
        } catch (Exception e){
            e.printStackTrace();
        }
        if (tdNumber != -1){
            return tdNumber;
        }else {
            return 0;
        }
    }

    private Field extractField(HttpSession currentSession){

        Object field = currentSession.getAttribute("field");
        if (Field.class != field.getClass()){
            currentSession.invalidate();
            throw new RuntimeException("Something go wrong, update the page");
        }
        return (Field) field;
    }

    private boolean checkWinner(HttpServletRequest req, HttpServletResponse resp, Field field) throws IOException {

        Sign winner = field.checkWin();
        if (winner == Sign.NOUGHT || winner == Sign.CROSS){

            HttpSession httpSession = req.getSession();
            httpSession.setAttribute("winner", winner);
            httpSession.setAttribute("data", field.getFieldData());

            resp.sendRedirect("/index.jsp");
            return true;
        }

        return false;

    }

}
