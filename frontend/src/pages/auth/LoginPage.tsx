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
          <strong className="auth-card__brand-text">gözcü</strong>
        </div>
        <h1 className="auth-card__title">Hoş Geldiniz!</h1>
        <p className="auth-card__subtitle">Devam etmek için giriş yapın.</p>

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
          Hesabınız yok mu? <Link to="/register">Kayıt olun</Link>
        </div>

        <div className="auth-card__footer-logo">
          <img src="/turksat_logotip_cmyk-01.png" alt="Türksat" />
        </div>
      </Card>
    </div>
  );
}
