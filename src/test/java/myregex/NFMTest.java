package myregex;

import org.junit.Assert;
import org.junit.Test;

public class NFMTest extends Assert {

    @Test
    public void testDot() {
        AST ast = new AST("ast");
        NFM nfm = NFM.buildFromAst(ast);
        String expected = """
                0: ((a, 1))
                1: (2)
                2: ((s, 3))
                3: (4)
                4: ((t, 5))
                5:
                """;
        String actual = nfm.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testEpsilon() {
        AST ast = new AST("a^s");
        NFM nfm = NFM.buildFromAst(ast);
        String expected = """
                0: ((a, 1))
                1: (2)
                2: (3)
                3: (4)
                4: ((s, 5))
                5:
                """;
        String actual = nfm.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testPlus() {
        AST ast = new AST("a+");
        NFM nfm = NFM.buildFromAst(ast);
        String expected = """
                0: (1)
                1: ((a, 2))
                2: (3) (1)
                3:
                """;
        String actual = nfm.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testOr() {
        AST ast = new AST("a|b");
        NFM nfm = NFM.buildFromAst(ast);
        String expected = """
                0: (1) (2)
                1: ((a, 3))
                2: ((b, 4))
                3: (5)
                4: (5)
                5:
                """;
        String actual = nfm.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testGroup() {
        AST ast = new AST("(2:a)");
        NFM nfm = NFM.buildFromAst(ast);
        String expected = """
                0: ((a, 1), Actions: 2)
                1:
                """;
        String actual = nfm.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testRepeat() {
        AST ast = new AST("a{0,1}");
        NFM nfm = NFM.buildFromAst(ast);
        String expected = """
                0: (1)
                1: (2)
                2: (3) (4)
                3: (5)
                4: ((a, 6))
                5: (7)
                6: (7)
                7:
                """;
        String actual = nfm.toString();
        assertEquals(expected, actual);

        ast = new AST("a{2,4}");
        nfm = NFM.buildFromAst(ast);
        expected = """
                0: (1)
                1: (2)
                2: ((a, 3))
                3: (4)
                4: ((a, 5))
                5: (6)
                6: (7) (8)
                7: (9)
                8: ((a, 10))
                9: (11)
                10: (11)
                11: (12)
                12: (13) (14)
                13: (15)
                14: ((a, 16))
                15: (17)
                16: (17)
                17:
                """;
        actual = nfm.toString();
        assertEquals(expected, actual);

        ast = new AST("a{0,}");
        nfm = NFM.buildFromAst(ast);
        expected = """
                0: (1)
                1: (2)
                2: (3) (4)
                3: ((a, 5))
                4:
                5: (4) (3)
                """;
        actual = nfm.toString();
        assertEquals(expected, actual);

        ast = new AST("a{1,1}");
        nfm = NFM.buildFromAst(ast);
        expected = """
                0: (1)
                1: (2)
                2: ((a, 3))
                3:
                """;
        actual = nfm.toString();
        assertEquals(expected, actual);

        ast = new AST("a+{0,1}");
        nfm = NFM.buildFromAst(ast);
        expected = """
                0: (1)
                1: (2)
                2: (3) (4)
                3: (5)
                4: (6)
                5: (7)
                6: ((a, 8))
                7:
                8: (9) (6)
                9: (7)
                """;
        actual = nfm.toString();
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInRepeat() {
        AST ast = new AST("a{3,2}");
        NFM nfm = NFM.buildFromAst(ast);
    }

}
