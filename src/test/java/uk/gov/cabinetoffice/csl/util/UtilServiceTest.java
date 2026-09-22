package uk.gov.cabinetoffice.csl.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilServiceTest {

    private UtilService utilService = new UtilService(Clock.systemUTC());

    @ParameterizedTest(name = "[{index}] input: \"{0}\" -> expected: \"{1}\"")
    @CsvSource(delimiter = '|', value = {
            "Hello World         | hello-world",
            "  Leading Trailing  | leading-trailing",
            "ALL CAPS TEXT       | all-caps-text",

            "Rock & Roll         | rock-and-roll",
            "Fish & Chips        | fish-and-chips",
            
            "Café au Lait        | cafe-au-lait",
            "München Hauptbahnhof| munchen-hauptbahnhof",
            "El Niño Weather     | el-nino-weather",
            "Château & Crème     | chateau-and-creme",
            "Façade Pattern      | facade-pattern",

            "What's Up? (100%)!  | whats-up-100",
            "User@Domain.com #1  | user-domain-com-1",
            "Price: $99.99 USD   | price-99-99-usd",
            "C++ and C# Language | c-and-c-language",

            "foo---bar           | foo-bar",
            "---start-and-end--- | start-and-end",
            "Space - Dash - Space | space-dash-space",

            "                    | ''",
            "''                  | ''"
    })
    void testGenerateUrlSlugFromString(String input, String expected) {
        String result = utilService.generateUrlSlugFromString(input, 100);
        assertEquals(expected, result);
    }

    @Test
    void testNullInput() {
        assertEquals("", utilService.generateUrlSlugFromString(null, 50));
    }

    @ParameterizedTest(name = "maxLength: {1} -> expected: \"{2}\"")
    @CsvSource(delimiter = '|', value = {
            "Supercalifragilisticexpialidocious | 5  | super",
            "Hello World                        | 7  | hello-w",
            "Hello World                        | 5  | hello",
            "Rock & Roll                        | 8  | rock-and"
    })
    void testSlugTruncation(String input, int maxLength, String expected) {
        String result = utilService.generateUrlSlugFromString(input, maxLength);
        assertEquals(expected, result);
    }
}
