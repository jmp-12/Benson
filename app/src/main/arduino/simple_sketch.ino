#include "variant.h"
#include <stdio.h>
#include <adk.h>

#define  LED_PIN  13

// Accessory description.
char descriptionName[] = "ArduinoADK_2";
char modelName[] = "UDOO_ADK";
char manufacturerName[] = "Aidilab";

// Make up anything you want for these
char versionNumber[] = "1.0";
char serialNumber[] = "1";
char url[] = "http://www.udoo.org";

USBHost Usb;
ADK adk(&Usb, manufacturerName, modelName, descriptionName, versionNumber, url, serialNumber);

#define RCVSIZE 128
uint8_t buf[RCVSIZE];
uint32_t bytesRead = 0;

void setup() {

    Serial.begin(115200);   
    pinMode(LED_PIN, OUTPUT);

}

void loop() {

    Usb.Task();
     
    if (adk.isReady()) {

      adk.read(&bytesRead, RCVSIZE, buf);

      if (bytesRead > 0) {

        if (parseCommand(buf[0]) == 1) {

          digitalWrite(LED_PIN, HIGH);

        } else if (parseCommand(buf[0]) == 0) {

          digitalWrite(LED_PIN, LOW);

        }

      }

    } else {

      digitalWrite(LED_PIN , LOW);

    }  
    
    delay(10);

}

// the characters sent to Arduino are interpreted as ASCII, we decrease 48 to return to ASCII range.
uint8_t parseCommand(uint8_t received) {

  return received - 48;

}
