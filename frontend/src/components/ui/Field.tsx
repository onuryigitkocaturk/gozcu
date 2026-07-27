import type { InputHTMLAttributes, ReactNode, SelectHTMLAttributes, TextareaHTMLAttributes } from "react";

interface FieldWrapperProps {
  label?: string;
  error?: string;
  children: ReactNode;
}

export function FieldWrapper({ label, error, children }: FieldWrapperProps) {
  return (
    <div className="field">
      {label && <label className="field__label">{label}</label>}
      {children}
      {error && <span className="field__error">{error}</span>}
    </div>
  );
}

interface InputFieldProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
}

export function Input({ label, error, className, ...rest }: InputFieldProps) {
  return (
    <FieldWrapper label={label} error={error}>
      <input className={["input", className ?? ""].join(" ")} {...rest} />
    </FieldWrapper>
  );
}

interface SelectFieldProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  error?: string;
  children: ReactNode;
}

export function Select({ label, error, className, children, ...rest }: SelectFieldProps) {
  return (
    <FieldWrapper label={label} error={error}>
      <select className={["select", className ?? ""].join(" ")} {...rest}>
        {children}
      </select>
    </FieldWrapper>
  );
}

/** Label/margin olmadan, liste satirlari gibi kompakt yerler icin. */
export function InlineSelect({ className, children, ...rest }: SelectHTMLAttributes<HTMLSelectElement> & { children: ReactNode }) {
  return (
    <select className={["select", className ?? ""].join(" ")} {...rest}>
      {children}
    </select>
  );
}

interface TextareaFieldProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
  error?: string;
}

export function Textarea({ label, error, className, ...rest }: TextareaFieldProps) {
  return (
    <FieldWrapper label={label} error={error}>
      <textarea className={["textarea", className ?? ""].join(" ")} {...rest} />
    </FieldWrapper>
  );
}
