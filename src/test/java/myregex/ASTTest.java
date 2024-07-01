package myregex;

import org.junit.Assert;
import org.junit.Test;

public class ASTTest extends Assert {

    @Test
    public void testEmpty() {
        AST ast = new AST("");
        String expected = "EPSILON";
        String actual = ast.toString();
        assertEquals(expected, actual);

        ast = new AST("()");
        expected = "EPSILON";
        actual = ast.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testNodeNode() {
        AST ast = new AST("ast");
        String expected = "DOT DOT SYMBOL SYMBOL SYMBOL";
        String actual = ast.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testDot() {
        AST ast = new AST("a.s.t");
        String expected = "DOT DOT SYMBOL SYMBOL SYMBOL";
        String actual = ast.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testShielding() {
        AST ast = new AST("a#.s");
        String expected = "DOT DOT SYMBOL SYMBOL SYMBOL";
        String actual = ast.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testEpsilon() {
        AST ast = new AST("a^t");
        String expected = "DOT DOT SYMBOL EPSILON SYMBOL";
        String actual = ast.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testPlus() {
        AST ast = new AST("a+");
        String expected = "PLUS SYMBOL";
        String actual = ast.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testRepeat() {
        AST ast = new AST("a{2,5}");
        String expected = "REPEAT SYMBOL";
        String actual = ast.toString();
        assertEquals(expected, actual);

        ast = new AST("a{2,}");
        expected = "REPEAT SYMBOL";
        actual = ast.toString();
        assertEquals(expected, actual);

        ast = new AST("a+{0,1}");
        expected = "REPEAT PLUS SYMBOL";
        actual = ast.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testOr() {
        AST ast = new AST("a|b");
        String expected = "PIPE SYMBOL SYMBOL";
        String actual = ast.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testGroup() {
        AST ast = new AST("(2:a)");
        String expected = "GROUP SYMBOL";
        String actual = ast.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testPriority() {
        AST ast = new AST("a(a.d)+{2,3}");
        String expected = "DOT SYMBOL REPEAT PLUS DOT SYMBOL SYMBOL";
        String actual = ast.toString();
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInOpenParenthesis1() {
        AST ast = new AST("(a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInOpenParenthesis2() {
        AST ast = new AST("(");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInCloseParenthesis() {
        AST ast = new AST("a)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInRepeat1() {
        AST ast = new AST("a{2}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInRepeat2() {
        AST ast = new AST("a{a, 3}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInRepeat3() {
        AST ast = new AST("a{2,");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInRepeat4() {
        AST ast = new AST("a{2,a}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInRepeat5() {
        AST ast = new AST("a{,");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInRepeat6() {
        AST ast = new AST("a{");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInGroup1() {
        AST ast = new AST("(:a)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInGroup2() {
        AST ast = new AST("(a:a)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInGroup3() {
        AST ast = new AST(":)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInTwoOperation1() {
        AST ast = new AST(".+");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInTwoOperation2() {
        AST ast = new AST("a.");
    }

}
