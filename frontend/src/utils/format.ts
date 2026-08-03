// Backend'den gelen degerleri (JPA LocalDateTime string'leri, JDBC'den
// gelen java.sql.Date degerleri, null'lar) ekranda okunakli gostermek icin.

const ISO_DATE_LIKE = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/;

export function isDateLike(value: unknown): value is string {
  return typeof value === "string" && ISO_DATE_LIKE.test(value);
}

export function formatDateTime(value: string): string {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString("tr-TR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export function formatCellValue(value: unknown): string {
  if (value === null || value === undefined) return "—";
  if (typeof value === "boolean") return value ? "Evet" : "Hayır";
  if (isDateLike(value)) return formatDateTime(value);
  return String(value);
}

export function isNullValue(value: unknown): boolean {
  return value === null || value === undefined;
}
