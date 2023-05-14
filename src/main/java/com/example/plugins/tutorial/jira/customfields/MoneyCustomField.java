package com.example.plugins.tutorial.jira.customfields;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.customfields.impl.AbstractSingleFieldType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


@Scanned
public class MoneyCustomField extends AbstractSingleFieldType<BigDecimal> {

    JiraAuthenticationContext jiraAuthenticationContext;

    private boolean viewField;
    final String JIRA_ADMINISTRATORS_GROUP = "jira-administrators";

    MutableIssue CustomFieldParentIssue;

    public MoneyCustomField(
            @JiraImport CustomFieldValuePersister customFieldValuePersister,
            @JiraImport GenericConfigManager genericConfigManager,
            @ComponentImport JiraAuthenticationContext jiraAuthenticationContext) {

        super(customFieldValuePersister, genericConfigManager);

        this.jiraAuthenticationContext = jiraAuthenticationContext;
        UserManager userManager = ComponentAccessor.getUserManager();
        ApplicationUser user = userManager.getUserByName(jiraAuthenticationContext.getLoggedInUser().getName());
        GroupManager groupManager = ComponentAccessor.getGroupManager();
        Set<String> currentUserGroups = (Set<String>) groupManager.getGroupNamesForUser(Objects.requireNonNull(user));
        for (String i : currentUserGroups) {
            System.out.println(i);
        }
        this.viewField = currentUserGroups.contains(JIRA_ADMINISTRATORS_GROUP);
    }

    @Override
    public String getStringFromSingularObject(final BigDecimal singularObject) {
        if (singularObject == null)
            return null;
        else
            return singularObject.toString();
    }

    @Override
    public BigDecimal getSingularObjectFromString(final String string) throws FieldValidationException {
        updateViewField();
        if (string == null) {
            return new BigDecimal("1.00");
        }
        try {
            BigDecimal decimal = new BigDecimal(string);
            if (decimal.scale() > 2) {
                throw new FieldValidationException(
                        "Maximum of 2 decimal places are allowed.");
            }
            updateViewField();
            return decimal.setScale(2, RoundingMode.UNNECESSARY);
        } catch (NumberFormatException ex) {
            throw new FieldValidationException("Not a valid number.");
        }
    }


    @Nonnull
    @Override
    protected PersistenceFieldType getDatabaseType() {
        return PersistenceFieldType.TYPE_LIMITED_TEXT;
    }

    @Override
    protected BigDecimal getObjectFromDbValue(@Nonnull final Object databaseValue)
            throws FieldValidationException {
        return getSingularObjectFromString((String) databaseValue);
    }

    @Override
    protected Object getDbValueFromObject(final BigDecimal customFieldObject) {

        return getStringFromSingularObject(customFieldObject);
    }

    private Set<String> getUserGroups(String name) {
        UserManager userManager = ComponentAccessor.getUserManager();
        ApplicationUser user = userManager.getUserByName(name);
        GroupManager groupManager = ComponentAccessor.getGroupManager();
        return (Set<String>) groupManager.getGroupNamesForUser(Objects.requireNonNull(user));
    }

    private boolean isLoggedInUserAdminUserAdmin() {
        String current_name = null;
        if (jiraAuthenticationContext != null) {
            current_name = jiraAuthenticationContext.getLoggedInUser().getName();
        }
        Set<String> currentUserGroups = getUserGroups(current_name);
        return currentUserGroups.contains(JIRA_ADMINISTRATORS_GROUP);
    }

    private void updateViewField() {
        this.viewField = isLoggedInUserAdminUserAdmin();
    }

    @Nonnull
    @Override
    public Map<String, Object> getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem) {
        Map<String, Object> params = super.getVelocityParameters(issue, field, fieldLayoutItem);
        params.put("viewField", viewField);
        CustomFieldParentIssue = (MutableIssue) issue;
        System.out.println("Summary of related issues:::: " +
                CustomFieldParentIssue.getSummary() +
                "ID: " + CustomFieldParentIssue.getId());
        return params;
    }
}