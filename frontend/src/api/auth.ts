import { api } from "./client";
import type { LoginRequest, LoginResponse, RegisterRequest, UserResponse, VerifyLoginCodeRequest } from "../types/api";

export const authApi = {
  register: (body: RegisterRequest) => api.post<UserResponse>("/api/auth/register", body),
  login: (body: LoginRequest) => api.post<LoginResponse>("/api/auth/login", body),
  verifyLoginCode: (body: VerifyLoginCodeRequest) => api.post<LoginResponse>("/api/auth/verify-login-code", body),
};
