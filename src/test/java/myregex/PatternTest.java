package myregex;

import org.junit.Assert;
import org.junit.Test;

public class PatternTest extends Assert {

    @Test
    public void testConvertToRegex() {
        Regex regex = new Regex("a");
        Pattern pattern = regex.compile();
        regex = pattern.convertToRegex();
        String expected = "Regex: (a)";
        String actual = regex.toString();
        assertEquals(expected, actual);

        regex = new Regex("ab");
        pattern = regex.compile();
        regex = pattern.convertToRegex();
        expected = "Regex: ((a)(b))";
        actual = regex.toString();
        assertEquals(expected, actual);

        regex = new Regex("a+");
        pattern = regex.compile();
        regex = pattern.convertToRegex();
        expected = "Regex: (a)(a){0,}";
        actual = regex.toString();
        assertEquals(expected, actual);

        regex = new Regex("a|b");
        pattern = regex.compile();
        regex = pattern.convertToRegex();
        expected = "Regex: (b)|(a)";
        actual = regex.toString();
        assertEquals(expected, actual);

        regex = new Regex("a{0,}");
        pattern = regex.compile();
        regex = pattern.convertToRegex();
        expected = "Regex: (a)(a){0,}|^";
        actual = regex.toString();
        assertEquals(expected, actual);

        regex = new Regex("a{1,}");
        pattern = regex.compile();
        regex = pattern.convertToRegex();
        expected = "Regex: ((a)((a){0,1}))(a){0,}";
        actual = regex.toString();
        assertEquals(expected, actual);

        regex = new Regex("a{2,4}");
        pattern = regex.compile();
        regex = pattern.convertToRegex();
        expected = "Regex: ((((a)(a))((a){0,1}))((a){0,1}))";
        actual = regex.toString();
        assertEquals(expected, actual);

        regex = new Regex("a{0,1}");
        pattern = regex.compile();
        regex = pattern.convertToRegex();
        expected = "Regex: (a)|^";
        actual = regex.toString();
        assertEquals(expected, actual);

        regex = new Regex("a+{0,1}");
        pattern = regex.compile();
        regex = pattern.convertToRegex();
        expected = "Regex: (a)(a){0,}|^";
        actual = regex.toString();
        assertEquals(expected, actual);

        regex = new Regex("");
        pattern = regex.compile();
        regex = pattern.convertToRegex();
        expected = "Regex: ^";
        actual = regex.toString();
        assertEquals(expected, actual);

        regex = new Regex("a(d|f)");
        pattern = regex.compile();
        regex = pattern.convertToRegex();
        expected = "Regex: ((a)(d))|((a)(f))";
        actual = regex.toString();
        assertEquals(expected, actual);

    }

    @Test
    public void testAddition() {
        Regex regex = new Regex("a");
        Pattern pattern = regex.compile();
        pattern = pattern.getAddition();
        String expected = """
                DFM:
                0: ((a, 1)) Is end
                1:
                Error: Is end
                """;
        String actual = pattern.toString();
        assertEquals(expected, actual);

        regex = new Regex("a{0,}");
        pattern = regex.compile();
        pattern = pattern.getAddition();
        expected = """
                DFM:
                0: ((a, 1))
                1: ((a, 1))
                Error: Is end
                """;
        actual = pattern.toString();
        assertEquals(expected, actual);

        regex = new Regex("a|b");
        pattern = regex.compile();
        pattern = pattern.getAddition();
        expected = """
                DFM:
                0: ((a, 1)) ((b, 2)) Is end
                1:
                2:
                Error: Is end
                """;
        actual = pattern.toString();
        assertEquals(expected, actual);
    }

}
