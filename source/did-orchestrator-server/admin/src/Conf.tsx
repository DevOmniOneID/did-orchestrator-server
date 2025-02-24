import React, { useState, useEffect } from 'react';
import HelpIcon from './icons/HelpIcon';
import showToolTip from "./Tooltip";
import ProgressOverlay from './ProgressOverlay';

interface Config {
  blockchain: { [key: string]: string };
  database: { [key: string]: string };
  services: {
    server: {
      [key: string]: {
        name: string;
        port: number;
        file: string;
      };
    };
    [key: string]: any;
  };
}

const Conf: React.FC = () => {
  const [config, setConfig] = useState<Config | null>(null);
  // isSaving 상태: Save 작업이 진행되는 동안 progress overlay를 표시하기 위한 상태
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    const fetchConfig = async () => {
      try {
        const response = await fetch('/configs');
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const configData = await response.json();
        setConfig(configData);
      } catch (error) {
        console.error('Error fetching config:', error);
      }
    };
  
    fetchConfig();
  }, []);

  // blockchain, database의 값 변경 핸들러 (동적 key)
  const handleConfigChange = (
    section: 'blockchain' | 'database',
    key: string,
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    if (!config) return;
    setConfig({
      ...config,
      [section]: {
        ...config[section],
        [key]: e.target.value,
      },
    });
  };

  // services 내 경로(key) 값 변경 핸들러
  const handleServicesPathChange = (
    key: string,
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    if (!config) return;
    setConfig({
      ...config,
      services: {
        ...config.services,
        [key]: e.target.value,
      },
    });
  };

  // services.server 내 값 변경 핸들러
  const handleServerChange = (
    serverKey: string,
    field: 'name' | 'port' | 'file',
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    if (!config) return;
    setConfig({
      ...config,
      services: {
        ...config.services,
        server: {
          ...config.services.server,
          [serverKey]: {
            ...config.services.server[serverKey],
            [field]: field === 'port' ? Number(e.target.value) : e.target.value,
          },
        },
      },
    });
  };

  // Save 버튼 클릭 시 config state를 JSON으로 전송하고, 완료 전까지 progress overlay 표시
  const handleSave = async () => {
    if (!config) return;
    setIsSaving(true); // 작업 시작 -> overlay 표시
    try {
      const updateResponse = await fetch('/configs', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(config),
      });
      if (!updateResponse.ok) {
        throw new Error(`HTTP error updating config! status: ${updateResponse.status}`);
      }
      console.log('Config saved successfully!');

      // 1초 대기
      await new Promise(resolve => setTimeout(resolve, 1000));

      // /actuator/refresh 호출
      const refreshResponse = await fetch('/actuator/refresh', {
        method: 'POST',
      });
      if (!refreshResponse.ok) {
        throw new Error(`HTTP error refreshing actuator! status: ${refreshResponse.status}`);
      }
      console.log('Actuator refreshed successfully!');

      alert('Config saved successfully!');
      window.location.reload();
    } catch (error) {
      console.error('Error updating config:', error);
    } finally {
      setIsSaving(false); // 작업 종료 -> overlay 숨김
    }
  };

  if (!config) {
    return (
      <ProgressOverlay />
    );
  }

  // services 내 server 외의 key (경로들)
  const servicePathKeys = Object.keys(config.services).filter(
    key => key !== 'server'
  );

  return (
    <>
      <div className="bg-gray-100 min-h-screen">
        <div className="flex">
          {/* Sidebar */}
          <aside className="w-64 bg-gray-900 text-white flex flex-col border-r border-gray-300 min-h-screen">
            <div className="p-6 text-lg font-bold">OmniOne OpenDID Orchestrator</div>
            <nav className="flex-1">
              <a href="/" className="block py-3 px-6 bg-gray-800">
                Dashboard
              </a>
              <a href="/conf" className="block py-3 px-6 bg-orange-500">
                Configuration
              </a>
            </nav>
            <div className="p-6 text-sm text-gray-400"></div>
          </aside>

          {/* Main Content */}
          <main className="flex-1 p-6">
            <header className="flex justify-between items-center mb-6">
              <h1 className="text-2xl font-bold">Configuration</h1>
            </header>

            <section className="bg-white p-6 rounded shadow mb-6">
              <h2 className="text-xl font-bold mb-4">Repositories</h2>
              {/* Blockchain Section */}
              <div className="mb-6">
                <h3 className="text-lg font-bold mb-4">
                  Blockchain
                  <button
                    onClick={(e) =>
                      showToolTip(
                        "The following settings are parameters applied when running Hyperledger Fabric. <br>Their meanings are as follows: <br> - Channel: The name of the channel created when running Fabric.<br> - ChaincodeName: The name of the chaincode deployed when running Fabric.",
                        e
                      )
                    }
                    className="text-gray-500 hover:text-gray-700 ml-1"
                  >
                    <HelpIcon width="0.9em" height="0.9em" />
                  </button>
                </h3>
                <table className="min-w-full">
                  <thead>
                    <tr className="bg-gray-200">
                      <th className="p-2 text-left w-48">Key</th>
                      <th className="p-2 text-left w-96">Value</th>
                    </tr>
                  </thead>
                  <tbody>
                    {Object.entries(config.blockchain).map(([key, value]) => (
                      <tr key={key} className="border-b">
                        <td className="p-2 font-bold capitalize">{key}</td>
                        <td className="p-2">
                          <input
                            type="text"
                            className="border rounded p-2 w-full"
                            value={value}
                            onChange={(e) => handleConfigChange('blockchain', key, e)}
                          />
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Database Section */}
              <div className="mb-6">
                <h3 className="text-lg font-bold mb-4">
                  Database
                  <button className="text-gray-500 hover:text-gray-700 ml-1">
                    <HelpIcon width="0.9em" height="0.9em" />
                  </button>
                </h3>
                <table className="min-w-full">
                  <thead>
                    <tr className="bg-gray-200">
                      <th className="p-2 text-left w-48">Key</th>
                      <th className="p-2 text-left w-96">Value</th>
                    </tr>
                  </thead>
                  <tbody>
                    {Object.entries(config.database).map(([key, value]) => (
                      <tr key={key} className="border-b">
                        <td className="p-2 font-bold capitalize">{key}</td>
                        <td className="p-2">
                          <input
                            type="text"
                            className="border rounded p-2 w-full"
                            value={value}
                            onChange={(e) => handleConfigChange('database', key, e)}
                          />
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>

            {/* Services Section */}
            <section className="bg-white p-6 rounded shadow mb-6">
              <h2 className="text-xl font-bold mb-4">Servers</h2>
              {/* Server Configuration */}
              <div className="mb-6">
                <h3 className="text-lg font-bold mb-2">
                  Server
                  <button className="text-gray-500 hover:text-gray-700 ml-1">
                    <HelpIcon width="0.9em" height="0.9em" />
                  </button>
                </h3>
                <table className="min-w-full">
                  <thead>
                    <tr className="bg-gray-200">
                      <th className="p-2 text-left w-48">Service</th>
                      <th className="p-2 text-left w-48">Name</th>
                      <th className="p-2 text-left w-48">Port</th>
                      <th className="p-2 text-left w-48">File</th>
                    </tr>
                  </thead>
                  <tbody>
                    {Object.entries(config.services.server).map(([key, service]) => (
                      <tr key={key} className="border-b">
                        <td className="p-2 font-bold capitalize">{key}</td>
                        <td className="p-2">
                          <input
                            type="text"
                            className="border rounded p-2 w-full"
                            value={service.name}
                            onChange={(e) => handleServerChange(key, 'name', e)}
                          />
                        </td>
                        <td className="p-2">
                          <input
                            type="text"
                            className="border rounded p-2 w-full"
                            value={service.port}
                            onChange={(e) => handleServerChange(key, 'port', e)}
                          />
                        </td>
                        <td className="p-2">
                          <input
                            type="text"
                            className="border rounded p-2 w-full"
                            value={service.file}
                            onChange={(e) => handleServerChange(key, 'file', e)}
                          />
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Service Paths Configuration */}
              <div>
                <h3 className="text-lg font-bold mb-2">
                  Service Paths
                  <button className="text-gray-500 hover:text-gray-700 ml-1">
                    <HelpIcon width="0.9em" height="0.9em" />
                  </button>
                </h3>
                <table className="min-w-full">
                  <thead>
                    <tr className="bg-gray-200">
                      <th className="p-2 text-left w-48">Key</th>
                      <th className="p-2 text-left w-96">Value</th>
                    </tr>
                  </thead>
                  <tbody>
                    {servicePathKeys.map((key) => (
                      <tr key={key} className="border-b">
                        <td className="p-2 font-bold capitalize">{key}</td>
                        <td className="p-2">
                          <input
                            type="text"
                            className="border rounded p-2 w-full"
                            value={config.services[key]}
                            onChange={(e) => handleServicesPathChange(key, e)}
                          />
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>

            {/* Save Button */}
            <div className="flex justify-end space-x-2 mt-6">
              <button
                className="bg-orange-500 text-white px-4 py-2 rounded"
                onClick={handleSave}
              >
                Save
              </button>
            </div>
          </main>
        </div>
      </div>

      {/* Progress Overlay Modal */}
      {isSaving && <ProgressOverlay />}
    </>
  );
};

export default Conf;
