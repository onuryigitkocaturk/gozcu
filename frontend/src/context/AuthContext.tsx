import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from "react";
import { authApi } from "../api/auth";
import { usersApi } from "../api/users";
import { setAuthToken } from "../api/client";
import type { LoginRequest, RegisterRequest, UserResponse } from "../types/api";

interface AuthContextValue {
  user: UserResponse | null;
  isAdmin: boolean;
  isAuthenticated: boolean;
  login: (body: LoginRequest) => Promise<void>;
  register: (body: RegisterRequest) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

// Token ve kullanici bilgisi SADECE bellekte (React state) tutulur.
// localStorage/sessionStorage KULLANILMAZ - sayfa yenilenince oturum
// bilerek kapanir (backend'in "access token localStorage'da saklanmaz"
// kuraliyla tutarli).
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null);

  const login = useCallback(async (body: LoginRequest) => {
    const { token } = await authApi.login(body);
    setAuthToken(token);
    try {
      const me = await usersApi.me();
      setUser(me);
    } catch (err) {
      setAuthToken(null);
      throw err;
    }
  }, []);

  const register = useCallback(async (body: RegisterRequest) => {
    await authApi.register(body);
  }, []);

  const logout = useCallback(() => {
    setAuthToken(null);
    setUser(null);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      isAdmin: user?.role === "ADMIN",
      isAuthenticated: user !== null,
      login,
      register,
      logout,
    }),
    [user, login, register, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return ctx;
}
