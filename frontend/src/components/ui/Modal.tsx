import type { ReactNode } from "react";
import { animated, useTransition } from "@react-spring/web";
import { Button } from "./Button";

export function Modal({
  open,
  onClose,
  title,
  children,
  footer,
}: {
  open: boolean;
  onClose: () => void;
  title: string;
  children: ReactNode;
  footer?: ReactNode;
}) {
  const transitions = useTransition(open, {
    from: { opacity: 0, transform: "translateY(-8px) scale(0.98)" },
    enter: { opacity: 1, transform: "translateY(0px) scale(1)" },
    leave: { opacity: 0, transform: "translateY(-8px) scale(0.98)" },
    config: { tension: 340, friction: 28 },
  });

  return transitions(
    (style, item) =>
      item && (
        <div
          className="modal-overlay"
          onMouseDown={(e) => {
            if (e.target === e.currentTarget) onClose();
          }}
        >
          <animated.div className="modal" style={style}>
            <div className="modal__header">
              <div className="modal__title">{title}</div>
              <Button variant="ghost" size="sm" onClick={onClose} aria-label="Kapat">
                ✕
              </Button>
            </div>
            <div className="modal__body">{children}</div>
            {footer && <div className="modal__footer">{footer}</div>}
          </animated.div>
        </div>
      ),
  );
}
