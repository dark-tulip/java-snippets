public class SpamAnalyzer extends KeywordsAnalyzer {

    protected String[] keywords;

    public SpamAnalyzer(String[] keywords){
        this.keywords = keywords;
    }

    protected String[] getKeywords() {
        return this.keywords;
    }

    protected Label getLabel() {
        return Label.SPAM;
    }

}
