import type { ReactNode } from "react";

type BadgeColor = "blue" | "green" | "amber" | "red" | "neutral";

export function Badge({ color = "neutral", children }: { color?: BadgeColor; children: ReactNode }) {
  return <span className={`badge badge--${color}`}>{children}</span>;
}
