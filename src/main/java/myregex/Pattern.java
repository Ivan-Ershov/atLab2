package myregex;

public class Pattern {

    private final DFM dfm;
    Pattern(DFM dfm) {
        this.dfm = dfm;
    }

    public boolean match(String input) {
        return matcher(input).match();
    }

    public Matcher matcher(String input) {
        return new Matcher(input, dfm);
    }

    public Pattern getResidual(Pattern pattern) {
        return new Pattern(getResidual(dfm, pattern.dfm));
    }

    private static DFM getResidual(DFM first, DFM second) {
        return first.multiplication(second, true, false);
    }

    public Pattern getAddition() {
        DFM result = getResidual(DFM.getAllStrings(), dfm);
        result.makeErrorIsEnd();
        return new Pattern(result);
    }

    public Regex convertToRegex() {
        Pair<RegexType, String> result = dfm.convertToRegex();
        return new Regex(result.second(), result.first());
    }

    @Override
    public String toString() {
        return "DFM:\n".concat(dfm.toString());
    }
}
