create or replace view view_month_date_compare as
select distinct aaa.username                                                                      AS username,
                aaa.work_date                                                                     AS work_date,
                aaa.大楼上班                                                                      AS 大楼上班,
                aaa.大楼下班                                                                      AS 大楼下班,
                aaa.大楼时差                                                                      AS 大楼时差,
                aaa.移动上班                                                                      AS 移动上班,
                aaa.移动下班                                                                      AS 移动下班,
                aaa.移动时差                                                                      AS 移动时差,
                aaa.DEDUCTION                                                                     AS DEDUCTION,
                aaa.MINS_JS                                                                       AS MINS_JS,
                aaa.DAYS_JS                                                                       AS DAYS_JS,
                aaa.DAYS_JS_RS  AS DAYS_JS_RS,
                (case
                     when ((aaa.大楼上班 is null) and (aaa.移动上班 is null)) then '未上班'
                     when ((aaa.大楼下班 is null) and (aaa.移动下班 is null)) then '未下班'
                     when ((aaa.大楼上班 is null) or (aaa.移动上班 is null)) then '缺上班卡'
                     when ((aaa.大楼下班 is null) or (aaa.移动下班 is null)) then '缺下班卡' end) AS 缺卡状态,
                minute_diff(aaa.移动上班, aaa.大楼上班)                                           AS 上班分钟差,
                minute_diff(aaa.大楼下班, aaa.移动下班)                                           AS 下班分钟差
from (select dlyd.username  AS username,
             dlyd.work_date AS work_date,
             dlyd.大楼上班  AS 大楼上班,
             dlyd.大楼下班  AS 大楼下班,
             dlyd.大楼时差  AS 大楼时差,
             dlyd.移动上班  AS 移动上班,
             dlyd.移动下班  AS 移动下班,
             dlyd.移动时差  AS 移动时差,
             dlyd.DEDUCTION AS DEDUCTION,
             dlyd.MINS_JS   AS MINS_JS,
             dlyd.DAYS_JS   AS DAYS_JS,
             dlyd.DAYS_JS_RS AS DAYS_JS_RS
      from (
               -- 大楼有的考勤-开始
               select dl.username             AS username,
                      dl.work_date            AS work_date,
                      dl.start                AS 大楼上班,
                      dl.end                  AS 大楼下班,
                      floor((dl.时间差 / 60)) AS 大楼时差,
                      yd.start                AS 移动上班,
                      yd.end                  AS 移动下班,
                      floor((yd.时间差 / 60)) AS 移动时差,
                      yd.DEDUCTION            AS DEDUCTION,
                      yd.MINS_JS              AS MINS_JS,
                      yd.DAYS_JS              AS DAYS_JS,
                      yd.DAYS_JS_RS          AS DAYS_JS_RS
               from (
                        (select USERNAME                         AS username,
                                WORK_DATE                        AS work_date,
                                min(CARD_TIME)                   AS start,
                                max(CARD_TIME)                   AS end,
                                (unix_timestamp(max(CARD_TIME)) -
                                 unix_timestamp(min(CARD_TIME))) AS 时间差
                         from ccb_door_kq
                         group by USERNAME,
                                  WORK_DATE
                        ) dl  -- 大楼
                            left join (
                            select USERNAME                            AS username,
                                   WORK_DATE                           AS work_date,
                                   min(START_TIME)                     AS start,
                                   max(END_TIME)                       AS end,
                                   (unix_timestamp(max(END_TIME)) -
                                    unix_timestamp(min(START_TIME)))   AS 时间差,
                                   max(DEDUCTION)                      AS DEDUCTION,
                                   max(MINS_JS)                        AS MINS_JS,
                                   max(DAYS_JS)                        AS DAYS_JS,
                                   max(cast(REMARK as decimal(10, 2))) AS DAYS_JS_RS
                            from ccb_app_kq
                            group by USERNAME,
                                     WORK_DATE
                        ) yd -- 移动打卡
                        on dl.username = yd.username and dl.work_date = yd.work_date
                        )
           ) dlyd
      -- 大楼有的考勤-结束
      union all
      select yddl.username  AS username,
             yddl.work_date AS work_date,
             yddl.大楼上班  AS 大楼上班,
             yddl.大楼下班  AS 大楼下班,
             yddl.大楼时差  AS 大楼时差,
             yddl.移动上班  AS 移动上班,
             yddl.移动下班  AS 移动下班,
             yddl.移动时差  AS 移动时差,
             yddl.DEDUCTION AS DEDUCTION,
             yddl.MINS_JS   AS MINS_JS,
             yddl.DAYS_JS   AS DAYS_JS,
             yddl.DAYS_JS_RS   AS DAYS_JS_RS
      from (select yd.username             AS username,
                   yd.work_date            AS work_date,
                   dl.start                AS 大楼上班,
                   dl.end                  AS 大楼下班,
                   floor((dl.时间差 / 60)) AS 大楼时差,
                   yd.start                AS 移动上班,
                   yd.end                  AS 移动下班,
                   floor((yd.时间差 / 60)) AS 移动时差,
                   yd.DEDUCTION            AS DEDUCTION,
                   yd.MINS_JS              AS MINS_JS,
                   yd.DAYS_JS              AS DAYS_JS,
                   yd.DAYS_JS_RS              AS DAYS_JS_RS
            from (
                     (select USERNAME                          AS username,
                             WORK_DATE                         AS work_date,
                             min(START_TIME)                   AS start,
                             max(END_TIME)                     AS end,
                             (unix_timestamp(max(END_TIME)) -
                              unix_timestamp(min(START_TIME))) AS 时间差,
                             max(DEDUCTION)                    AS DEDUCTION,
                             max(MINS_JS)                      AS MINS_JS,
                             max(DAYS_JS)                      AS DAYS_JS,
                             max(cast(REMARK as decimal(10, 2))) AS DAYS_JS_RS
                      from ccb_app_kq
                      group by USERNAME,
                               WORK_DATE
                     ) yd
                         left join (
                         select USERNAME                         AS username,
                                WORK_DATE                        AS work_date,
                                min(CARD_TIME)                   AS start,
                                max(CARD_TIME)                   AS end,
                                (unix_timestamp(max(CARD_TIME)) -
                                 unix_timestamp(min(CARD_TIME))) AS 时间差
                         from ccb_door_kq
                         group by USERNAME, WORK_DATE
                     ) dl
                     on dl.username = yd.username and dl.work_date = yd.work_date
                     )
           ) yddl
     ) aaa
;

