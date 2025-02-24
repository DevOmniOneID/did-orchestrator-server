// Tooltip.tsx
import React from "react";

interface TooltipProps {
  content: string;
  event: React.MouseEvent<HTMLButtonElement>;
}

const showToolTip = (content: string, event: React.MouseEvent<HTMLButtonElement>) => {
  event.preventDefault();
  const target = event.currentTarget;
  const rect = target.getBoundingClientRect();

  // Tooltip 생성
  const tooltip = document.createElement("div");
  tooltip.className = "absolute bg-gray-700 text-white text-xs rounded py-1 px-2";
  tooltip.style.position = "absolute";
  tooltip.style.zIndex = "1000";
  tooltip.innerHTML = content;

  // 위치 설정
  const top = rect.bottom + window.scrollY + 5;
  const left = rect.left + window.scrollX;
  tooltip.style.top = `${top}px`;
  tooltip.style.left = `${left}px`;

  // Tooltip 추가
  document.body.appendChild(tooltip);

  // 3초 후 자동 제거
  setTimeout(() => tooltip.remove(), 3000);
};

export default showToolTip;
