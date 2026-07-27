import type { DragEvent } from "react";
import { Card, CardHeader } from "../ui";

export function FieldPalette({ columns }: { columns: string[] }) {
  const handleDragStart = (e: DragEvent, field: string) => {
    e.dataTransfer.setData("text/plain", field);
    e.dataTransfer.effectAllowed = "copy";
  };

  return (
    <Card className="qb-palette">
      <CardHeader title="Kolonlar" />
      <p className="text-sm text-muted mb-16">
        Bir kolonu sağdaki bir gruba sürükle, otomatik olarak yeni bir koşul oluşsun.
      </p>
      <div className="qb-palette__list">
        {columns.map((col) => (
          <div key={col} className="qb-chip" draggable onDragStart={(e) => handleDragStart(e, col)}>
            <span aria-hidden>⠿</span>
            {col}
          </div>
        ))}
      </div>
    </Card>
  );
}
