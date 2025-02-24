import React, { useEffect, useState } from "react";
import ReactMarkdown from "react-markdown";

const HelpMarkdown: React.FC = () => {
  const [markdownContent, setMarkdownContent] = useState<string>("");

  useEffect(() => {
    fetch("https://raw.githubusercontent.com/DevOmniOneID/did-orchestrator-frontend/refs/heads/main/readme.md")
      .then((res) => res.text())
      .then((text) => setMarkdownContent(text))
      .catch((err) => console.error("Error fetching markdown:", err));
  }, []);

  return (
    // prose 클래스를 적용하여 요소 간 간격과 타이포그래피가 명확하게 표시되도록 함
    <div className="prose prose-sm sm:prose-base lg:prose-lg xl:prose-xl" style={{ fontSize: "80%" }}>
      <ReactMarkdown>{markdownContent}</ReactMarkdown>
    </div>
  );
};

const Help: React.FC = () => {
  return (
    <div className="fixed inset-0 bg-gray-900 bg-opacity-50 flex items-center justify-center">
      <div className="bg-white w-[1000px] max-h-[80vh] p-6 rounded-lg shadow-lg overflow-y-auto">
        <h2 className="text-lg font-bold border-b pb-2 mb-4">
          OmniOne OpenDID Orchestrator Prerequisites
        </h2>
        <HelpMarkdown />
        <div className="flex justify-end mt-6">
          <button
            type="button"
            onClick={() => window.close()}
            className="px-4 py-2 bg-orange-500 text-white rounded"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
};

export default Help;
