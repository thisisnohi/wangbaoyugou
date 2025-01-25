package nohi.boot.kqjs.consts;


import nohi.boot.common.consts.Dict;

/**
 * <h3>ccb-timesheet</h3>
 *
 * @author NOHI
 * @description <p>群报数常量类</p>
 * @date 2025/01/24 10:41
 **/
public class Qun100Consts {
    // token 参数 key
    public static final String PUB_PARA_QUN100_TOKEN = "QUN100_TOKEN";
    public static final String PUB_PARA_QUN100_FORMID = "QUN100_FORMID";

    public enum RespCode implements Dict {
        SUC("0", "成功"),
        ;
        String key;
        String val;

        RespCode(String key, String val) {
            this.key = key;
            this.val = val;
        }

        @Override
        public String getType() {
            return this.getClass().getName();
        }

        @Override
        public String getCode() {
            return this.key;
        }

        @Override
        public String getValue() {
            return this.val;
        }
    }
}
