import { useEffect, useState } from 'react';

export function Clock({ multiplier }) {
  const [time, setTime] = useState(new Date());

  useEffect(() => {
    const id = setInterval(() => {
      setTime(prev => new Date(prev.getTime() + 1000 * multiplier));
    }, 1000);
    return () => clearInterval(id);
  }, [multiplier]);

  return (
    <div className="clock">
      <i className="pi pi-clock" />
      <span className="clock-time">
        {time.toLocaleTimeString('en-US', { hour12: true })}
      </span>
      <span className="clock-multiplier">{multiplier}x</span>
    </div>
  );
}