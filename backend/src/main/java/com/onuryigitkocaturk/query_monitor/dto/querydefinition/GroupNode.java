package com.onuryigitkocaturk.query_monitor.dto.querydefinition;

import com.onuryigitkocaturk.query_monitor.enums.LogicOperator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * VE/VEYA ile birlestirilmis alt dugumler (GroupNode ya da ConditionNode
 * olabilir) - ic ice gruplamayi bu sekilde destekliyoruz.
 */
public class GroupNode implements QueryNode {

    @NotNull
    private LogicOperator logic;

    @NotEmpty
    @Valid
    private List<QueryNode> children;

    public GroupNode() {
    }

    public GroupNode(LogicOperator logic, List<QueryNode> children) {
        this.logic = logic;
        this.children = children;
    }

    public LogicOperator getLogic() {
        return logic;
    }

    public void setLogic(LogicOperator logic) {
        this.logic = logic;
    }

    public List<QueryNode> getChildren() {
        return children;
    }

    public void setChildren(List<QueryNode> children) {
        this.children = children;
    }
}
