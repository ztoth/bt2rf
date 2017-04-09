/*
 *------------------------------------------------------------------------------
 *
 * bt2rf.ino
 *
 * Converts bluetooth messages to RF signals for a set of RF outlets
 *
 * Copyright (c) 2017 Zoltan Toth <ztoth AT thetothfamily DOT net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *------------------------------------------------------------------------------
 */
#include <SoftwareSerial.h>
#include <RCSwitch.h>

/* BT variables */
const int bt_tx_pin = 0;
const int bt_rx_pin = 1;
SoftwareSerial btmodem(bt_tx_pin, bt_rx_pin);

/* RF variables */
RCSwitch rfradio = RCSwitch();
const int rf_tx_pin = 5;

/* RF signal codes */
const int BUTTON_1 = 13390851;
const int BUTTON_2 = 13390860;
const int BUTTON_3 = 13390896;
const int BUTTON_4 = 13391040;
const int BUTTON_5 = 13391616;

/**
 * Setup
 */
void
setup (void)
{
    btmodem.begin(115200);
    rfradio.enableTransmit(rf_tx_pin);
    rfradio.setProtocol(1);
    rfradio.setPulseLength(165);
}

/**
 * Loop
 */
void
loop (void)
{
    if (btmodem.available()) {
        /* convert bluetooth command to RF signal */
        send_rf((char)btmodem.read());
    }
}

/**
 * Send RF signal
 */
void
send_rf (char c)
{
    switch (c) {
    case '1': {
        rfradio.send(BUTTON_1, 24);
        break;
    }

    case '2': {
        rfradio.send(BUTTON_2, 24);
        break;
    }

    case '3': {
        rfradio.send(BUTTON_3, 24);
        break;
    }

    case '4': {
        rfradio.send(BUTTON_4, 24);
        break;
    }

    case '5': {
        rfradio.send(BUTTON_5, 24);
        break;
    }

    default:
        break;
    }
}
