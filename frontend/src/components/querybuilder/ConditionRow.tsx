import type { ConditionOperator } from "../../types/api";
import type { BuilderCondition, BuilderConditionValue } from "./builderTypes";
import { OPERATOR_LABELS } from "./builderTypes";
import { ValueEditor } from "./ValueEditor";
import { Button } from "../ui";

const OPERATORS: ConditionOperator[] = [
  "EQUALS",
  "NOT_EQUALS",
  "GREATER_THAN",
  "GREATER_THAN_OR_EQUAL",
  "LESS_THAN",
  "LESS_THAN_OR_EQUAL",
  "CONTAINS",
  "IS_NULL",
  "IS_NOT_NULL",
];

export function ConditionRow({
  condition,
  onChange,
  onDelete,
}: {
  condition: BuilderCondition;
  onChange: (updated: BuilderCondition) => void;
  onDelete: () => void;
}) {
  const needsValue = condition.operator !== "IS_NULL" && condition.operator !== "IS_NOT_NULL";

  const handleValueChange = (value: BuilderConditionValue) => {
    onChange({ ...condition, value });
  };

  return (
    <div className="qb-condition">
      <span className="qb-condition__field">{condition.field}</span>
      <select
        value={condition.operator}
        onChange={(e) => onChange({ ...condition, operator: e.target.value as ConditionOperator })}
      >
        {OPERATORS.map((op) => (
          <option key={op} value={op}>
            {OPERATOR_LABELS[op]}
          </option>
        ))}
      </select>

      {needsValue && (
        <ValueEditor value={condition.value ?? { kind: "LITERAL", value: "" }} onChange={handleValueChange} />
      )}

      <span className="qb-condition__spacer" />
      <Button variant="ghost" size="sm" onClick={onDelete} aria-label="Koşulu sil">
        ✕
      </Button>
    </div>
  );
}
