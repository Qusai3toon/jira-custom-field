package com.example.plugins.tutorial.jira.servlets;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.example.plugins.tutorial.jira.AO.evaluationProcess.evaluationService.EvaluationService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date; 

import static com.google.common.base.Preconditions.checkNotNull;

public class EvaluationServlet extends HttpServlet {

    private final EvaluationService evaluationService;
    final private JiraAuthenticationContext jiraAuthenticationContext;

    public EvaluationServlet(EvaluationService evaluationService, JiraAuthenticationContext jiraAuthenticationContext) {
        this.evaluationService = checkNotNull(evaluationService);
        this.jiraAuthenticationContext = jiraAuthenticationContext;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write(evaluationService.evaluate(new Date(45L), new Date(54L), Math.toIntExact(jiraAuthenticationContext.getLoggedInUser().getId())) + "");
        response.getWriter().close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
