import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { ApiError } from "../../api/client";
import { Button, Card, Input } from "../../components/ui";

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await login({ username, password });
      navigate("/", { replace: true });
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Giriş başarısız oldu.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <Card className="auth-card">
        <div className="auth-card__brand">
          <span className="sidebar__brand-mark">Q</span>
          <strong>Query Monitor</strong>
        </div>
        <h1 className="auth-card__title">Tekrar hoş geldin</h1>
        <p className="auth-card__subtitle">Devam etmek için giriş yap.</p>

        {error && <div className="alert-banner alert-banner--error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <Input
            label="Kullanıcı adı"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoFocus
            required
          />
          <Input
            label="Şifre"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <Button type="submit" variant="primary" disabled={loading} style={{ width: "100%" }}>
            {loading ? "Giriş yapılıyor…" : "Giriş yap"}
          </Button>
        </form>

        <div className="auth-card__footer">
          Hesabın yok mu? <Link to="/register">Kayıt ol</Link>
        </div>
      </Card>
    </div>
  );
}
