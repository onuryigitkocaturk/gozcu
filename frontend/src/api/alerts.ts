import { api } from "./client";
import type { AlertEvaluationResult, AlertLogResponse, AlertRequest, AlertResponse } from "../types/api";

const base = (projectId: number, tableId: number, queryId: number) =>
  `/api/projects/${projectId}/tables/${tableId}/queries/${queryId}/alerts`;

export const alertsApi = {
  list: (projectId: number, tableId: number, queryId: number) =>
    api.get<AlertResponse[]>(base(projectId, tableId, queryId)),
  create: (projectId: number, tableId: number, queryId: number, body: AlertRequest) =>
    api.post<AlertResponse>(base(projectId, tableId, queryId), body),
  remove: (projectId: number, tableId: number, queryId: number, alertId: number) =>
    api.del<void>(`${base(projectId, tableId, queryId)}/${alertId}`),
  evaluate: (projectId: number, tableId: number, queryId: number, alertId: number) =>
    api.get<AlertEvaluationResult>(`${base(projectId, tableId, queryId)}/${alertId}/evaluate`),
  logs: (projectId: number, tableId: number, queryId: number, alertId: number) =>
    api.get<AlertLogResponse[]>(`${base(projectId, tableId, queryId)}/${alertId}/logs`),
};
