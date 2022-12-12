package at.petrak.bemis;

import at.petrak.bemis.api.BemisApi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestUnsubs {
    @Test
    void testUnsubs() {
        var test = "Cross: &#x2020; or &#8224;. Euro: &#x20AC; or &#8364;. Lozenge: &#x25CA; or &#9674;";
        var target = "Cross: † or †. Euro: € or €. Lozenge: ◊ or ◊";

        assertEquals(target, BemisApi.get().unsubstituteAdoc(test, true));
    }
}
