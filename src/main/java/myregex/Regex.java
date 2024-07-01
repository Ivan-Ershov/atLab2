package myregex;

public class Regex {

    private final String regularExpression;
    public final RegexType type;

    public Regex(String regularExpression) {
        this.regularExpression = regularExpression;
        this.type = RegexType.REGEX;
    }

    public Regex(String regularExpression, RegexType type) {

        if (type == null) {
            throw new IllegalArgumentException("!!!Error: type is null!!!");
        }

        this.regularExpression = regularExpression;
        this.type = type;

    }

    public boolean match(String input) {
        return compile().matcher(input).match();
    }

    public Matcher matcher(String input) {
        return compile().matcher(input);
    }

    public Pattern compile() {
        DFM dfm = switch (type) {
            case REGEX -> {
                AST ast = new AST(regularExpression);
                NFM nfm = NFM.buildFromAst(ast);
                yield new DFM(nfm);
            }
            case ALL_STRINGS -> DFM.getAllStrings();
            case NONE_STRINGS -> DFM.getNoneStrings();
        };
        return new Pattern(dfm);
    }

    @Override
    public String toString() {
        return switch (type) {
            case REGEX -> "Regex: ".concat(regularExpression);
            case ALL_STRINGS -> "All strings";
            case NONE_STRINGS -> "None strings";
        };
    }
}
