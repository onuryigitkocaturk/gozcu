import { api } from "./client";
import type { AuthResponse, LoginRequest, RegisterRequest, UserResponse } from "../types/api";

export const authApi = {
  register: (body: RegisterRequest) => api.post<UserResponse>("/api/auth/register", body),
  login: (body: LoginRequest) => api.post<AuthResponse>("/api/auth/login", body),
};
