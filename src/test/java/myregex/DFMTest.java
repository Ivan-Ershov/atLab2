package myregex;

import org.junit.Assert;
import org.junit.Test;

public class DFMTest extends Assert {

    @Test
    public void testMultiplication() {
        AST ast1 = new AST("ab");
        NFM nfm1 = NFM.buildFromAst(ast1);
        DFM dfm1 = new DFM(nfm1);
        AST ast2 = new AST("ab");
        NFM nfm2 = NFM.buildFromAst(ast2);
        DFM dfm2 = new DFM(nfm2);

        DFM dfm = dfm1.multiplication(dfm2, true, false);

        String expected = """
                0: ((a, 1))
                1: ((b, 2))
                2:
                """;
        String actual = dfm.toString();
        assertEquals(expected, actual);

        ast1 = new AST("a+");
        nfm1 = NFM.buildFromAst(ast1);
        dfm1 = new DFM(nfm1);
        ast2 = new AST("a");
        nfm2 = NFM.buildFromAst(ast2);
        dfm2 = new DFM(nfm2);

        dfm = dfm1.multiplication(dfm2, true, false);

        expected = """
                0: ((a, 1))
                1: ((a, 2))
                2: ((a, 2)) Is end
                """;
        actual = dfm.toString();
        assertEquals(expected, actual);

        ast1 = new AST("a|b");
        nfm1 = NFM.buildFromAst(ast1);
        dfm1 = new DFM(nfm1);
        ast2 = new AST("a");
        nfm2 = NFM.buildFromAst(ast2);
        dfm2 = new DFM(nfm2);

        dfm = dfm1.multiplication(dfm2, true, false);

        expected = """
                0: ((a, 1)) ((b, 2))
                1:
                2: Is end
                """;
        actual = dfm.toString();
        assertEquals(expected, actual);

        ast1 = new AST("a{0,}");
        nfm1 = NFM.buildFromAst(ast1);
        dfm1 = new DFM(nfm1);
        ast2 = new AST("a");
        nfm2 = NFM.buildFromAst(ast2);
        dfm2 = new DFM(nfm2);

        dfm = dfm1.multiplication(dfm2, true, false);

        expected = """
                0: ((a, 1)) Is end
                1: ((a, 2))
                2: ((a, 2)) Is end
                """;
        actual = dfm.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testDot() {
        AST ast = new AST("ast");
        NFM nfm = NFM.buildFromAst(ast);
        DFM dfm = new DFM(nfm);
        String expected = """
                0: ((a, 1))
                1: ((s, 2))
                2: ((t, 3))
                3: Is end
                """;
        String actual = dfm.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testEpsilon() {
        AST ast = new AST("a^s");
        NFM nfm = NFM.buildFromAst(ast);
        DFM dfm = new DFM(nfm);
        String expected = """
                0: ((a, 1))
                1: ((s, 2))
                2: Is end
                """;
        String actual = dfm.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testPlus() {
        AST ast = new AST("a+");
        NFM nfm = NFM.buildFromAst(ast);
        DFM dfm = new DFM(nfm);
        String expected = """
                0: ((a, 1))
                1: ((a, 1)) Is end
                """;
        String actual = dfm.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testOr() {
        AST ast = new AST("a|b");
        NFM nfm = NFM.buildFromAst(ast);
        DFM dfm = new DFM(nfm);
        String expected = """
                0: ((a, 1)) ((b, 2))
                1: Is end
                2: Is end
                """;
        String actual = dfm.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testGroup() {
        AST ast = new AST("(2:a)");
        NFM nfm = NFM.buildFromAst(ast);
        DFM dfm = new DFM(nfm);
        String expected = """
                0: ((a, 1), Action: 2)
                1: Is end
                """;
        String actual = dfm.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testRepeat() {
        AST ast = new AST("a{0,1}");
        NFM nfm = NFM.buildFromAst(ast);
        DFM dfm = new DFM(nfm);
        String expected = """
                0: ((a, 1)) Is end
                1: Is end
                """;
        String actual = dfm.toString();
        assertEquals(expected, actual);

        ast = new AST("a{2,4}");
        nfm = NFM.buildFromAst(ast);
        dfm = new DFM(nfm);
        expected = """
                0: ((a, 1))
                1: ((a, 2))
                2: ((a, 3)) Is end
                3: ((a, 4)) Is end
                4: Is end
                """;
        actual = dfm.toString();
        assertEquals(expected, actual);

        ast = new AST("a{0,}");
        nfm = NFM.buildFromAst(ast);
        dfm = new DFM(nfm);
        expected = """
                0: ((a, 1)) Is end
                1: ((a, 1)) Is end
                """;
        actual = dfm.toString();
        assertEquals(expected, actual);

        ast = new AST("a{1,1}");
        nfm = NFM.buildFromAst(ast);
        dfm = new DFM(nfm);
        expected = """
                0: ((a, 1))
                1: Is end
                """;
        actual = dfm.toString();
        assertEquals(expected, actual);

        ast = new AST("a+{0,1}");
        nfm = NFM.buildFromAst(ast);
        dfm = new DFM(nfm);
        expected = """
                0: ((a, 1)) Is end
                1: ((a, 1)) Is end
                """;
        actual = dfm.toString();
        assertEquals(expected, actual);
    }
}
