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

package org.example.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see HTTP11WebClientConfig
 * @see HTTP2CWebClientConfig
 * @author Hong Qiaowei
 */
abstract class WebClientConfig {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final int default_time_out = 30_000; // ms


    private int     readTimeout      = default_time_out;

    private int     writeTimeout     = default_time_out;

    private int     connTimeout      = default_time_out;

//  private int     responseTimeout  = default_time_out;

    private boolean tcpNodeLay       = true;

    private boolean keepAlive        = true;

    private boolean compress         = false;

    private boolean trustInsecureSSL = false;

    private boolean wiretap          = false;


    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public int getConnTimeout() {
        return connTimeout;
    }

    public void setConnTimeout(int connTimeout) {
        this.connTimeout = connTimeout;
    }

    public boolean isTcpNodeLay() {
        return tcpNodeLay;
    }

    public void setTcpNodeLay(boolean tcpNodeLay) {
        this.tcpNodeLay = tcpNodeLay;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public boolean isTrustInsecureSSL() {
        return trustInsecureSSL;
    }

    public void setTrustInsecureSSL(boolean trustInsecureSSL) {
        this.trustInsecureSSL = trustInsecureSSL;
    }

    public boolean isWiretap() {
        return wiretap;
    }

    public void setWiretap(boolean wiretap) {
        this.wiretap = wiretap;
    }

}
