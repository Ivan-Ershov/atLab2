package myregex;

import org.junit.Assert;
import org.junit.Test;

public class RegexTest extends Assert {

    @Test
    public void testNotCompile() {
        Regex regex = new Regex("ast");
        Matcher matcher = regex.matcher("ast");
        boolean actual = matcher.match();
        assertTrue(actual);
        Matcher.Iterator iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = regex.matcher("as");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        //////////////////////////////////////////////////////
        regex = new Regex("a.s.t");
        Pattern pattern = regex.compile();
        matcher = pattern.matcher("ast");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("as");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        //////////////////////////////////////////////////////

        regex = new Regex("ast");
        actual = regex.match("ast");
        assertTrue(actual);

        actual = regex.match("as");
        assertFalse(actual);

        //////////////////////////////////////////////////////
        regex = new Regex("a.s.t");
        pattern = regex.compile();
        actual = pattern.match("ast");
        assertTrue(actual);

        actual = pattern.match("as");
        assertFalse(actual);

    }
    @Test
    public void testEmpty() {
        Regex regex = new Regex("");
        Pattern pattern = regex.compile();
        Matcher matcher = pattern.matcher("");
        boolean actual = matcher.match();
        assertTrue(actual);
        Matcher.Iterator iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("a");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        ////////////////////////////////////////////////////////

        regex = new Regex("()");
        pattern = regex.compile();
        matcher = pattern.matcher("");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("a");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

    }

    @Test
    public void testDot() {
        Regex regex = new Regex("ast");
        Pattern pattern = regex.compile();
        Matcher matcher = pattern.matcher("ast");
        boolean actual = matcher.match();
        assertTrue(actual);
        Matcher.Iterator iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("as");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        //////////////////////////////////////////////////////
        regex = new Regex("a.s.t");
        pattern = regex.compile();
        matcher = pattern.matcher("ast");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("as");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testShielding() {
        Regex regex = new Regex("a#.s");
        Pattern pattern = regex.compile();
        Matcher matcher = pattern.matcher("a.s");
        boolean actual = matcher.match();
        assertTrue(actual);
        Matcher.Iterator iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("as");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testEpsilon() {
        Regex regex = new Regex("a^t");
        Pattern pattern = regex.compile();
        Matcher matcher = pattern.matcher("at");
        boolean actual = matcher.match();
        assertTrue(actual);
        Matcher.Iterator iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("att");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testPlus() {
        Regex regex = new Regex("a+");
        Pattern pattern = regex.compile();
        Matcher matcher = pattern.matcher("a");
        boolean actual = matcher.match();
        assertTrue(actual);
        Matcher.Iterator iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("aa");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testRepeat() {
        Regex regex = new Regex("a{0,1}");
        Pattern pattern = regex.compile();
        Matcher matcher = pattern.matcher("");
        boolean actual = matcher.match();
        assertTrue(actual);
        Matcher.Iterator iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("a");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("aa");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        //////////////////////////////////////////////////////
        regex = new Regex("a{2,4}");
        pattern = regex.compile();
        matcher = pattern.matcher("aa");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("aaa");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("aaaa");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("a");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("aaaaa");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        //////////////////////////////////////////////////////
        regex = new Regex("a{0,}");
        pattern = regex.compile();
        matcher = pattern.matcher("");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("a");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("aaaaaa");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("b");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        //////////////////////////////////////////////////////
        regex = new Regex("a{1,1}");
        pattern = regex.compile();
        matcher = pattern.matcher("a");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("aa");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        //////////////////////////////////////////////////////
        regex = new Regex("a{1,}");
        pattern = regex.compile();
        matcher = pattern.matcher("a");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("aaaaa");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testOr() {
        Regex regex = new Regex("a|b");
        Pattern pattern = regex.compile();
        Matcher matcher = pattern.matcher("a");
        boolean actual = matcher.match();
        assertTrue(actual);
        Matcher.Iterator iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("b");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("ab");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testGroup() {
        Regex regex = new Regex("(2:a)");
        Pattern pattern = regex.compile();
        Matcher matcher = pattern.matcher("a");
        boolean actual = matcher.match();
        assertTrue(actual);
        Matcher.Iterator iterator = matcher.iterator();
        assertTrue(iterator.hasNext());
        Pair<Integer, String> result = iterator.next();
        assertNotNull(result.second());
        assertEquals(2, result.first().intValue());
        assertEquals("a", result.second());
        assertEquals("a", matcher.group(2));
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        //////////////////////////////////////////////////////
        regex = new Regex("(3:(2:a))");
        pattern = regex.compile();
        matcher = pattern.matcher("a");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertTrue(iterator.hasNext());
        result = iterator.next();
        assertNotNull(result.second());
        assertEquals(2, result.first().intValue());
        assertEquals("a", result.second());
        assertEquals("a", matcher.group(2));
        assertTrue(iterator.hasNext());
        result = iterator.next();
        assertNotNull(result.second());
        assertEquals(3, result.first().intValue());
        assertEquals("a", result.second());
        assertEquals("a", matcher.group(3));
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testPriority() {
        Regex regex = new Regex("a(d|f)");
        Pattern pattern = regex.compile();
        Matcher matcher = pattern.matcher("ad");
        boolean actual = matcher.match();
        assertTrue(actual);
        Matcher.Iterator iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("af");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("f");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInRepeat() {
        Regex regex = new Regex("a{3,2}");
        regex.compile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInOpenParenthesis1() {
        Regex regex = new Regex("(a");
        regex.compile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInOpenParenthesis2() {
        Regex regex = new Regex("(");
        regex.compile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInCloseParenthesis() {
        Regex regex = new Regex("a)");
        regex.compile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInRepeat1() {
        Regex regex = new Regex("a{2}");
        regex.compile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInRepeat2() {
        Regex regex = new Regex("a{a, 3}");
        regex.compile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInRepeat3() {
        Regex regex = new Regex("a{2,");
        regex.compile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInRepeat4() {
        Regex regex = new Regex("a{2,a}");
        regex.compile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInRepeat5() {
        Regex regex = new Regex("a{,");
        regex.compile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInRepeat6() {
        Regex regex = new Regex("a{");
        regex.compile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInGroup1() {
        Regex regex = new Regex("(:a)");
        regex.compile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInGroup2() {
        Regex regex = new Regex("(a:a)");
        regex.compile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInGroup3() {
        Regex regex = new Regex(":)");
        regex.compile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInTwoOperation1() {
        Regex regex = new Regex(".+");
        regex.compile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorInTwoOperation2() {
        Regex regex = new Regex("a.");
        regex.compile();
    }
}
