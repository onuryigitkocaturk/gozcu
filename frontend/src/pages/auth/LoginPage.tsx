import { useEffect, useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth, prefetchGeolocation } from "../../context/AuthContext";
import { ApiError } from "../../api/client";
import { Button, Card, Input } from "../../components/ui";

export function LoginPage() {
  const { login, verifyLoginCode } = useAuth();
  const navigate = useNavigate();

  // Tarayicinin konum izni diyalogu, kullanici formu doldururken zaten
  // acilsin diye - "Giris yap"a basildigi ana kadar beklemek, kullaniciya
  // dusunecek/cevap verecek yeterli zaman birakmiyordu.
  useEffect(() => {
    prefetchGeolocation();
  }, []);

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [code, setCode] = useState("");
  const [awaitingCode, setAwaitingCode] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const { verificationRequired } = await login({ username, password });
      if (verificationRequired) {
        setAwaitingCode(true);
      } else {
        navigate("/", { replace: true });
      }
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Giriş başarısız oldu.");
    } finally {
      setLoading(false);
    }
  };

  const handleVerify = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await verifyLoginCode(code);
      navigate("/", { replace: true });
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Kod doğrulanamadı.");
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

        {!awaitingCode ? (
          <>
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
          </>
        ) : (
          <>
            <h1 className="auth-card__title">Doğrulama kodu</h1>
            <p className="auth-card__subtitle">
              Bu bilinmeyen bir cihaz — mailine gönderdiğimiz 6 haneli kodu gir.
            </p>

            {error && <div className="alert-banner alert-banner--error">{error}</div>}

            <form onSubmit={handleVerify}>
              <Input
                label="Doğrulama kodu"
                value={code}
                onChange={(e) => setCode(e.target.value)}
                autoFocus
                required
                inputMode="numeric"
                maxLength={6}
              />
              <Button type="submit" variant="primary" disabled={loading} style={{ width: "100%" }}>
                {loading ? "Doğrulanıyor…" : "Doğrula"}
              </Button>
            </form>

            <div className="auth-card__footer">
              <button
                type="button"
                className="link-button"
                onClick={() => {
                  setAwaitingCode(false);
                  setCode("");
                  setError(null);
                }}
              >
                Geri dön
              </button>
            </div>
          </>
        )}

        <div className="auth-card__footer-logo">
          <img src="/turksat_logotip_cmyk-01.png" alt="Türksat" />
        </div>
      </Card>
    </div>
  );
}
