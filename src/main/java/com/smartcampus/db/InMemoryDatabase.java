package com.smartcampus.db;

import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDatabase {
    // Singleton instance for data sharing across requests
    private static final InMemoryDatabase instance = new InMemoryDatabase();

    // Data structures for storing our records
    // We use ConcurrentHashMap to handle race conditions since JAX-RS Resource classes
    // are typically instantiated per request by default and thus multiple threads can access these maps simultaneously.
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();

    private InMemoryDatabase() {
        // Initialize with some dummy data if needed, or leave empty
    }

    public static InMemoryDatabase getInstance() {
        return instance;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Map<String, Sensor> getSensors() {
        return sensors;
    }

    public Map<String, List<SensorReading>> getSensorReadings() {
        return sensorReadings;
    }
}
