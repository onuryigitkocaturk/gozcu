import { EmptyState } from "./EmptyState";
import { formatCellValue, isNullValue } from "../../utils/format";
import type { TableRow } from "../../types/api";

/** Satirlarin kendi anahtarlarindan kolonlari cikarip generic bir tablo cizer. */
export function DataTable({ rows, emptyLabel = "Kayıt bulunamadı" }: { rows: TableRow[]; emptyLabel?: string }) {
  if (rows.length === 0) {
    return <EmptyState icon="🗒️" title={emptyLabel} />;
  }

  const columns = Array.from(
    rows.reduce((set, row) => {
      Object.keys(row).forEach((key) => set.add(key));
      return set;
    }, new Set<string>()),
  );

  return (
    <div className="table-wrap">
      <table className="data-table">
        <thead>
          <tr>
            {columns.map((col) => (
              <th key={col}>{col}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, i) => (
            <tr key={i}>
              {columns.map((col) => (
                <td key={col} className={isNullValue(row[col]) ? "cell-null" : undefined}>
                  {formatCellValue(row[col])}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
