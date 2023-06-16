package com.example.plugins.tutorial.jira.servlets;

import com.example.plugins.tutorial.jira.AO.ToDo.Todo;
import com.example.plugins.tutorial.jira.AO.ToDo.TodoService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter; 

import static com.google.common.base.Preconditions.checkNotNull;

public class TodoServlet_WithService extends HttpServlet {


    private final TodoService todoService;

    public TodoServlet_WithService(TodoService todoService) {
        this.todoService = checkNotNull(todoService);

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final PrintWriter w = response.getWriter();
        w.write("<h1>Todos</h1>");
        w.write("<form method=\"post\">");
        w.write("<input type=\"text\" name=\"task\" size=\"25\"/>");
        w.write("  ");
        w.write("<input type=\"submit\" name=\"submit\" value=\"Add\"/>");
        w.write("</form>");
        w.write("<ol>");
        for (Todo todo : todoService.all()) {
            w.printf("<li><%2$s> %s </%2$s></li>", todo.getDescription(), todo.isComplete() ? "strike" : "strong");
        }
        w.write("</ol>");
        w.write("<script language='javascript'>document.forms[0].elements[0].focus();</script>");
        w.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String description = request.getParameter("task");
        todoService.add(description);
        response.sendRedirect(request.getContextPath() + "/plugins/servlet/todo");

    }
}
