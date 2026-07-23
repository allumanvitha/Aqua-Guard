/**
 * Aqua Guard: Smart Water Management & Wastage Prevention System
 * ESP32 Firmware
 * 
 * Hardware Requirements:
 * - ESP32 Development Board
 * - Water Flow Sensor (YF-S201) - Pulse-based
 * - Ultrasonic Water Level Sensor (HC-SR04)
 * - Soil Moisture / Water Leak Sensor
 * - 5V Relay Module (for Solenoid Valve control)
 * 
 * Required Libraries (Install via Arduino Library Manager):
 * - Firebase ESP32 Client by Mobizt
 */

#include <WiFi.h>
#include <FirebaseESP32.h>

// --- HARDWARE PIN CONFIGURATION ---
#define FLOW_SENSOR_PIN      4   // GPIO4 (Interrupt-enabled pin for flow sensor)
#define VALVE_RELAY_PIN     12   // GPIO12 (Controls solenoid valve)
#define ULTRASONIC_TRIG_PIN 13   // GPIO13 (HC-SR04 Trigger)
#define ULTRASONIC_ECHO_PIN 14   // GPIO14 (HC-SR04 Echo)
#define LEAK_SENSOR_PIN     34   // GPIO34 (Analog input for leak detection)

// --- WIFI & FIREBASE CONFIGURATION ---
#define WIFI_SSID           "YOUR_WIFI_SSID"
#define WIFI_PASSWORD       "YOUR_WIFI_PASSWORD"
#define FIREBASE_HOST       "YOUR_PROJECT_ID-rtdb.firebaseio.com"
#define FIREBASE_AUTH       "YOUR_DATABASE_SECRET_OR_API_KEY"
#define DEVICE_ID           "device_id_123" // Unique ID for this Aqua Guard unit

// --- TANK DIMENSIONS (for level percentage calculation) ---
#define TANK_DEPTH_CM       100.0  // Distance from sensor to bottom of empty tank (in cm)
#define TANK_FULL_DIST_CM   10.0   // Distance from sensor when tank is full (in cm)

// --- FLOW SENSOR CALIBRATION ---
// YF-S201 flow sensor: Pulse frequency (Hz) = 7.5 * Flow rate (L/min)
const float calibrationFactor = 7.5;
volatile byte pulseCount = 0;
float flowRate = 0.0;
unsigned int flowMilliLiters = 0;
unsigned long totalMilliLiters = 0;
unsigned long oldTime = 0;

// --- LEAK DETECTION THRESHOLD ---
#define LEAK_THRESHOLD      1500  // Analog value (0-4095) above which a leak is registered

// --- SYSTEM STATE ---
bool valveOpen = true;
bool autoMode = true;
bool leakDetected = false;
int waterLevelPercent = 0;
unsigned long lastFirebaseUpdate = 0;
const unsigned long firebaseUpdateInterval = 2000; // 2 seconds

// Firebase objects
FirebaseData fbdo;
FirebaseAuth fbAuth;
FirebaseConfig fbConfig;

// Interrupt Service Routine (ISR) for flow sensor pulses
void IRAM_ATTR pulseCounter() {
  pulseCount++;
}

void setup() {
  Serial.begin(115200);
  
  // Pin Modes
  pinMode(VALVE_RELAY_PIN, OUTPUT);
  pinMode(ULTRASONIC_TRIG_PIN, OUTPUT);
  pinMode(ULTRASONIC_ECHO_PIN, INPUT);
  pinMode(LEAK_SENSOR_PIN, INPUT);
  pinMode(FLOW_SENSOR_PIN, INPUT_PULLUP);

  // Initialize Valve state (Open by default)
  digitalWrite(VALVE_RELAY_PIN, HIGH); // Assuming active-high relay (HIGH = Valve Open)

  // Attach interrupt to Flow Sensor
  attachInterrupt(digitalPinToInterrupt(FLOW_SENSOR_PIN), pulseCounter, FALLING);

  // Connect to Wi-Fi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nConnected to Wi-Fi!");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());

  // Configure Firebase
  fbConfig.host = FIREBASE_HOST;
  fbConfig.signer.tokens.legacy_token = FIREBASE_AUTH;
  Firebase.begin(&fbConfig, &fbAuth);
  Firebase.reconnectWiFi(true);

  // Setup Firebase Stream to listen for Valve Commands from App
  String streamPath = "/devices/" + String(DEVICE_ID) + "/live_status";
  if (!Firebase.beginStream(fbdo, streamPath)) {
    Serial.println("Firebase Stream begin failed: " + fbdo.errorReason());
  } else {
    Serial.println("Firebase Stream started on: " + streamPath);
  }
}

void loop() {
  // 1. Calculate Flow Rate
  if ((millis() - oldTime) > 1000) { // Process every 1 second
    detachInterrupt(digitalPinToInterrupt(FLOW_SENSOR_PIN));
    
    // Calculate flow rate in L/min
    flowRate = ((1000.0 / (millis() - oldTime)) * pulseCount) / calibrationFactor;
    oldTime = millis();
    
    // Calculate volume in mL passed in this second
    flowMilliLiters = (flowRate / 60.0) * 1000.0;
    totalMilliLiters += flowMilliLiters;
    
    pulseCount = 0;
    attachInterrupt(digitalPinToInterrupt(FLOW_SENSOR_PIN), pulseCounter, FALLING);
  }

  // 2. Read Water Level (Ultrasonic Sensor)
  long duration;
  float distance;
  digitalWrite(ULTRASONIC_TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(ULTRASONIC_TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(ULTRASONIC_TRIG_PIN, LOW);
  duration = pulseIn(ULTRASONIC_ECHO_PIN, HIGH);
  distance = (duration * 0.0343) / 2.0; // Speed of sound is 343 m/s

  // Calculate percentage
  if (distance >= TANK_DEPTH_CM) {
    waterLevelPercent = 0;
  } else if (distance <= TANK_FULL_DIST_CM) {
    waterLevelPercent = 100;
  } else {
    waterLevelPercent = (int)(((TANK_DEPTH_CM - distance) / (TANK_DEPTH_CM - TANK_FULL_DIST_CM)) * 100.0);
  }
  waterLevelPercent = constrain(waterLevelPercent, 0, 100);

  // 3. Read Leak Sensor
  int leakRawValue = analogRead(LEAK_SENSOR_PIN);
  leakDetected = (leakRawValue > LEAK_THRESHOLD);

  // 4. Local Fail-Safe Logic (Auto Mode)
  if (autoMode) {
    if (leakDetected) {
      if (valveOpen) {
        Serial.println("ALERT: Leak detected! Shutting off valve immediately.");
        closeValve();
      }
    } else if (waterLevelPercent >= 98) {
      if (valveOpen) {
        Serial.println("ALERT: Tank Full! Shutting off valve to prevent overflow.");
        closeValve();
      }
    }
  }

  // 5. Handle Firebase Stream commands (Non-blocking check)
  handleFirebaseStream();

  // 6. Push Live Status to Firebase RTDB
  if (millis() - lastFirebaseUpdate > firebaseUpdateInterval) {
    lastFirebaseUpdate = millis();
    pushStatusToFirebase();
  }
}

void openValve() {
  valveOpen = true;
  digitalWrite(VALVE_RELAY_PIN, HIGH); // Valve Open
  Serial.println("Valve Opened.");
}

void closeValve() {
  valveOpen = false;
  digitalWrite(VALVE_RELAY_PIN, LOW); // Valve Closed
  Serial.println("Valve Closed.");
}

void handleFirebaseStream() {
  if (Firebase.ready() && Firebase.readStream(fbdo)) {
    if (fbdo.streamAvailable()) {
      String path = fbdo.dataPath();
      if (path == "/valve_open") {
        bool targetValveState = fbdo.boolData();
        if (targetValveState != valveOpen) {
          if (targetValveState) {
            openValve();
          } else {
            closeValve();
          }
        }
      } else if (path == "/auto_mode") {
        autoMode = fbdo.boolData();
        Serial.print("Auto Mode updated: ");
        Serial.println(autoMode ? "ENABLED" : "DISABLED");
      }
    }
  }
}

void pushStatusToFirebase() {
  if (Firebase.ready()) {
    String path = "/devices/" + String(DEVICE_ID) + "/live_status";
    
    FirebaseJson json;
    json.set("flow_rate", flowRate);
    json.set("water_level_pct", waterLevelPercent);
    json.set("leak_detected", leakDetected);
    json.set("valve_open", valveOpen);
    json.set("auto_mode", autoMode);
    json.set("last_seen", (double)millis()); // Simple heartbeat using millis

    if (Firebase.updateNode(fbdo, path, json)) {
      Serial.println("Firebase status updated successfully.");
    } else {
      Serial.println("Firebase update failed: " + fbdo.errorReason());
    }
  }
}
