package com.example.plugins.tutorial.jira.AO.evaluationProcess.evaluationImpl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.search.SearchProvider;
import com.example.plugins.tutorial.jira.AO.evaluationProcess.Evaluation;
import com.example.plugins.tutorial.jira.AO.evaluationProcess.evaluationService.EvaluationService;
import net.java.ao.Query;
import org.ofbiz.core.entity.GenericEntityException;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

@Scanned
@Named
public class EvaluationServiceImpl implements EvaluationService {
    @ComponentImport
    ActiveObjects ao;
    @ComponentImport
    SearchProvider searchProvider;

    @Inject
    public EvaluationServiceImpl(ActiveObjects ao, SearchProvider searchProvider) {
        this.ao = ao;
        this.searchProvider = searchProvider;
    }

    @Override
    public int evaluate(Date EvaluationPeriodEndDate, Date EvaluationPeriodStartDate, int employeeID) {
        ArrayList<Issue> issuesAssignedToUSer = new ArrayList<>();
        UserManager userManager = ComponentAccessor.getUserManager();
        System.out.println(userManager.getUserById((long) employeeID));
        IssueManager issueManager = ComponentAccessor.getIssueManager();
        ProjectManager projectManager = ComponentAccessor.getProjectManager();
        List<Project> projects = projectManager.getProjects();
        for (Project project : projects) {
            System.out.println("Searching through issues in project: " + project.getName());
            Collection<Long> projectIssueIDs = null;
            try {
                projectIssueIDs = issueManager.getIssueIdsForProject(project.getId());
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
            if (projectIssueIDs != null) {
                for (Long id : projectIssueIDs) {
                    if (issueManager.getIssueObject(id).getAssignee() != null) {
                        System.out.println("Checking issue: " + issueManager.getIssueObject(id).getSummary());
                        if (issueManager.getIssueObject(id).getAssignee().getId().equals((long) employeeID) && id != null) {
                            issuesAssignedToUSer.add(issueManager.getIssueObject(id));
                        }
                        else
                            System.out.println("The issue '" + issueManager.getIssueObject(id).getSummary() + "' is not assigned the user. ");
                    }
                }
            }
            for (Issue assignedIssue : issuesAssignedToUSer) {
                System.out.println("The issue '" + assignedIssue.getSummary() + "' is assigned to " + assignedIssue.getAssignee().getUsername());
            }
        }
        return employeeID;
    }

    @Override
    public Evaluation[] getEvaluationByEmployeeID(int employeeID) {
        Evaluation[] evaluations = ao.find(Evaluation.class, Query.select().distinct().where("EmployeeId = " + employeeID));
        if (evaluations != null && evaluations.length == 1)
            return evaluations;
        else
            return null;
    }


    @Override
    public void removeEvaluation(Evaluation evaluation) {
        ArrayList<Evaluation> evaluations = getAllEvaluations();
        Evaluation tobeDeleted;
        for (Evaluation e : evaluations) {
            if (e.getID() == evaluation.getID()) {
                tobeDeleted = evaluation;
                ao.delete(tobeDeleted);
                return;
            }
        }
    }

    @Override
    public ArrayList<Evaluation> getAllEvaluations() {
        return newArrayList(ao.find(Evaluation.class));
    }
}