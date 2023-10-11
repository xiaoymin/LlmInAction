/*
 * THIS FILE IS PART OF Zhejiang LiShi Technology CO.,LTD.
 * Copyright (c) 2019-2023  Zhejiang LiShi Technology CO.,LTD.
 * It is forbidden to distribute or copy the code under this software without the consent of the Zhejiang LiShi Technology
 *
 *     https://www.lishiots.com/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.github.xiaoymin.llm.domain.llm;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @Classname ZpChatglmReq
 * @Description TODO
 * @Date 2023/7/13 15:21
 * @Author jerrylin
 */
@Data
public class ZhipuChatCompletion {
    
    private List<Prompt> prompt=new LinkedList<>();
    private float temperature;
    private float top_p;
    
    private String request_id;
    /**
     * SSE接口调用时，用于控制每次返回内容方式是增量还是全量，不提供此参数时默认为增量返回
     * - true 为增量返回
     * - false 为全量返回
     */
    private boolean incremental = true;

    public void addPrompt(String content){
        this.prompt.add(Prompt.buildOne(content));
    }
    
    @Data
    public static class Prompt {
        
        private String role = "user";
        private String content;

        public static Prompt buildOne(String content){
            Prompt prompt1=new Prompt();
            prompt1.setContent(content);
            return prompt1;
        }
    }
}
