package edu.postech.csed332.team3.MarkdownDoc.mdcomponents;

public class Emoji implements MarkdownString {
    private final String str;

    public Emoji(String str) {
        this.str = str;
    }

    @Override
    public String getHTMLString() {
        // TODO: 11/14/2020 What should we do here?
        return null;
    }
}
