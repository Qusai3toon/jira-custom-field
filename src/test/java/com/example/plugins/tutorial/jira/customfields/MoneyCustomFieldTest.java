package com.example.plugins.tutorial.jira.customfields;

import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class MoneyCustomFieldTest {

    @Test
    public void testGetDbValueFromObject() throws Exception
    {
        MoneyCustomField moneyCustomField = new MoneyCustomField(null, null, null);
        assertEquals("1", moneyCustomField.getDbValueFromObject(new BigDecimal(1)));
        assertEquals("1.02", moneyCustomField.getDbValueFromObject(new BigDecimal("1.02")));
        assertEquals("0.02", moneyCustomField.getDbValueFromObject(new BigDecimal("0.02")));
    }

    @Test
    public void testGetStringFromSingularObject() throws Exception
    {
        MoneyCustomField moneyCustomField = new MoneyCustomField(null, null, null);
        assertEquals("1", moneyCustomField.getStringFromSingularObject(new BigDecimal(1)));
        assertEquals("1.02", moneyCustomField.getStringFromSingularObject(new BigDecimal("1.02")));
        assertEquals("0.02", moneyCustomField.getStringFromSingularObject(new BigDecimal("0.02")));
    }

    @Test
    public void testGetSingularObjectFromString() throws Exception
    {
        MoneyCustomField moneyCustomField = new MoneyCustomField(null, null, null);
        assertEquals("3.00", moneyCustomField.getSingularObjectFromString("3").toString());
        assertEquals("3.03", moneyCustomField.getSingularObjectFromString("3.03").toString());
        assertEquals("3.20", moneyCustomField.getSingularObjectFromString("3.2").toString());
        // Now test the errors:
        try
        {
            moneyCustomField.getSingularObjectFromString("3 dollars");
            fail("Validation should have failed.");
        }
        catch (FieldValidationException ex)
        {
            assertEquals("Not a valid number.", ex.getMessage());
        }
        try
        {
            moneyCustomField.getSingularObjectFromString("3.203");
            fail("Validation should have failed.");
        }
        catch (FieldValidationException ex)
        {
            assertEquals("Maximum of 2 decimal places are allowed.", ex.getMessage());
        }
    }

}
