abstract class KeywordsAnalyzer implements TextAnalyzer {

    abstract protected String[] getKeywords();

    abstract protected Label getLabel();

    @Override
    public Label processText(String text) {

        for(String keyword : this.getKeywords()) {
            if (text.contains(keyword)) {
                return this.getLabel();
            }
        }
        return Label.OK;
    }
}
