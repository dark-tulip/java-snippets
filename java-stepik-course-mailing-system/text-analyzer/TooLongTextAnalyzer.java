public class TooLongTextAnalyzer implements TextAnalyzer {

    final int maxLength;

    public TooLongTextAnalyzer(int maxLength) {
        this.maxLength = maxLength;
    }

    public Label processText(String text) {
        if (text.length() > this.maxLength)
            return Label.TOO_LONG;
        return Label.OK;
    }
}
