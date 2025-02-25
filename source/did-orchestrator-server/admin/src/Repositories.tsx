import React, { useState, forwardRef, useImperativeHandle, useEffect } from "react";
import ProgressIcon from "./icons/ProgressIcon";

interface Repository {
  id: string;
  name: string;
  // "⚪", "🟢", "🔴"와 진행 중일 경우 "PROGRESS" 값을 사용합니다.
  status: string;
}

interface RepositoriesProps {}

const defaultRepos: Repository[] = [
  { id: "fabric", name: "Hyperledger Fabric", status: "⚪" },
  { id: "postgre", name: "PostgreSQL", status: "⚪" },
];

const Repositories = forwardRef((props: RepositoriesProps, ref) => {
  // 초기 상태는 localStorage에서 불러오고, 없으면 defaultRepos 사용
  const [repositories, setRepositories] = useState<Repository[]>(() => {
    const stored = localStorage.getItem("repositories");
    if (stored) {
      try {
        return JSON.parse(stored) as Repository[];
      } catch (e) {
        console.error("Error parsing repositories from localStorage", e);
        return defaultRepos;
      }
    }
    return defaultRepos;
  });

  // repositories 상태가 변경될 때마다 localStorage에 저장
  useEffect(() => {
    localStorage.setItem("repositories", JSON.stringify(repositories));
  }, [repositories]);

  // fromUser가 true이면 사용자 직접 호출로 간주하여 진행 중 체크를 합니다.
  const healthCheck = async (repoId: string, fromUser: boolean = false) => {
    const currentRepo = repositories.find((repo) => repo.id === repoId);
    if (fromUser && currentRepo && currentRepo.status === "PROGRESS") {
      alert("The operation is currently in progress. Please try again later.");
      return;
    }

    /*
    setRepositories((prevRepos) =>
      prevRepos.map((repo) =>
        repo.id === repoId ? { ...repo, status: "PROGRESS" } : repo
      )
    );
    */

    try {
      const response = await fetch(`/healthcheck/${repoId}`, {
        method: "GET",
      });
      if (!response.ok) {
        throw new Error(`Failed to fetch health status for ${repoId}`);
      }
      const data = await response.json();
      setRepositories((prevRepos) =>
        prevRepos.map((repo) =>
          repo.id === repoId
            ? { ...repo, status: data.status === "UP" ? "🟢" : "🔴" }
            : repo
        )
      );
    } catch (error) {
      console.error("Error checking repository status:", error);
      setRepositories((prevRepos) =>
        prevRepos.map((repo) =>
          repo.id === repoId ? { ...repo, status: "🔴" } : repo
        )
      );
    }
  };

  const startRepository = async (repoId: string, fromUser: boolean = false) => {
    const currentRepo = repositories.find((repo) => repo.id === repoId);
    if (fromUser && currentRepo && currentRepo.status === "PROGRESS") {
      alert("The operation is currently in progress. Please try again later.");
      return;
    }

    setRepositories((prevRepos) =>
      prevRepos.map((repo) =>
        repo.id === repoId ? { ...repo, status: "PROGRESS" } : repo
      )
    );

    try {
      const response = await fetch(`/startup/${repoId}`, {
        method: "GET",
      });
      if (response.ok) {
        console.log(`Repository ${repoId} started successfully`);
      } else {
        console.error(`Failed to start repository ${repoId}`);
      }
    } catch (error) {
      console.error("Error starting repository:", error);
    }

    // 내부 호출에서는 fromUser를 false로 전달하여 진행 중 체크를 건너뜁니다.
    await healthCheck(repoId, false);
  };

  const stopRepository = async (repoId: string, fromUser: boolean = false) => {
    const currentRepo = repositories.find((repo) => repo.id === repoId);
    if (fromUser && currentRepo && currentRepo.status === "PROGRESS") {
      alert("The operation is currently in progress. Please try again later.");
      return;
    }

    setRepositories((prevRepos) =>
      prevRepos.map((repo) =>
        repo.id === repoId ? { ...repo, status: "PROGRESS" } : repo
      )
    );

    try {
      const response = await fetch(`/shutdown/${repoId}`, {
        method: "GET",
      });
      if (response.ok) {
        console.log(`Repository ${repoId} stopped successfully`);
      } else {
        console.error(`Failed to stop repository ${repoId}`);
      }
    } catch (error) {
      console.error("Error stopping repository:", error);
    }

    // 내부 호출에서는 fromUser를 false로 전달하여 진행 중 체크를 건너뜁니다.
    await healthCheck(repoId, false);
  };

  // 전체 상태를 결정하는 함수
  const getOverallStatus = async (): Promise<string> => {
    await new Promise((resolve) => setTimeout(resolve, 500));

    const statuses = repositories.map((repo) => repo.status);
    const allGreen = statuses.every((status) => status === "🟢");
    const allRed = statuses.every((status) => status === "🔴");

    if (allGreen) {
      return "SUCCESS";
    } else if (allRed) {
      return "FAIL";
    } else if (statuses.some((status) => status === "🟢")) {
      return "PARTIAL";
    }
    return "FAIL";
  };

  // 모든 리포지토리에 대해 healthCheck를 실행하여 상태를 업데이트하고 전체 상태를 반환하는 함수
  const statusAll = async (): Promise<string> => {
    for (const repo of repositories) {
      await healthCheck(repo.id);
    }

    await Promise.all(repositories.map((repo) => healthCheck(repo.id)));
    return getOverallStatus();
  };

  const startAll = async () => {
    for (const repo of repositories) {
      await startRepository(repo.id);
    }
  };

  const stopAll = async () => {
    for (let i = repositories.length - 1; i >= 0; i--) {
      const repo = repositories[i];
      await stopRepository(repo.id);
    }
  };

  useImperativeHandle(ref, () => ({
    getOverallStatus,
    startAll,
    stopAll,
    statusAll,
  }));

  return (
    <section className="bg-white p-6 rounded shadow mb-6">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-bold">Repositories</h2>
      </div>
      <table className="w-full text-left border-collapse">
        <thead>
          <tr className="bg-gray-200">
            <th className="p-2 w-20">Status</th>
            <th className="p-2 w-56">Name</th>
            <th className="p-2 w-96">Actions</th>
            <th className="p-2 w-56">Info</th>
          </tr>
        </thead>
        <tbody className="server-table">
          {repositories.map((repo) => (
            <tr key={repo.id} className="border-b">
              <td className="p-2 pl-6">
                {repo.status === "PROGRESS" ? <ProgressIcon /> : repo.status}
              </td>
              <td className="p-2 font-bold"
                  onClick={() => window.open(`/logs/${repo.id}.log`)}
              >
                {repo.name}
              </td>
              <td className="p-2">
                <div className="flex space-x-1">
                  {/* onClick 핸들러에서는 단순히 함수 호출만 합니다. */}
                  <button
                    className="bg-green-600 text-white px-3 py-1 rounded"
                    onClick={() => startRepository(repo.id, true)}
                  >
                    Start
                  </button>
                  <button
                    className="bg-red-600 text-white px-3 py-1 rounded"
                    onClick={() => stopRepository(repo.id, true)}
                  >
                    Stop
                  </button>
                  <button
                    className="bg-gray-600 text-white px-3 py-1 rounded"
                    onClick={() => healthCheck(repo.id, true)}
                  >
                    Status
                  </button>
                </div>
              </td>
              <td className="p-2"></td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
});

export default Repositories;
