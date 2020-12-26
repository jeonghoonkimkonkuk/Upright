#include <Arduino_LSM9DS1.h>
#include <ArduinoBLE.h>

//통신 방식 설정 (uuid 설정등)
BLEService accelscopeService("0000fff0-0000-1000-8000-00805f9b34fb");       
BLECharacteristic accelscopeChar("00002902-0000-1000-8000-00805f9b34fb",BLERead | BLENotify, 32, (1==1));

long previousMillis = 0;
char buf[32];

const double RADIAN_TO_DEGREE = 180 / 3.14159;

void setup() {
  Serial.begin(9600);    
  //while (!Serial);

  pinMode(LED_BUILTIN, OUTPUT); 

  // begin initialization
  if (!BLE.begin()) {
    Serial.println("starting BLE failed!");
    while (1);
  }

  // IMU센서를 초기화합니다. 초기화중 문제가 발생하면 오류를 발생시킵니다.
  if (!IMU.begin()) {
    Serial.println("Failed to initialize IMU!");
    while (1);
  }

  BLE.setLocalName("Upright");  
  BLE.setAdvertisedService(accelscopeService);
  accelscopeService.addCharacteristic(accelscopeChar); 
  BLE.addService(accelscopeService); 
  accelscopeChar.writeValue("oldxyz"); 
  BLE.advertise();

  Serial.println(accelscopeService.uuid());
  Serial.println("Bluetooth device active, waiting for connections...");
}



// 각 센서별로 XYZ값을 저장할 변수
int timePeriod = 1000;       //업데이트 주기 (ms단위)
float accel_x,accel_y,accel_z;
double bf,lr;
void loop() {
  BLEDevice central = BLE.central();

  if (central) {
    Serial.print("Connected to central: ");
    Serial.println(central.address());
   
    while (central.connected()) {
      long currentMillis = millis();
      //timePeriod 초마다 자이로 센서값 업데이
      if (currentMillis - previousMillis >= timePeriod) {
        previousMillis = currentMillis;
        updateAccelscope();
      }
    }
    
    Serial.print("Disconnected from central: ");
    Serial.println(central.address());
  }
}

void updateAccelscope() {
  if (IMU.accelerationAvailable()) {
    IMU.readAcceleration(accel_x, accel_y, accel_z);
    
    //String accelPacket = String(accel_x) + "," + String(accel_y) + "," + String(accel_z);
    //accelPacket.toCharArray(buf,32);
    //Serial.println(accelPacket);

    if(abs(pow(accel_x,2)+pow(accel_y,2)+pow(accel_z,2) - 1)< 0.2){

    
      if(accel_z>1){
        accel_z = 1;
      }
      if(accel_z<-1){
        accel_z = -1;
      }
      bf = acos(accel_z);
      bf *= RADIAN_TO_DEGREE;


      double lrv = accel_y / sqrt(pow(accel_x,2)+pow(accel_y,2));
      if(lrv>1){
        lrv = 1;
      }
      if(lrv<-1){
        lrv = -1;
      }
      lr = acos(lrv);
      lr *= RADIAN_TO_DEGREE;

      String accelPacket = String(bf)+","+String(lr);
      accelPacket.toCharArray(buf,32);
      Serial.println(accelPacket);
      accelscopeChar.writeValue(buf, 32);
    }
    //Serial.println(String(accel_x)+", "+String(accel_y)+", "+String(accel_z));
  // 문자열을 블루투스 신호로 보냅니다.
    //accelscopeChar.writeValue(buf, 32);
  }
}
