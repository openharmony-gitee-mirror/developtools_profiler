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
select TB.tid,TB.name as thread ,TA.cpu,TC.pid,TC.name as process
from sched TA
left join internal_thread TB on TA.utid = TB.id
left join internal_process TC on TB.upid = TC.id
where utid = (select utid from raw where name = 'sched_waking' and ts = %d )
    and TA.ts < %d
    and Ta.ts + Ta.dur >= %d