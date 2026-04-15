package me.schattgen.felis.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LinkCleanerTest {

    @Test
    fun containsUrl_returnsTrue_whenUrlPresent() {
        assertTrue(LinkCleaner.containsUrl("Check this: https://example.com?a=1"))
    }

    @Test
    fun containsUrl_returnsFalse_whenNoUrlPresent() {
        assertFalse(LinkCleaner.containsUrl("there is no link here"))
    }

    @Test
    fun cleanUrl_removesGenericTrackingParams_andKeepsOthers() {
        val result = LinkCleaner.cleanUrl("https://example.com/path?utm_source=x&foo=1&utm_medium=y&bar=2&fbclid=abc")

        assertEquals("https://example.com/path?foo=1&bar=2", result.cleanedUrl)
        assertEquals(3, result.removedParamCount)
    }

    @Test
    fun cleanUrl_removesAllQueryParams_forSpotify() {
        val result = LinkCleaner.cleanUrl("https://open.spotify.com/track/abc123?si=xyz&utm_source=foo")

        assertEquals("https://open.spotify.com/track/abc123", result.cleanedUrl)
        assertEquals(2, result.removedParamCount)
    }

    @Test
    fun cleanUrl_removesAllQueryParams_forInstagram() {
        val result = LinkCleaner.cleanUrl("https://www.instagram.com/reel/ABC123/?igshid=one&utm_campaign=test")

        assertEquals("https://www.instagram.com/reel/ABC123/", result.cleanedUrl)
        assertEquals(2, result.removedParamCount)
    }

    @Test
    fun cleanUrl_normalizesAmazonDpPath_andDropsQuery() {
        val result = LinkCleaner.cleanUrl("https://www.amazon.de/Some-Product-Name/dp/B08TEST123/ref=something?tag=affiliate&utm_source=ads")

        assertEquals("https://www.amazon.de/dp/B08TEST123", result.cleanedUrl)
        assertEquals(2, result.removedParamCount)
    }

    @Test
    fun cleanUrl_normalizesAmazonGpProductPath_andDropsQuery() {
        val result = LinkCleaner.cleanUrl("https://www.amazon.com/gp/product/B0ABCDEF12?ref_=abc&utm_source=foo&gclid=123")

        assertEquals("https://www.amazon.com/dp/B0ABCDEF12", result.cleanedUrl)
        assertEquals(3, result.removedParamCount)
    }

    @Test
    fun cleanUrl_keepsRepeatedNonTrackingParams() {
        val result = LinkCleaner.cleanUrl("https://example.com/search?q=cat&q=dog&utm_source=foo")

        assertEquals("https://example.com/search?q=cat&q=dog", result.cleanedUrl)
        assertEquals(1, result.removedParamCount)
    }

    @Test
    fun cleanText_replacesMultipleUrls_andRecordsOnlyChangedOnes() {
        val text = "First https://example.com?utm_source=x then https://example.com?foo=bar end"

        val result = LinkCleaner.cleanText(text)

        assertEquals("First https://example.com then https://example.com?foo=bar end", result.cleanedText)
        assertEquals(1, result.records.size)
        assertEquals("https://example.com?utm_source=x", result.records[0].originalUrl)
        assertEquals("https://example.com", result.records[0].cleanedUrl)
        assertEquals(1, result.records[0].removedParamCount)
    }

    @Test
    fun cleanText_returnsSameText_andNoRecords_whenNothingToClean() {
        val text = "Visit https://example.com/path?foo=1 and enjoy"

        val result = LinkCleaner.cleanText(text)

        assertEquals(text, result.cleanedText)
        assertTrue(result.records.isEmpty())
    }

    @Test
    fun cleanUrl_returnsOriginal_whenInputIsNotAWebUrl() {
        val result = LinkCleaner.cleanUrl("not-a-url")

        assertEquals("not-a-url", result.cleanedUrl)
        assertEquals(0, result.removedParamCount)
    }
}
