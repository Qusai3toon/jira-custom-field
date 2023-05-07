package com.example.plugins.tutorial.jira.customfields;

import com.atlassian.jira.issue.customfields.impl.AbstractSingleFieldType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;

public class nbv extends AbstractSingleFieldType<BigDecimal> {

    protected nbv(CustomFieldValuePersister customFieldValuePersister, GenericConfigManager genericConfigManager) {
        super(customFieldValuePersister, genericConfigManager);
    }

    @Nonnull
    @Override
    protected PersistenceFieldType getDatabaseType() {
        return null;
    }

    @Nullable
    @Override
    protected Object getDbValueFromObject(BigDecimal bigDecimal) {
        return null;
    }

    @Nullable
    @Override
    protected BigDecimal getObjectFromDbValue(@Nonnull Object o) throws FieldValidationException {
        return null;
    }

    @Override
    public String getStringFromSingularObject(BigDecimal bigDecimal) {
        return null;
    }

    @Override
    public BigDecimal getSingularObjectFromString(String s) throws FieldValidationException {
        return null;
    }
}
