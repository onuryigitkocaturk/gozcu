import { api } from "./client";
import type { CountResponse, QueryRequest, QueryResponse, TableRow } from "../types/api";

const base = (projectId: number, tableId: number) => `/api/projects/${projectId}/tables/${tableId}/queries`;

export const queriesApi = {
  list: (projectId: number, tableId: number) => api.get<QueryResponse[]>(base(projectId, tableId)),
  create: (projectId: number, tableId: number, body: QueryRequest) =>
    api.post<QueryResponse>(base(projectId, tableId), body),
  remove: (projectId: number, tableId: number, queryId: number) =>
    api.del<void>(`${base(projectId, tableId)}/${queryId}`),
  run: (projectId: number, tableId: number, queryId: number) =>
    api.get<TableRow[]>(`${base(projectId, tableId)}/${queryId}/run`),
  count: (projectId: number, tableId: number, queryId: number) =>
    api.get<CountResponse>(`${base(projectId, tableId)}/${queryId}/count`),
};
