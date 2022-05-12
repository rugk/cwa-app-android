package de.rki.coronawarnapp.util.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

// Mainly used to bypass the annoying "Inappropriate blocking method call"
inline fun <reified T> ObjectMapper.writeToFile(file: File, data: T) = file.bufferedWriter().use {
    writeValue(it, data)
}
