/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.monitor.config;

import com.huaweicloud.sermant.core.config.common.ConfigFieldKey;
import com.huaweicloud.sermant.core.config.common.ConfigTypeKey;
import com.huaweicloud.sermant.core.plugin.config.PluginConfig;

/**
 * 配置类
 *
 * @author zhp
 * @version 1.0.0
 * @since 2022-08-02
 */
@ConfigTypeKey(value = "monitor.config")
public class MonitorServiceConfig implements PluginConfig {

    /**
     * 服务开关
     */
    @ConfigFieldKey("enable-start-service")
    private boolean enableStartService;

    /**
     * 性能监控地址
     */
    private String address;

    /**
     * 性能监控端口
     */
    private int port;

    public boolean isEnableStartService() {
        return enableStartService;
    }

    public void setEnableStartService(boolean enableStartService) {
        this.enableStartService = enableStartService;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
