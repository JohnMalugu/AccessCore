package com.jcmlabs.AccessCore.Utilities.PaginationUtilities;

import java.util.List;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class SearchFieldsInput {
    private String fieldName;
    private Object fieldValue;
    private List<Object> fieldValues;
    @Enumerated(EnumType.STRING)
    private QueryOperator queryOperator;
    @Enumerated(EnumType.STRING)
    private SearchOperationType searchType;

    public SearchFieldsInput(String fieldName, Object fieldValue, SearchOperationType searchType) {
        this.queryOperator = QueryOperator.OR;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.searchType = searchType;
    }

    public SearchFieldsInput(String fieldName, SearchOperationType searchType) {
        this.queryOperator = QueryOperator.OR;
        this.fieldName = fieldName;
        this.searchType = searchType;
    }

    public SearchFieldsInput(String fieldName, List<Object> fieldValues, SearchOperationType searchType) {
        this.queryOperator = QueryOperator.OR;
        this.fieldName = fieldName;
        this.fieldValues = fieldValues;
        this.searchType = searchType;
    }

    public SearchFieldsInput(String fieldName, Object fieldValue, List<Object> fieldValues, QueryOperator queryOperator, SearchOperationType searchType) {
        this.queryOperator = QueryOperator.OR;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.fieldValues = fieldValues;
        this.queryOperator = queryOperator;
        this.searchType = searchType;
    }

    public SearchFieldsInput() {
        this.queryOperator = QueryOperator.OR;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getFieldValue() {
        return this.fieldValue;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    public List<Object> getFieldValues() {
        return this.fieldValues;
    }

    public void setFieldValues(List<Object> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public QueryOperator getQueryOperator() {
        return this.queryOperator;
    }

    public void setQueryOperator(QueryOperator queryOperator) {
        this.queryOperator = queryOperator;
    }

    public SearchOperationType getSearchType() {
        return this.searchType;
    }

    public void setSearchType(SearchOperationType searchType) {
        this.searchType = searchType;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SearchFieldsInput other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else {
                label71:
                {
                    Object this$fieldName = this.getFieldName();
                    Object other$fieldName = other.getFieldName();
                    if (this$fieldName == null) {
                        if (other$fieldName == null) {
                            break label71;
                        }
                    } else if (this$fieldName.equals(other$fieldName)) {
                        break label71;
                    }

                    return false;
                }

                Object this$fieldValue = this.getFieldValue();
                Object other$fieldValue = other.getFieldValue();
                if (this$fieldValue == null) {
                    if (other$fieldValue != null) {
                        return false;
                    }
                } else if (!this$fieldValue.equals(other$fieldValue)) {
                    return false;
                }

                label57:
                {
                    Object this$fieldValues = this.getFieldValues();
                    Object other$fieldValues = other.getFieldValues();
                    if (this$fieldValues == null) {
                        if (other$fieldValues == null) {
                            break label57;
                        }
                    } else if (this$fieldValues.equals(other$fieldValues)) {
                        break label57;
                    }

                    return false;
                }

                Object this$queryOperator = this.getQueryOperator();
                Object other$queryOperator = other.getQueryOperator();
                if (this$queryOperator == null) {
                    if (other$queryOperator != null) {
                        return false;
                    }
                } else if (!this$queryOperator.equals(other$queryOperator)) {
                    return false;
                }

                Object this$searchType = this.getSearchType();
                Object other$searchType = other.getSearchType();
                if (this$searchType == null) {
                    return other$searchType == null;
                } else return this$searchType.equals(other$searchType);
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof SearchFieldsInput;
    }

    public int hashCode() {
        int result = 1;
        Object $fieldName = this.getFieldName();
        result = result * 59 + ($fieldName == null ? 43 : $fieldName.hashCode());
        Object $fieldValue = this.getFieldValue();
        result = result * 59 + ($fieldValue == null ? 43 : $fieldValue.hashCode());
        Object $fieldValues = this.getFieldValues();
        result = result * 59 + ($fieldValues == null ? 43 : $fieldValues.hashCode());
        Object $queryOperator = this.getQueryOperator();
        result = result * 59 + ($queryOperator == null ? 43 : $queryOperator.hashCode());
        Object $searchType = this.getSearchType();
        result = result * 59 + ($searchType == null ? 43 : $searchType.hashCode());
        return result;
    }

    public String toString() {
        return "SearchFieldsInput(fieldName=" + this.getFieldName() + ", fieldValue=" + this.getFieldValue() + ", fieldValues=" + this.getFieldValues() + ", queryOperator=" + this.getQueryOperator() + ", searchType=" + this.getSearchType() + ")";
    }

    public enum QueryOperator {
        AND,
        OR;

        QueryOperator() {
        }
    }
}
