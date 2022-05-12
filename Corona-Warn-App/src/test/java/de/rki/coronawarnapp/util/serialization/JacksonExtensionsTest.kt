package de.rki.coronawarnapp.util.serialization

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import testhelpers.BaseIOTest
import java.io.File
import java.util.UUID

class JacksonExtensionsTest : BaseIOTest() {

    private val testDir = File(IO_TEST_BASEDIR, this::class.java.simpleName)
    private val testFile = File(testDir, "testfile")
    private val objectMapper = SerializationModule().jacksonObjectMapper()

    @BeforeEach
    fun setup() {
        testDir.mkdirs()
    }

    @AfterEach
    fun teardown() {
        testDir.deleteRecursively()
    }

    @Test
    fun `serialize and deserialize`() {
        val testData = TestData(value = UUID.randomUUID().toString())
        objectMapper.writeToFile(testFile, testData)

        objectMapper.readValue<TestData>(testFile) shouldBe testData
    }
}

private data class TestData(
    val value: String
)
