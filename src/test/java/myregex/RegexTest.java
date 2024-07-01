package myregex;

import org.junit.Assert;
import org.junit.Test;

public class RegexTest extends Assert {

    @Test
    public void testExtra() {
        Regex regex = new Regex("(a|a|a|a)bc");
        Matcher matcher = regex.matcher("abc");
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
        regex = new Regex("a(b|c)(c|b)c+");
        Pattern pattern = regex.compile();
        matcher = pattern.matcher("abbc");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("accc");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("abcc");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("acbc");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("acccc");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("abb");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        //////////////////////////////////////////////////////

        regex = new Regex("a(bc)|(bb)|(cd)|(cc)c{0,}c");
        pattern = regex.compile();
        matcher = pattern.matcher("abc");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("bb");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("cd");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("ccc");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("cccc");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("cc");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        //////////////////////////////////////////////////////
        regex = new Regex("((ab)|(2:ac))");
        pattern = regex.compile();
        matcher = pattern.matcher("ab");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertTrue(iterator.hasNext());
        Pair<Integer, String> result = iterator.next();
        assertEquals(2, result.first().intValue());
        assertNull(result.second());
        assertNull(matcher.group(2));
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("ac");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertTrue(iterator.hasNext());
        result = iterator.next();
        assertNotNull(result.second());
        assertEquals(2, result.first().intValue());
        assertEquals("ac", result.second());
        assertEquals("ac", matcher.group(2));
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("a");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        //////////////////////////////////////////////////////
        regex = new Regex("cef(1:ab+)bb");
        pattern = regex.compile();
        matcher = pattern.matcher("cefabbb");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertTrue(iterator.hasNext());
        result = iterator.next();
        assertNotNull(result.second());
        assertEquals(1, result.first().intValue());
        assertEquals("abbb", result.second());
        assertEquals("abbb", matcher.group(1));
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("cefabbbb");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertTrue(iterator.hasNext());
        result = iterator.next();
        assertNotNull(result.second());
        assertEquals(1, result.first().intValue());
        assertEquals("abbbb", result.second());
        assertEquals("abbbb", matcher.group(1));
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("cefab");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        //////////////////////////////////////////////////////
        regex = new Regex("ac(1:ab|ac)ab");
        pattern = regex.compile();
        matcher = pattern.matcher("acacab");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertTrue(iterator.hasNext());
        result = iterator.next();
        assertNotNull(result.second());
        assertEquals(1, result.first().intValue());
        assertEquals("ac", result.second());
        assertEquals("ac", matcher.group(1));
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("acabab");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertTrue(iterator.hasNext());
        result = iterator.next();
        assertNotNull(result.second());
        assertEquals(1, result.first().intValue());
        assertEquals("ab", result.second());
        assertEquals("ab", matcher.group(1));
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("abab");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());

        //////////////////////////////////////////////////////
        regex = new Regex("b(1:baa)+a");
        pattern = regex.compile();
        matcher = pattern.matcher("bbaabaaa");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertTrue(iterator.hasNext());
        result = iterator.next();
        assertNotNull(result.second());
        assertEquals(1, result.first().intValue());
        assertEquals("baa", result.second());
        assertEquals("baa", matcher.group(1));
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("bbaabaabaaa");
        actual = matcher.match();
        assertTrue(actual);
        iterator = matcher.iterator();
        assertTrue(iterator.hasNext());
        result = iterator.next();
        assertNotNull(result.second());
        assertEquals(1, result.first().intValue());
        assertEquals("baa", result.second());
        assertEquals("baa", matcher.group(1));
        assertFalse(iterator.hasNext());

        matcher = pattern.matcher("bbaa");
        actual = matcher.match();
        assertFalse(actual);
        iterator = matcher.iterator();
        assertFalse(iterator.hasNext());
    }

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
