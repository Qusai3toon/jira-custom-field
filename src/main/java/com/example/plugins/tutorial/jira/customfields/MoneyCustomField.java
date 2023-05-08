package com.example.plugins.tutorial.jira.customfields;

import com.atlassian.jira.issue.customfields.impl.AbstractSingleFieldType;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.user.*;
import com.atlassian.jira.component.ComponentAccessor;


import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
//import com.atlassian.sal.api.user.*;

@Scanned
public class MoneyCustomField extends AbstractSingleFieldType<BigDecimal> {

    JiraAuthenticationContext jiraAuthenticationContext;
    public boolean viewField = false;

    public MoneyCustomField(
            @JiraImport CustomFieldValuePersister customFieldValuePersister,
            @JiraImport GenericConfigManager genericConfigManager,
            @ComponentImport JiraAuthenticationContext jiraAuthenticationContext) {
        super(customFieldValuePersister, genericConfigManager);
        this.jiraAuthenticationContext = jiraAuthenticationContext;

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
        if (string == null)
            return null;
        try {
            BigDecimal decimal = new BigDecimal(string);
            if (decimal.scale() > 2) {
                throw new FieldValidationException(
                        "Maximum of 2 decimal places are allowed.");
            }
            return decimal.setScale(2);
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
        System.err.println("getObjectFromDbValue(): " + (String) databaseValue);
        UserDetails userDetails = new UserDetails("admin", "admin");
        System.out.println("DirectoryID: " + userDetails.getDirectoryId().toString());
        System.out.println("username: " + userDetails.getUsername());
        System.out.println("Password: " + userDetails.getPassword());
        System.out.println("Display name: " + userDetails.getDisplayName());
//        System.out.println(jiraAuthenticationContext.getLoggedInUser());
        System.out.println("ID: "
                + jiraAuthenticationContext.getLoggedInUser().getId().toString()
                + " Name " + jiraAuthenticationContext.getLoggedInUser().getName());

//        Collection<String> user_groups new Collection<String>;

        String current_name = jiraAuthenticationContext.getLoggedInUser().getName();

        Set<String> groups = getUserGroups(current_name);

        for (String group : groups) {
            System.out.println("Current user groups are: " + group);
        }

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
}