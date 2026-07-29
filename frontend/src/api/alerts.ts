import { api } from "./client";
import type { AlertEvaluationResult, AlertLogResponse, AlertRequest, AlertResponse } from "../types/api";

const base = (projectId: string, tableId: string, queryId: string) =>
  `/api/projects/${projectId}/tables/${tableId}/queries/${queryId}/alerts`;

export const alertsApi = {
  list: (projectId: string, tableId: string, queryId: string) =>
    api.get<AlertResponse[]>(base(projectId, tableId, queryId)),
  create: (projectId: string, tableId: string, queryId: string, body: AlertRequest) =>
    api.post<AlertResponse>(base(projectId, tableId, queryId), body),
  remove: (projectId: string, tableId: string, queryId: string, alertId: string) =>
    api.del<void>(`${base(projectId, tableId, queryId)}/${alertId}`),
  evaluate: (projectId: string, tableId: string, queryId: string, alertId: string) =>
    api.get<AlertEvaluationResult>(`${base(projectId, tableId, queryId)}/${alertId}/evaluate`),
  logs: (projectId: string, tableId: string, queryId: string, alertId: string) =>
    api.get<AlertLogResponse[]>(`${base(projectId, tableId, queryId)}/${alertId}/logs`),
};
