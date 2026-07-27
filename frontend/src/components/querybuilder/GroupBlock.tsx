import { useState, type DragEvent } from "react";
import type { LogicOperator } from "../../types/api";
import type { BuilderCondition, BuilderGroup } from "./builderTypes";
import { ConditionRow } from "./ConditionRow";
import { Button } from "../ui";

export interface GroupBlockHandlers {
  onUpdateLogic: (groupId: string, logic: LogicOperator) => void;
  onUpdateCondition: (conditionId: string, updated: BuilderCondition) => void;
  onRemove: (nodeId: string) => void;
  onAddCondition: (parentGroupId: string, field: string) => void;
  onAddGroup: (parentGroupId: string) => void;
}

export function GroupBlock({
  node,
  isRoot,
  handlers,
}: {
  node: BuilderGroup;
  isRoot: boolean;
  handlers: GroupBlockHandlers;
}) {
  const [dragOver, setDragOver] = useState(false);
  const { onUpdateLogic, onUpdateCondition, onRemove, onAddCondition, onAddGroup } = handlers;

  const handleDragOver = (e: DragEvent) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = "copy";
    setDragOver(true);
  };

  const handleDrop = (e: DragEvent) => {
    e.preventDefault();
    setDragOver(false);
    const field = e.dataTransfer.getData("text/plain");
    if (field) onAddCondition(node.id, field);
  };

  return (
    <div
      className={`qb-group${dragOver ? " drag-over" : ""}`}
      onDragOver={handleDragOver}
      onDragLeave={() => setDragOver(false)}
      onDrop={handleDrop}
    >
      <div className="qb-group__header">
        <div className="qb-logic-toggle">
          <button
            type="button"
            className={node.logic === "AND" ? "active" : ""}
            onClick={() => onUpdateLogic(node.id, "AND")}
          >
            VE
          </button>
          <button
            type="button"
            className={node.logic === "OR" ? "active" : ""}
            onClick={() => onUpdateLogic(node.id, "OR")}
          >
            VEYA
          </button>
        </div>
        {!isRoot && (
          <Button variant="ghost" size="sm" onClick={() => onRemove(node.id)}>
            Grubu sil
          </Button>
        )}
      </div>

      <div className="qb-group__children">
        {node.children.length === 0 && (
          <div className="qb-group__empty">Buraya bir kolon sürükle ya da aşağıdan koşul ekle.</div>
        )}
        {node.children.map((child) =>
          child.type === "GROUP" ? (
            <GroupBlock key={child.id} node={child} isRoot={false} handlers={handlers} />
          ) : (
            <ConditionRow
              key={child.id}
              condition={child}
              onChange={(updated) => onUpdateCondition(child.id, updated)}
              onDelete={() => onRemove(child.id)}
            />
          ),
        )}
      </div>

      <div className="qb-group__actions">
        <Button variant="secondary" size="sm" onClick={() => onAddGroup(node.id)}>
          + Alt grup
        </Button>
      </div>
    </div>
  );
}
