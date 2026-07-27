import { FieldPalette } from "./FieldPalette";
import { GroupBlock, type GroupBlockHandlers } from "./GroupBlock";
import {
  addChild,
  createCondition,
  createEmptyGroup,
  mapNode,
  removeNode,
  type BuilderGroup,
} from "./builderTypes";

export function QueryBuilder({
  columns,
  value,
  onChange,
}: {
  columns: string[];
  value: BuilderGroup;
  onChange: (tree: BuilderGroup) => void;
}) {
  const handlers: GroupBlockHandlers = {
    onUpdateLogic: (groupId, logic) =>
      onChange(mapNode(value, groupId, (n) => (n.type === "GROUP" ? { ...n, logic } : n))),
    onUpdateCondition: (conditionId, updated) => onChange(mapNode(value, conditionId, () => updated)),
    onRemove: (nodeId) => onChange(removeNode(value, nodeId)),
    onAddCondition: (parentGroupId, field) => onChange(addChild(value, parentGroupId, createCondition(field))),
    onAddGroup: (parentGroupId) => onChange(addChild(value, parentGroupId, createEmptyGroup())),
  };

  return (
    <div className="qb-layout">
      <FieldPalette columns={columns} />
      <GroupBlock node={value} isRoot handlers={handlers} />
    </div>
  );
}
