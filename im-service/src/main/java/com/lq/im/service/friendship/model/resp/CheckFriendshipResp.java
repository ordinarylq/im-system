package com.lq.im.service.friendship.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckFriendshipResp {

    /**
     * 应用id
     */
    private Integer appId;

    /**
     * 用户id
     */
    private String fromId;

    /**
     * 好友id
     */
    private String toId;

    /**
     * 好友关系状态<br/>
     * 单向校验：<br/>
     * 1- A好友列表中有B<br/>
     * 0- A好友列表中没有B<br/>
     * 双向校验：<br/>
     * 1- A好友列表中有B，B好友列表中有A<br/>
     * 2- A好友列表中有B，B好友列表中没有A<br/>
     * 3- A好友列表中没有B，B好友列表中有A<br/>
     * 4- A好友列表中没有B，B好友列表中没有A<br/>
     * <br/>
     * 黑名单状态，类似好友关系状态<br/>
     * 单向校验： <br/>
     * 1- A没有拉黑B <br/>
     * 0- A拉黑了B <br/>
     * 双向校验：<br/>
     * 1- A没有拉黑B，B没有拉黑A<br/>
     * 2- A没有拉黑B，B拉黑了A<br/>
     * 3- A拉黑了B，B没有拉黑A<br/>
     * 4- A拉黑了B，B拉黑了A<br/>
     * @see <a href="https://cloud.tencent.com/document/product/269/1501#.E6.A0.A1.E9.AA.8C.E5.A5.BD.E5.8F.8B">腾讯IM校验好友关系</a>
     * @see <a href="https://cloud.tencent.com/document/product/269/1501#.E6.A0.A1.E9.AA.8C.E9.BB.91.E5.90.8D.E5.8D.95">腾讯IM校验黑名单</a>
     */
    private Integer status;
}
