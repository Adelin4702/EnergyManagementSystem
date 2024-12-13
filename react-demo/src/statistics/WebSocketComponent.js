import React, { useEffect, useState } from 'react';
import { getDeviceByPersonId } from '../my-device/api/my-device-api';
import { DayPicker } from 'react-day-picker';
import 'react-day-picker/dist/style.css';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Line } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js';
import { format } from 'date-fns';
import styles from './style/style.module.css';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

const WebSocketComponent = () => {
  const [devices, setDevices] = useState([]);
  const [selectedDevice, setSelectedDevice] = useState(null);
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [stompClient, setStompClient] = useState(null);
  const [notifications, setNotifications] = useState([]);
  const [chartData, setChartData] = useState({
    labels: [],
    datasets: [
      {
        label: 'Consumption',
        data: [],
        fill: false,
        borderColor: 'rgb(75, 192, 192)',
        tension: 0.1,
      },
    ],
  });

  const personId = sessionStorage.getItem('personId');

  useEffect(() => {
    if (personId) {
      getDeviceByPersonId(personId, (result, status, err) => {
        if (result !== null && status === 200) {
          setDevices(result);
          setSelectedDevice(result.at(0).id);
        }
      });
    } else {
      console.error('No person ID found in sessionStorage');
    }
  }, [personId]);

  useEffect(() => {
    if (personId) {
      const socket = new SockJS('http://localhost:80/api-monitor/ws');
      const client = new Client({
        webSocketFactory: () => socket,
        connectHeaders: { 'personId': personId },
        onConnect: () => {
          client.subscribe(`/topic/sendConsumption/${personId}`, (message) => {
            setNotifications((prevNotifications) => [...prevNotifications, message.body]);
            updateChartData(message.body);
          });
        },
        onDisconnect: () => console.log("Disconnected from WebSocket server"),
        onStompError: (frame) => console.error("WebSocket error:", frame),
      });
      setStompClient(client);
      client.activate();
    }
  }, [personId]);

  const handleDeviceChange = (event) => {
    setSelectedDevice(event.target.value);
  };

  const handleDateChange = (dateSelected) => {
    setSelectedDate(dateSelected);
  };

  const sendDataToWebSocket = () => {
    selectedDate.setHours(2, 0, 0, 0);
    if (stompClient && stompClient.connected) {
      const message = {
        deviceId: selectedDevice,
        date: selectedDate.toISOString(),
        personId: personId,
      };
      stompClient.publish({
        destination: '/app/sendConsumption',
        body: JSON.stringify(message),
      });
      console.log('--- Sent message: ', message)
    }
  };

  const updateChartData = (messageBody) => {
    try {
      const data = JSON.parse(messageBody);
      const labels = [];
      const values = [];
      const hoursInDay = Array.from({ length: 24 }, (_, i) => i.toString().padStart(2, '0')); // ['00', '01', ..., '23']
      const dataMap = {}; // A map to store values by hour

      // Process data and store values by hour
      data.forEach(item => {
        const date = Object.keys(item)[0];
        const value = item[date];
        const correctDate = new Date(date);
        correctDate.setHours(correctDate.getHours() - 2); // Adjusting timezone
        const formattedDate = format(new Date(correctDate), 'HH:mm');
        const hour = formattedDate.slice(0, 2); // Extracting the hour part (e.g., '00', '01', etc.)

        // Store the value for each hour
        dataMap[hour] = value;
      });

      // Add all hours from 00 to 23 with their corresponding values
      hoursInDay.forEach(hour => {
        labels.push(`${hour}:00`);
        values.push(dataMap[hour] || 0); // If no data for the hour, default to 0
      });

      // Sort labels and values by hour
      const sortedLabelsValues = labels.map((label, index) => ({
        label,
        value: values[index]
      })).sort((a, b) => a.label.localeCompare(b.label));

      setChartData({
        labels: labels,
        datasets: [
          {
            label: 'Consumption',
            data: values,
            fill: false,
            borderColor: 'rgb(75, 192, 192)',
            tension: 0.1,
          },
        ],
      });
    } catch (error) {
      console.error("Error parsing message:", error);
    }
  };

  return (
    <div className={styles.pageContainer}>
      <h2 className={styles.header}>Device and Date Selector</h2>
      <div className={styles.sideBySideContainer}>
        <div className={styles.leftPanel}>
          <div className={styles.deviceSelector}>
            <label htmlFor="deviceSelector" className={styles.deviceLabel}>
              Select Device:
            </label>
            <select
              id="deviceSelector"
              value={selectedDevice || ''}
              onChange={handleDeviceChange}
              disabled={!devices.length}
              className={styles.select}
            >
              {devices.map((device) => (
                <option key={device.id} value={device.id}>
                  {device.description}
                </option>
              ))}
            </select>
          </div>
          <DayPicker
            mode="single"
            selected={selectedDate}
            onSelect={handleDateChange}
            className={styles.datePicker}
          />
          <button onClick={sendDataToWebSocket} disabled={!selectedDate} className={styles.button}>
            View Consumption
          </button>
          {/* <div className={styles.notifications}>
            <h3>Notifications</h3>
            {notifications.length > 0 ? (
              notifications.map((notif, index) => (
                <div key={index} className={styles.notification}>{notif}</div>
              ))
            ) : (
              <p className={styles.noNotifications}>No notifications</p>
            )}
          </div> */}
        </div>
        <div className={styles.rightPanel}>
          <h3>Consumption Chart</h3>
          <Line data={chartData} />
        </div>
      </div>
    </div>
  );
};

export default WebSocketComponent;
