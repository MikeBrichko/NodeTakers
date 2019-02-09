import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import { Connector } from 'mqtt-react';

class App extends Component {
  render() {
    return (
      <Connector mqttProps="wss://mr4b11zr953.messaging.mymaas.net:8443, 
      {'username':'solace-cloud-client',
      'password': 'ucaltv4mc6q3kd2qfbibv0bpet'}">
      <div className="App">
        <input type="text" name="topic"></input>

      </div>
      </Connector>
    );
  }
}

export default App;
