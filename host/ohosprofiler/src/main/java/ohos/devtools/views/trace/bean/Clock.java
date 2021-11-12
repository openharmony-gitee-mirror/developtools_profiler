/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
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

package ohos.devtools.views.trace.bean;

import ohos.devtools.views.trace.DField;

/**
 * Clock
 *
 * @since 2021/08/25 00:31
 */
public class Clock {
    @DField(name = "name")
    private String name;
    @DField(name = "srcname")
    private String srcname;
    @DField(name = "num")
    private Integer num;

    /**
     * get clock name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * set clock name
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get source name
     *
     * @return source name
     */
    public String getSrcname() {
        return srcname;
    }

    /**
     * set source name
     *
     * @param srcname source name
     */
    public void setSrcname(String srcname) {
        this.srcname = srcname;
    }

    /**
     * get clock num
     *
     * @return num
     */
    public Integer getNum() {
        return num;
    }

    /**
     * set clock num
     *
     * @param num num
     */
    public void setNum(Integer num) {
        this.num = num;
    }
}
