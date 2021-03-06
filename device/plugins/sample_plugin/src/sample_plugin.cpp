/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include "sample_plugin.h"
#include "securec.h"

SamplePlugin::SamplePlugin() {}

SamplePlugin::~SamplePlugin() {}

uint64_t SamplePlugin::GetTimeMS()
{
    const int MS_PER_S = 1000;
    const int NS_PER_MS = 1000000;
    struct timespec ts;
    clock_gettime(CLOCK_BOOTTIME, &ts);
    return ts.tv_sec * MS_PER_S + ts.tv_nsec / NS_PER_MS;
}

int SamplePlugin::Start(const uint8_t* configData, uint32_t configSize)
{
    HILOG_INFO(LOG_CORE, "SamplePlugin: config data -->configSize=%d", configSize);
    CHECK_TRUE(configData != nullptr, -1, "SamplePlugin: param invalid!!!");
    for (uint32_t i = 0; i < configSize; i++) {
        HILOG_INFO(LOG_CORE, "0x%02x", configData[i]);
    }

    // 反序列化
    if (protoConfig_.ParseFromArray(configData, configSize) <= 0) {
        HILOG_ERROR(LOG_CORE, "SamplePlugin: ParseFromArray failed");
        return -1;
    }
    HILOG_INFO(LOG_CORE, "ParseFromArray --> %d", protoConfig_.pid());
    // 插件准备工作

    return 0;
}

int SamplePlugin::Report(uint8_t* data, uint32_t dataSize)
{
    SampleData dataProto;

    // 回填数据
    dataProto.set_time_ms(GetTimeMS());

    uint32_t length = dataProto.ByteSizeLong();
    if (length > dataSize) {
        return -length;
    }
    // 序列化
    if (dataProto.SerializeToArray(data, length) > 0) {
        HILOG_DEBUG(LOG_CORE, "SamplePlugin: report success! length = %d", length);
        return length;
    }
    return 0;
}

int SamplePlugin::Stop()
{
    HILOG_INFO(LOG_CORE, "SamplePlugin: stop success!");
    return 0;
}