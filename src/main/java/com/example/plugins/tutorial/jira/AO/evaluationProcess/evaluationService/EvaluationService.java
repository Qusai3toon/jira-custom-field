package com.example.plugins.tutorial.jira.AO.evaluationProcess.evaluationService;


import com.atlassian.activeobjects.tx.Transactional;
import com.example.plugins.tutorial.jira.AO.evaluationProcess.Evaluation;

import java.util.ArrayList;
import java.util.Date;

@Transactional
public interface EvaluationService {

    int evaluate(Date EvaluationPeriodEndDate, Date EvaluationPeriodStartDate, int employeeID);

    Evaluation[] getEvaluationByEmployeeID(int employeeID);

    void removeEvaluation(Evaluation evaluationID);

    ArrayList<Evaluation> getAllEvaluations();

}
