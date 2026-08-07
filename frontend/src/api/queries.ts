import { api } from "./client";
import type { CountResponse, QueryRequest, QueryResponse, TableRow } from "../types/api";

const base = (projectId: string, tableId: string) => `/api/projects/${projectId}/tables/${tableId}/queries`;

export const queriesApi = {
  list: (projectId: string, tableId: string) => api.get<QueryResponse[]>(base(projectId, tableId)),
  create: (projectId: string, tableId: string, body: QueryRequest) =>
    api.post<QueryResponse>(base(projectId, tableId), body),
  update: (projectId: string, tableId: string, queryId: string, body: QueryRequest) =>
    api.put<QueryResponse>(`${base(projectId, tableId)}/${queryId}`, body),
  remove: (projectId: string, tableId: string, queryId: string) =>
    api.del<void>(`${base(projectId, tableId)}/${queryId}`),
  run: (projectId: string, tableId: string, queryId: string) =>
    api.get<TableRow[]>(`${base(projectId, tableId)}/${queryId}/run`),
  count: (projectId: string, tableId: string, queryId: string) =>
    api.get<CountResponse>(`${base(projectId, tableId)}/${queryId}/count`),
};
