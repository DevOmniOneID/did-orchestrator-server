import React from 'react';

const ProgressOverlay: React.FC = () => {
  return (
    <div className="fixed inset-0 bg-gray-900 bg-opacity-50 flex items-center justify-center z-50">
      <img src="https://i.gifer.com/ZZ5H.gif" alt="Loading..." className="w-16 h-16" />
    </div>
  );
};

export default ProgressOverlay;