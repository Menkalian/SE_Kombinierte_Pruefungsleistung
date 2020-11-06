package security.customer;

import org.jetbrains.annotations.NotNull;

public class Layer {
    @NotNull
    private char[] content;

    public Layer(String content){
        this.content = content.toCharArray();
    }

    @NotNull
    public char[] getContent () {
        return content;
    }

    public void setContent (@NotNull char[] content) {
        this.content = content;
    }
}
