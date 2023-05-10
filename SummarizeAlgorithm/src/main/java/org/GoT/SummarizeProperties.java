package org.GoT;

public record SummarizeProperties(Integer maxSentences, Integer min_length) {
    private static final SummarizeProperties defaultValue;

    static  {
        defaultValue = new SummarizeProperties(5, 128);
    }

    public static SummarizeProperties getDefault() {
        return defaultValue;
    }

    public SummarizeProperties(Integer maxSentences, Integer min_length) {
        this.maxSentences = maxSentences == null ? defaultValue.maxSentences : maxSentences;
        this.min_length = min_length == null ? defaultValue.min_length : min_length;
    }
}
