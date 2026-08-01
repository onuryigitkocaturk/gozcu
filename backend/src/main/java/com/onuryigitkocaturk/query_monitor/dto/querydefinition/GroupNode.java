package com.onuryigitkocaturk.query_monitor.dto.querydefinition;

import com.onuryigitkocaturk.query_monitor.enums.LogicOperator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

// QueryNode interface'ini implement ederek QueryNode tipinde beklenen her yere koyabileceğimizi garanti ettik.
// ve/veya ile birleştirilmiş alt düğümleri tutuyor.
public class GroupNode implements QueryNode {

    // bu grubun altındaki tüm childerenın birbirine nasıl bağlandığını
    // belirtiyor.
    @NotNull
    private LogicOperator logic;

    @NotEmpty // boş bir grup anlamsız.
    @Valid // listenin içindeki her elemanı kendi validasyon kurallarına
            // göre kontrol et. recursive validation sağlar.
    private List<QueryNode> children;

    // bu boş constructor Jackson'ın JSON'dan nesne üretebilmesi için.
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
