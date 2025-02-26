import React, { useState, useEffect, forwardRef, useImperativeHandle } from "react";
import HelpIcon from "./icons/HelpIcon";
import showToolTip from "./Tooltip";
import ProgressIcon from "./icons/ProgressIcon";
import LogIcon from './icons/LogIcon';
import { CSSTransition } from "react-transition-group";

interface Demo {
  id: string;
  name: string;
  port: number;
  status: string;
}

const defaultDemo: Demo = {
  id: "demo",
  name: "DEMO",
  port: 8099,
  status: "⚪",
};

const Demo = forwardRef((props, ref) => {
  const [demo, setDemo] = useState<Demo>(() => {
    const stored = localStorage.getItem("demo");
    if (stored) {
      try {
        return JSON.parse(stored) as Demo;
      } catch (e) {
        console.error("Error parsing demo from localStorage", e);
        return defaultDemo;
      }
    }
    return defaultDemo;
  });

  const [showDemoActionsAndInfo, setShowDemoActionsAndInfo] = useState(false); // 버튼 표시 여부 상태

  useEffect(() => {
    localStorage.setItem("demo", JSON.stringify(demo));
    shouldRenderDemoActionsAndInfo();
  }, [demo]);

  // 특정 조건에 따라 버튼 영역 활성화 여부를 판단하는 함수
  const shouldRenderDemoActionsAndInfo = async (): Promise<void> => {
    await new Promise((resolve) => setTimeout(resolve, 500));

    var allStatus = localStorage.getItem("allStatus")
    setShowDemoActionsAndInfo(allStatus === "🟢");
  };

  useImperativeHandle(ref, () => ({
    shouldRenderDemoActionsAndInfo,
    startDemo,
    stopDemo,
    healthCheckDemo
  }));

  // DEMO의 상태를 체크하는 함수 (fromUser가 true이면 사용자가 직접 호출한 것으로 판단)
  const healthCheckDemo = async (fromUser: boolean = false) => {
    if (fromUser && demo.status === "PROGRESS") {
      alert("The operation is currently in progress. Please try again later.");
      return;
    }
    /*
    setDemo((prev) => ({ ...prev, status: "PROGRESS" }));
    */
    try {
      const response = await fetch(`/healthcheck/${demo.port}`, { method: "GET" });
      if (!response.ok) {
        throw new Error("Failed to fetch health status");
      }
      const data = await response.json();
      setDemo((prev) => ({
        ...prev,
        status: data.status === "UP" ? "🟢" : "🔴",
      }));
    } catch (error) {
      console.error("Error checking demo status:", error);
      setDemo((prev) => ({ ...prev, status: "🔴" }));
    }
  };

  // DEMO를 시작하는 함수
  const startDemo = async (fromUser: boolean = false) => {
    if (fromUser && demo.status === "PROGRESS") {
      alert("The operation is currently in progress. Please try again later.");
      return;
    }
    setDemo((prev) => ({ ...prev, status: "PROGRESS" }));
    try {
      const response = await fetch(`/startup/${demo.port}`, { method: "GET" });
      if (response.ok) {
        console.log("Demo started successfully");
      } else {
        console.error("Failed to start demo");
      }
    } catch (error) {
      console.error("Error starting demo:", error);
    }
    await healthCheckDemo(false);
  };

  // DEMO를 중지하는 함수
  const stopDemo = async (fromUser: boolean = false) => {
    if (fromUser && demo.status === "PROGRESS") {
      alert("The operation is currently in progress. Please try again later.");
      return;
    }
    setDemo((prev) => ({ ...prev, status: "PROGRESS" }));
    try {
      const response = await fetch(`/shutdown/${demo.port}`, { method: "GET" });
      if (response.ok) {
        console.log("Demo stopped successfully");
      } else {
        console.error("Failed to stop demo");
      }
    } catch (error) {
      console.error("Error stopping demo:", error);
    }
    await healthCheckDemo(false);
  };

  return (
    <section className="bg-white p-6 rounded shadow">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-bold">
          Demo
          <button
            onClick={(e) =>
              showToolTip(
                "You can control the Demo below once all the Entities above are running.<br>The Actions, Info, and other items will be activated at that time.",
                e
              )
            }
            className="text-gray-500 hover:text-gray-700 ml-1"
          >
            <HelpIcon width="0.9em" height="0.9em" />
          </button>
        </h2>
      </div>
      <table className="w-full text-left border-collapse">
        <thead>
          <tr className="bg-gray-200">
            <th className="p-2 w-20">Status</th>
            <th className="p-2 w-56">Name</th>
            <th className="p-2 w-56">Actions</th>
            <th className="p-2 w-48">Info</th>
            <th className="p-2 w-48">Generators</th>
          </tr>
        </thead>
        <tbody className="server-table">
        <tr className="border-b">
          <td className="p-2 pl-6 demo">
            <CSSTransition
                in={showDemoActionsAndInfo}
                timeout={300} // 300ms 애니메이션 지속
                classNames="fade"
                unmountOnExit
            >
              <div>
                {demo.status === "PROGRESS" ? <ProgressIcon/> : demo.status}
              </div>
            </CSSTransition>
          </td>
          <td className="p-2 font-bold">
            <CSSTransition
                in={showDemoActionsAndInfo}
                timeout={300} // 300ms 애니메이션 지속
                classNames="fade"
                unmountOnExit
            >
              <div>
                {demo.name} ({demo.port}) <button onClick={() => window.open(`/logs/server_${demo.port}.log`)}><LogIcon width="0.8em" height="0.8em"/></button>
              </div>
            </CSSTransition>
          </td>
          <td className="p-2">
            <CSSTransition
                in={showDemoActionsAndInfo}
                timeout={300} // 300ms 애니메이션 지속
                classNames="fade"
                unmountOnExit
            >
              <div className="flex space-x-1">
                <button
                    className="bg-green-600 text-white px-3 py-1 rounded"
                    onClick={() => startDemo(true)}
                >
                  Start
                </button>
                <button
                    className="bg-red-600 text-white px-3 py-1 rounded"
                    onClick={() => stopDemo(true)}
                >
                  Stop
                </button>
                <button
                    className="bg-gray-600 text-white px-3 py-1 rounded"
                    onClick={() => healthCheckDemo(true)}
                >
                  Status
                </button>
              </div>
            </CSSTransition>
          </td>
          <td className="p-2">
            <CSSTransition
                in={showDemoActionsAndInfo}
                timeout={300} // 300ms 애니메이션 지속
                classNames="fade"
                unmountOnExit
            >
              <div className="flex space-x-1">
                <button
                    className="bg-gray-600 text-white px-3 py-1 rounded"
                    onClick={() => window.open(`http://localhost:${demo.port}`)}
                >
                  Demo Site
                </button>
              </div>
            </CSSTransition>
          </td>
          <td className="p-2"></td>
        </tr>
        </tbody>
      </table>
    </section>
  );
});

export default Demo;
