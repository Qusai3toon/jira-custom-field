package com.example.plugins.tutorial.jira.jira.customfields;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.customfields.impl.AbstractSingleFieldType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.*;

@Scanned
public class StoryCost extends AbstractSingleFieldType<BigDecimal> {
    private static final Logger log = LoggerFactory.getLogger(StoryCost.class);
    JiraAuthenticationContext jiraAuthenticationContext;
    final String JIRA_ADMINISTRATORS_GROUP = "jira-administrators";
    MutableIssue currentIssue;
    BigDecimal storyCost = new BigDecimal("0.00");

    public StoryCost(@JiraImport CustomFieldValuePersister customFieldValuePersister,
                     @JiraImport GenericConfigManager genericConfigManager,
                     @ComponentImport JiraAuthenticationContext jiraApplicationContext) {
        super(customFieldValuePersister, genericConfigManager);
        this.jiraAuthenticationContext = jiraApplicationContext;
//        getLinkedTasks(currentIssue);
    }

    @Nonnull
    @Override
    protected PersistenceFieldType getDatabaseType() {
        System.out.println(1);
        return PersistenceFieldType.TYPE_DECIMAL;
    }

    @Nullable
    @Override
    protected Object getDbValueFromObject(BigDecimal bigDecimal) {
        System.out.println(2);
        getLinkedTasks(currentIssue);
        return getStringFromSingularObject(bigDecimal);
    }

    @Override
    protected BigDecimal getObjectFromDbValue(@Nonnull final Object databaseValue)
            throws FieldValidationException {
        System.out.println(3);
        return getSingularObjectFromString(String.valueOf(databaseValue));
    }

    @Override
    public String getStringFromSingularObject(BigDecimal singularObject) {
        System.out.println(4);
        getLinkedTasks(currentIssue);
        if (singularObject == null)
            return null;
        else
            return singularObject.toString();
    }

    @Override
    public BigDecimal getSingularObjectFromString(String s) throws FieldValidationException {
        System.out.println(5);
        System.out.println(storyCost);
        if (currentIssue != null)
            getLinkedTasks(currentIssue);
        else
            System.out.println("Story wasn't created yet....");
        System.out.println("The current issue: is " + currentIssue);
        if (s.isEmpty())
            return new BigDecimal("1.00");
        else
            return new BigDecimal(String.valueOf(storyCost));
    }

    @Nonnull
    @Override
    public Map<String, Object> getVelocityParameters(final Issue issue,
                                                     final CustomField field,
                                                     final FieldLayoutItem fieldLayoutItem) {
        final Map<String, Object> map = super.getVelocityParameters(issue, field, fieldLayoutItem);
        map.put("Hello", "World");


        // This method is also called to get the default value, in
        // which case issue is null, so we can't use it to add currencyLocale
        if (issue == null) {
            return map;
        }


        FieldConfig fieldConfig = field.getRelevantConfig(issue);
        //add what you need to the map here
        currentIssue = (MutableIssue) issue;
        getLinkedTasks((MutableIssue) issue);
        map.put("storyCost", storyCost);
        return map;
    }

    private boolean isLoggedInUserAdminUserAdmin() {
        String current_name = null;
        if (jiraAuthenticationContext != null) {
            current_name = jiraAuthenticationContext.getLoggedInUser().getName();
        }
        Set<String> currentUserGroups = getUserGroups(current_name);
        return currentUserGroups.contains(JIRA_ADMINISTRATORS_GROUP);
    }

    private Set<String> getUserGroups(String name) {
        UserManager userManager = ComponentAccessor.getUserManager();
        ApplicationUser user = userManager.getUserByName(name);
        GroupManager groupManager = ComponentAccessor.getGroupManager();
        return (Set<String>) groupManager.getGroupNamesForUser(Objects.requireNonNull(user));
    }

    private ApplicationUser getLoggedInUser() {
        assert jiraAuthenticationContext != null;
        return jiraAuthenticationContext.getLoggedInUser();
    }

    @Nonnull
    private ArrayList<Issue> getLinkedTasks(@Nonnull MutableIssue issue) {
        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
        List<IssueLink> links = issueLinkManager.getOutwardLinks(issue.getId());
        ArrayList<Issue> linkedIssues = new ArrayList<>();
        storyCost = new BigDecimal("0.00");
        for (IssueLink link : links) {
            if (link.getDestinationObject().getIssueType() != null)
                if (link.getDestinationObject().getIssueType().getName().equalsIgnoreCase("task")) {
                    linkedIssues.add(link.getDestinationObject());
                }
        }
        for (Issue linkedIssue : linkedIssues) {
            if (linkedIssue.getIssueType() != null)
                if (linkedIssue.getIssueType().getName().equalsIgnoreCase("task")) {
                    System.out.println("Issue IDs Linked to : " + currentIssue + linkedIssue.getId());
                    storyCost = storyCost.add((BigDecimal) Objects.requireNonNull(customFieldManager.getCustomFieldObject(10000L)).getValue(linkedIssue));
                }
        }
        return linkedIssues;
    }
}