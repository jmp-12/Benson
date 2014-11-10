#include "variant.h"
#include <stdio.h>
#include <adk.h>

#define  LED_PIN  13

// Accessory descriptor. It's how Arduino identifies itself to Android.
char descriptionName[] = "ArduinoADK_2"; 
char modelName[] = "UDOO_ADK";           // your Arduino Accessory name (Need to be the same defined in the Android App)
char manufacturerName[] = "Aidilab";     // manufacturer (Need to be the same defined in the Android App)

// Make up anything you want for these
char versionNumber[] = "1.0";            // version (Need to be the same defined in the Android App)
char serialNumber[] = "1";
char url[] = "http://www.udoo.org";      // If there isn't any compatible app installed, Android suggest to visit this url

USBHost Usb;
ADK adk(&Usb, manufacturerName, modelName, descriptionName, versionNumber, url, serialNumber);

#define RCVSIZE 128
uint8_t buf[RCVSIZE];
uint32_t bytesRead = 0;

void setup()
{
    Serial.begin(115200);   
    pinMode(LED_PIN, OUTPUT);
    delay(500);
    Serial.println("UDOO ADK demo start...");
}

void loop()
{
    Usb.Task();
     
    if (adk.isReady()) {
      adk.read(&bytesRead, RCVSIZE, buf);// read data into buf variable
      if (bytesRead > 0) {
        if (parseCommand(buf[0]) == 1) {// compare received data
          // Received "1" - turn on LED
          digitalWrite(LED_PIN, HIGH);
        } else if (parseCommand(buf[0]) == 0) {
          // Received "0" - turn off LED
          digitalWrite(LED_PIN, LOW); 
        }  
      }
    } else {
      digitalWrite(LED_PIN , LOW); // turn off light
    }  
    
    delay(10);
}

// the characters sent to Arduino are interpreted as ASCII, we decrease 48 to return to ASCII range.
uint8_t parseCommand(uint8_t received) {
  return received - 48;
}
