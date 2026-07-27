import type { BuilderConditionValue } from "./builderTypes";
import { UNIT_LABELS } from "./builderTypes";
import type { RelativeDateDirection, RelativeDateUnit } from "../../types/api";

const UNITS: RelativeDateUnit[] = ["HOURS", "DAYS", "MONTHS", "YEARS"];

export function ValueEditor({
  value,
  onChange,
}: {
  value: BuilderConditionValue;
  onChange: (value: BuilderConditionValue) => void;
}) {
  const kind = value.kind;

  return (
    <div className="qb-condition__value">
      <div className="qb-value-kind-toggle">
        <button
          type="button"
          className={kind === "LITERAL" ? "active" : ""}
          onClick={() => onChange({ kind: "LITERAL", value: "" })}
        >
          Sabit değer
        </button>
        <button
          type="button"
          className={kind === "RELATIVE_DATE" ? "active" : ""}
          onClick={() => onChange({ kind: "RELATIVE_DATE", direction: "FUTURE", amount: 1, unit: "DAYS" })}
        >
          Göreli tarih
        </button>
      </div>

      {value.kind === "LITERAL" ? (
        <input
          type="text"
          placeholder="değer…"
          value={value.value}
          onChange={(e) => onChange({ kind: "LITERAL", value: e.target.value })}
        />
      ) : (
        <>
          <select
            value={value.direction}
            onChange={(e) => onChange({ ...value, direction: e.target.value as RelativeDateDirection })}
          >
            <option value="FUTURE">ileride</option>
            <option value="PAST">geçmişte</option>
          </select>
          <input
            type="number"
            min={0}
            style={{ width: 64 }}
            value={value.amount}
            onChange={(e) => onChange({ ...value, amount: Number(e.target.value) })}
          />
          <select value={value.unit} onChange={(e) => onChange({ ...value, unit: e.target.value as RelativeDateUnit })}>
            {UNITS.map((u) => (
              <option key={u} value={u}>
                {UNIT_LABELS[u]}
              </option>
            ))}
          </select>
          <span className="text-sm text-muted">sonra/önce (şu ana göre)</span>
        </>
      )}
    </div>
  );
}
