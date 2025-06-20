package org.jetbrains.research.codecocoon.llm;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

public class OpenAI implements LLM{
    private final OpenAIClient client = OpenAIOkHttpClient.fromEnv();
    private final ChatModel chatModel;

    public OpenAI(String model) {
        super();
        if (model.equals("ChatGPT4o-mini")) {
            this.chatModel = ChatModel.GPT_4O_MINI;
        } else {
            throw new LLMUnavailableException("Unsupported model: " + model);
        }
    }

    @Override
    public String query(String prompt) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(prompt)
                .model(this.chatModel)
                .build();
        ChatCompletion chatCompletion = client.chat().completions().create(params);
        return chatCompletion.choices().get(0).message().content().get();
    }
}
