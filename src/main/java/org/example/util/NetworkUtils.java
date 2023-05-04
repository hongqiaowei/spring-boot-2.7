/*
 *  Copyright (C) 2023 Hong Qiaowei <hongqiaowei@163.com>. All rights reserved.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Hong Qiaowei
 */
public abstract class NetworkUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkUtils.class);


    private static final long         max_server_id = 8191;

    private static       long         server_id     = -1;

    private static       String       mac;

    private static       String       server_ip;

    private static final List<String> server_ips    = new ArrayList<>();


    private NetworkUtils() {
    }

    public static String getServerIp() {
        if (server_ip == null) {
            server_ip = getServerIps().get(0);
        }
        return server_ip;
    }

    public static List<String> getServerIps() {
        if (server_ips.isEmpty()) {
            try {
                Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
                while (nis.hasMoreElements()) {
                    NetworkInterface ni = (NetworkInterface) nis.nextElement();
                    if (ni.isLoopback() || ni.isVirtual() || !ni.isUp()) {
                        continue;
                    }
                    Enumeration<InetAddress> ias = ni.getInetAddresses();
                    while (ias.hasMoreElements()) {
                        InetAddress ia = ias.nextElement();
                        if (ia.isSiteLocalAddress()) {
                            server_ips.add(ia.getHostAddress());
                        }
                    }
                }
                if (server_ips.isEmpty()) {
                    InetAddress ia = InetAddress.getLocalHost();
                    server_ips.add(ia.getHostAddress());
                }
            } catch (SocketException | UnknownHostException e) {
                LOGGER.error("get server ips error", e);
                throw new RuntimeException(e);
            }
        }
        return server_ips;
    }

    public static String getServerMac() {
        if (mac == null) {
            StringBuilder b = new StringBuilder();
            try {
                Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
                while (nis.hasMoreElements()) {
                    NetworkInterface ni = nis.nextElement();
                    if (ni.isLoopback() || ni.isVirtual() || !ni.isUp()) {
                        continue;
                    }
                    byte[] mac = ni.getHardwareAddress();
                    if (mac != null) {
                        for (byte value : mac) {
                            b.append(String.format("%02X", value));
                        }
                        break;
                    }
                }
            } catch (SocketException e) {
                LOGGER.error("get server mac error", e);
                throw new RuntimeException(e);
            }
            if (b.length() == 0) {
                String msg = "cant find server interface";
                LOGGER.error(msg);
                throw ThrowableUtils.runtimeExceptionWithoutStack(msg);
            }
            mac = b.toString();
        }
        return mac;
    }

    /**
     * id范围[0,8191]
     * TODO: 改进
     */
    public static long getServerId() {
        if (server_id == -1) {
            String mac = getServerMac();
            long longMac = Long.parseLong(mac, 16);
            server_id = longMac % (max_server_id + 1);
            // server_id = (mac.hashCode()) & max_server_id;
            LOGGER.info("current server id is {}", server_id);
        }
        return server_id;
    }
}
