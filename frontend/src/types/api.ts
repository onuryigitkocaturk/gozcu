// Backend ile birebir eslesen tipler (bkz. dto/, enums/ paketleri).

export type Role = "ADMIN" | "USER";
export type Frequency = "HOURLY" | "DAILY";
export type LogStatus = "TRIGGERED" | "NOT_TRIGGERED" | "ERROR";
export type AlertMetric = "ROW_COUNT";
export type ConditionOperator =
  | "EQUALS"
  | "NOT_EQUALS"
  | "GREATER_THAN"
  | "GREATER_THAN_OR_EQUAL"
  | "LESS_THAN"
  | "LESS_THAN_OR_EQUAL"
  | "CONTAINS"
  | "IS_NULL"
  | "IS_NOT_NULL";
export type LogicOperator = "AND" | "OR";
export type RelativeDateDirection = "FUTURE" | "PAST";
export type RelativeDateUnit = "HOURS" | "DAYS" | "MONTHS" | "YEARS";

// ---- Auth ----
export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
}
export interface LoginRequest {
  username: string;
  password: string;
}
export interface AuthResponse {
  token: string;
}
export interface UserResponse {
  id: number;
  username: string;
  email: string;
  role: Role;
  createdAt: string;
}
export interface UpdateUserRequest {
  username: string;
  email: string;
}
export interface ChangeRoleRequest {
  role: Role;
}

// ---- Group ----
export interface GroupRequest {
  name: string;
  description?: string;
}
export interface GroupResponse {
  id: number;
  name: string;
  description: string | null;
  createdAt: string;
}

// ---- Project ----
export interface ProjectRequest {
  name: string;
  description?: string;
}
export interface ProjectResponse {
  id: number;
  name: string;
  description: string | null;
  createdAt: string;
}

// ---- ProjectTable ----
export interface ProjectTableRequest {
  tableName: string;
}
export interface ProjectTableResponse {
  id: number;
  tableName: string;
  projectId: number;
  projectName: string;
  createdAt: string;
}

// ---- Query definition tree (surukle-birak JSON) ----
export interface LiteralValue {
  kind: "LITERAL";
  value: string | number | boolean;
}
export interface RelativeDateValue {
  kind: "RELATIVE_DATE";
  direction: RelativeDateDirection;
  amount: number;
  unit: RelativeDateUnit;
}
export type ConditionValue = LiteralValue | RelativeDateValue;

export interface ConditionNode {
  type: "CONDITION";
  field: string;
  operator: ConditionOperator;
  value: ConditionValue | null;
}
export interface GroupNode {
  type: "GROUP";
  logic: LogicOperator;
  children: QueryNode[];
}
export type QueryNode = GroupNode | ConditionNode;

// ---- Query ----
export interface QueryRequest {
  name: string;
  frequency: Frequency;
  definition: QueryNode;
}
export interface QueryResponse {
  id: number;
  name: string;
  frequency: Frequency;
  active: boolean;
  projectId: number;
  projectTableId: number;
  tableName: string;
  definition: QueryNode;
  createdAt: string;
  createdByUsername: string | null;
}
export interface CountResponse {
  count: number;
}

// ---- Alert ----
export interface AlertConditionValue {
  metric: AlertMetric;
  operator: ConditionOperator;
  value: number;
}
export interface AlertRequest {
  groupId: number;
  condition: AlertConditionValue;
}
export interface AlertResponse {
  id: number;
  queryId: number;
  projectId: number;
  groupId: number;
  groupName: string;
  condition: AlertConditionValue;
  active: boolean;
  createdAt: string;
}
export interface AlertEvaluationResult {
  triggered: boolean;
  matchCount: number;
}
export interface AlertLogResponse {
  id: number;
  status: LogStatus;
  message: string;
  executedAt: string;
}

// ---- Generic table row (connector) ----
export type TableRow = Record<string, unknown>;

// ---- Error shape ----
export interface ErrorResponse {
  status: number;
  message: string;
  timestamp: string;
}
