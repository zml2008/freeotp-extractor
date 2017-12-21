/**
*   
*  Copyright 2017 zml [at] aoeu [dot] xyz
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package xyz.aoeu.freeotpextract

import java.nio.file.Files
import java.nio.file.Path
import javax.xml.stream.XMLEventFactory
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants
import javax.xml.stream.events.Attribute
import javax.xml.stream.events.XMLEvent

data class SharedPersistenceEntry(val name: String, val value: String)

enum class ReaderState {
    ROOT, MAP, STRING
}

fun readEntriesFromXml(path: Path): List<SharedPersistenceEntry> {
    val factory = XMLInputFactory.newInstance()
    return Files.newBufferedReader(path).use {
        val reader = factory.createXMLEventReader(it)
        val entries = mutableListOf<SharedPersistenceEntry>()
        var state = ReaderState.ROOT
        var key: String? = null
        val buf: StringBuilder = StringBuilder()

        eventLoop@ while (reader.hasNext()) {
            val event = reader.nextEvent()
            when (event.eventType) {
                XMLStreamConstants.START_ELEMENT -> {
                    val startEl = event.asStartElement()
                    when (startEl.name.localPart) {
                        "map" -> state = ReaderState.MAP
                        "string" -> {
                            if (state != ReaderState.MAP) {
                                throw Exception("you've gotta be in a map pal")
                            }
                            state = ReaderState.STRING
                            for (el in startEl.attributes) {
                                val attr = el as Attribute
                                if (attr.name.localPart == "name") {
                                    key = attr.value
                                }
                            }
                            if (key == null) {
                                throw Exception("there's no key what")
                            }
                        }
                        else -> throw Exception("Invalid key: ${startEl.name.localPart}")
                    }

                }
                XMLStreamConstants.CHARACTERS -> {
                    buf.append(event.asCharacters().data)
                }
                XMLStreamConstants.END_ELEMENT -> {
                    val endEl = event.asEndElement()
                    when (endEl.name.localPart) {
                        "map" -> {
                            if (state != ReaderState.MAP) {
                                throw Exception("what are you even trying to do help")
                            }
                            state = ReaderState.ROOT
                        }
                        "string" -> {
                            if (state != ReaderState.STRING) {
                                throw Exception("You need to be in a string to end a string right")
                            }
                            state = ReaderState.MAP
                            if (key == null) {
                                continue@eventLoop
                            }
                            entries.add(SharedPersistenceEntry(key as String, buf.toString()))
                            // reset
                            key = null
                            buf.delete(0, buf.length)
                        }
                        else -> throw Exception("Invalaid element: ${endEl.name.localPart}")
                    }
                }
            }
        }
        return entries
    }
}

