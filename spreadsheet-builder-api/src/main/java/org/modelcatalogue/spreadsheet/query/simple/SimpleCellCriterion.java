package org.modelcatalogue.spreadsheet.query.simple;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.modelcatalogue.spreadsheet.api.Cell;
import org.modelcatalogue.spreadsheet.query.api.CellCriterion;
import org.modelcatalogue.spreadsheet.query.api.CellStyleCriterion;
import org.modelcatalogue.spreadsheet.query.api.Condition;

import java.util.Calendar;
import java.util.Date;

public final class SimpleCellCriterion extends AbstractCriterion<Cell> implements CellCriterion {

    @Override
    public void date(final Date value) {
        addValueCondition(value, Date.class);
    }

    @Override
    public void date(final Condition<Date> condition) {
        addValueCondition(condition, Date.class);
    }

    @Override
    public void number(Double value) {
        addValueCondition(value, Double.class);
    }

    @Override
    public void number(Condition<Double> condition) {
        addValueCondition(condition, Double.class);
    }

    @Override
    public void string(String value) {
        addValueCondition(value, String.class);
    }

    @Override
    public void string(Condition<String> condition) {
        addValueCondition(condition, String.class);
    }

    @Override
    public void value(Object value) {
        if (value == null) {
            string("");
            return;
        }
        if (value instanceof Date) {
            date((Date) value);
            return;
        }
        if (value instanceof Calendar) {
            date(((Calendar) value).getTime());
            return;
        }
        if (value instanceof Number) {
            number(((Number) value).doubleValue());
            return;
        }
        if (value instanceof Boolean) {
            bool((Boolean) value);
        }
        string(value.toString());
    }

    @Override
    public void name(final String name) {
        addCondition(new Condition<Cell>() {
            @Override
            public boolean evaluate(Cell o) {
                return name.equals(o.getName());
            }
        });
    }

    @Override
    public void comment(final String comment) {
        addCondition(new Condition<Cell>() {
            @Override
            public boolean evaluate(Cell o) {
                return comment.equals(o.getComment().getText());
            }
        });
    }

    @Override
    public void bool(Boolean value) {
        addValueCondition(value, Boolean.class);
    }


    @Override
    public void style(@DelegatesTo(CellStyleCriterion.class) @ClosureParams(value = FromString.class, options = "org.modelcatalogue.spreadsheet.query.api.CellStyleCriterion") Closure styleCriterion) {
        SimpleCellStyleCriterion criterion = new SimpleCellStyleCriterion(this);
        DefaultGroovyMethods.with(criterion, styleCriterion);
        // no need to add criteria, they are added by the style criterion itself
    }

    private <T> void addValueCondition(final T value, final Class<T> type) {
        addCondition(new Condition<Cell>() {
            @Override
            public boolean evaluate(Cell o) {
                try {
                    return value.equals(o.read(type));
                } catch (Exception e) {
                    return false;
                }
            }
        });
    }

    private <T> void addValueCondition(final Condition<T> condition, final Class<T> type) {
        addCondition(new Condition<Cell>() {
            @Override
            public boolean evaluate(Cell o) {
                try {
                    return condition.evaluate(o.read(type));
                } catch (Exception e) {
                    return false;
                }
            }
        });
    }

}
