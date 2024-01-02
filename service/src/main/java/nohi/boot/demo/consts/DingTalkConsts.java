package nohi.boot.demo.consts;

import com.dingtalk.api.DingTalkConstants;
import nohi.boot.common.consts.Dict;

/**
 * @author NOHI
 * @program: nohi-dd-miniprogram-server
 * @description:
 * @create 2021-01-06 17:16
 **/
public class DingTalkConsts extends DingTalkConstants {
    public static final String ROOT_SEND = "/robot/send";
    public enum RespCode implements Dict {
        SUC("0","成功"),
        ;
        private String key;

        private String val;

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
