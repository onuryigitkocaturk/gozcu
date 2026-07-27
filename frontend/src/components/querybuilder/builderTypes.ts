import type {
  ConditionOperator,
  ConditionValue,
  LogicOperator,
  QueryNode,
  RelativeDateDirection,
  RelativeDateUnit,
} from "../../types/api";

// Builder agaci, backend'in QueryNode'una cok benzer ama her dugumde
// React key'leri ve agac islemleri icin client-side bir `id` tasir.
// Backend'e gonderilmeden once `id` alanlari soyulur (bkz. toQueryNode).

export interface BuilderLiteralValue {
  kind: "LITERAL";
  value: string;
}
export interface BuilderRelativeDateValue {
  kind: "RELATIVE_DATE";
  direction: RelativeDateDirection;
  amount: number;
  unit: RelativeDateUnit;
}
export type BuilderConditionValue = BuilderLiteralValue | BuilderRelativeDateValue;

export interface BuilderCondition {
  id: string;
  type: "CONDITION";
  field: string;
  operator: ConditionOperator;
  value: BuilderConditionValue | null;
}

export interface BuilderGroup {
  id: string;
  type: "GROUP";
  logic: LogicOperator;
  children: BuilderNode[];
}

export type BuilderNode = BuilderGroup | BuilderCondition;

let idCounter = 0;
export function nextId(): string {
  idCounter += 1;
  return `n${idCounter}-${Date.now().toString(36)}`;
}

export function createEmptyGroup(logic: LogicOperator = "AND"): BuilderGroup {
  return { id: nextId(), type: "GROUP", logic, children: [] };
}

export function createCondition(field: string): BuilderCondition {
  return {
    id: nextId(),
    type: "CONDITION",
    field,
    operator: "EQUALS",
    value: { kind: "LITERAL", value: "" },
  };
}

// ---- Immutable tree operations (root'tan baslayip id'yi arar) ----

export function mapNode(root: BuilderGroup, id: string, updater: (node: BuilderNode) => BuilderNode): BuilderGroup {
  function walk(node: BuilderNode): BuilderNode {
    if (node.id === id) return updater(node);
    if (node.type === "GROUP") {
      return { ...node, children: node.children.map(walk) };
    }
    return node;
  }
  return walk(root) as BuilderGroup;
}

export function removeNode(root: BuilderGroup, id: string): BuilderGroup {
  function walk(node: BuilderGroup): BuilderGroup {
    return {
      ...node,
      children: node.children
        .filter((child) => child.id !== id)
        .map((child) => (child.type === "GROUP" ? walk(child) : child)),
    };
  }
  return walk(root);
}

export function addChild(root: BuilderGroup, parentId: string, child: BuilderNode): BuilderGroup {
  function walk(node: BuilderGroup): BuilderGroup {
    if (node.id === parentId) {
      return { ...node, children: [...node.children, child] };
    }
    return { ...node, children: node.children.map((c) => (c.type === "GROUP" ? walk(c) : c)) };
  }
  return walk(root);
}

// ---- Backend donusumu ----

export function toQueryNode(node: BuilderNode): QueryNode {
  if (node.type === "GROUP") {
    return { type: "GROUP", logic: node.logic, children: node.children.map(toQueryNode) };
  }
  return {
    type: "CONDITION",
    field: node.field,
    operator: node.operator,
    value: node.operator === "IS_NULL" || node.operator === "IS_NOT_NULL" ? null : toConditionValue(node.value),
  };
}

function toConditionValue(value: BuilderConditionValue | null): ConditionValue | null {
  if (!value) return null;
  if (value.kind === "RELATIVE_DATE") {
    return { kind: "RELATIVE_DATE", direction: value.direction, amount: value.amount, unit: value.unit };
  }
  return { kind: "LITERAL", value: coerceLiteral(value.value) };
}

function coerceLiteral(raw: string): string | number | boolean {
  if (raw === "true") return true;
  if (raw === "false") return false;
  if (raw.trim() !== "" && !Number.isNaN(Number(raw))) return Number(raw);
  return raw;
}

export function fromQueryNode(node: QueryNode): BuilderNode {
  if (node.type === "GROUP") {
    return { id: nextId(), type: "GROUP", logic: node.logic, children: node.children.map(fromQueryNode) };
  }
  return {
    id: nextId(),
    type: "CONDITION",
    field: node.field,
    operator: node.operator,
    value: node.value
      ? node.value.kind === "LITERAL"
        ? { kind: "LITERAL", value: String(node.value.value) }
        : node.value
      : null,
  };
}

/** Backend her GroupNode.children'in @NotEmpty olmasini zorunlu kilar. */
export function findEmptyGroup(node: BuilderNode): boolean {
  if (node.type === "GROUP") {
    if (node.children.length === 0) return true;
    return node.children.some(findEmptyGroup);
  }
  return false;
}

export const OPERATOR_LABELS: Record<ConditionOperator, string> = {
  EQUALS: "eşittir",
  NOT_EQUALS: "eşit değildir",
  GREATER_THAN: "büyüktür",
  GREATER_THAN_OR_EQUAL: "büyük eşittir",
  LESS_THAN: "küçüktür",
  LESS_THAN_OR_EQUAL: "küçük eşittir",
  CONTAINS: "içerir",
  IS_NULL: "boş",
  IS_NOT_NULL: "boş değil",
};

export const UNIT_LABELS: Record<RelativeDateUnit, string> = {
  HOURS: "saat",
  DAYS: "gün",
  MONTHS: "ay",
  YEARS: "yıl",
};
