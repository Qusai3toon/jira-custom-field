package com.example.plugins.tutorial.jira.AO.evaluationProcess;

import net.java.ao.Entity;

import java.util.Date;

public interface Evaluation extends Entity {
    int getEmployeeId();

    void setEmployeeId(int employeeId);

    Date getEvaluationDate();

    void setEvaluationDate(Date evaluationDate);

    Date getEvaluationPeriodStartDate();

    void setEvaluationPeriodStartDate(Date evaluationPeriodStartDate);

    Date getEvaluationPeriodEndDate();

    void setEvaluationPeriodEndDate(Date evaluationPeriodEndDate);

    int setEvaluationResult();

    void getEvaluationResult(int evaluationResult);
}

/*
 * An evaluation has the below attributes:
 * 1- ID. (Generated Automatically)
 * 2- employeeId.
 * 3- evaluationDate.
 * 4- evaluationPeriodStartDate.
 * 5- evaluationPeriodEndDate.
 * 6- evaluationResult.
 * */
