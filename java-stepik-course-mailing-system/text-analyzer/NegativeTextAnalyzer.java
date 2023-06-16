public class NegativeTextAnalyzer extends KeywordsAnalyzer {

    protected String[] keywords;

    public NegativeTextAnalyzer() {
        this.keywords = new String[] {":(", "=(", ":|"};
    };

    protected String[] getKeywords() {
        return this.keywords;
    }

    protected Label getLabel() {
        return Label.NEGATIVE_TEXT;
    }

}
