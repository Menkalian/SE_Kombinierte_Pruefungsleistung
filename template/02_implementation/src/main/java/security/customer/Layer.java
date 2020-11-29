package security.customer;

public class Layer {

    private char[] content;


    public Layer (String content) {
        this.content = content.toCharArray();
    }


    public char[] getContent () {
        return content;
    }

    public void setContent (char[] content) {
        this.content = content;
    }
}
