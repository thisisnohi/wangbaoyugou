package nohi.boot.kqjs.dto.qun100;


import lombok.Data;

/**
 * <h3>ccb-timesheet</h3>
 *
 * @author NOHI
 * @description <p>token</p>
 * @date 2025/01/24 15:00
 **/
@Data
public class TokenResp {
    private String accessToken;
    private int expireInSecs;
}
