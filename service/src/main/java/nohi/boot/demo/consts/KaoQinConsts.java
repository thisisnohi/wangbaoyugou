package nohi.boot.demo.consts;

import nohi.boot.common.consts.Dict;

/**
 * <h3>ccb-timesheet</h3>
 *
 * @author NOHI
 * @description <p>考勤</p>
 * @date 2024/01/02 14:31
 **/
public class KaoQinConsts {

    /**
     * 上下班类型
     */
    public enum DutyType implements Dict {
        // 上班
        ON_DUTY("ON_DUTY", "08:30"),
        OFF_DUTY("OFF_DUTY", "18:00"),
        ;
        private String code;
        private String value;

        DutyType(String code, String value) {
            this.code = code;
            this.value = value;
        }

        @Override
        public String getType() {
            return "DutyType";
        }

        @Override
        public String getCode() {
            return this.code;
        }

        @Override
        public String getValue() {
            return this.value;
        }
    }
}
