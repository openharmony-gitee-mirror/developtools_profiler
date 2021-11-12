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
select t1.filter_id as trackId,t2.name,value, t1.ts - t3.start_ts as startTime
from measure t1
left join process_measure_filter t2 on t1.filter_id = t2.id
left join trace_range t3 where filter_id in (%s)
and startTime <= %d
order by startTime asc;