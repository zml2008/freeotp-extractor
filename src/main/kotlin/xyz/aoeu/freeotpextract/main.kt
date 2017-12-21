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

import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import org.fedorahosted.freeotp.Token
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path


fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Incorrect arguments. Usage: $0 <persistence file>")
    }

    val inputStore = FileSystems.getDefault().getPath(args[0])
    val outputDir = FileSystems.getDefault().getPath("images/")
    val data: List<SharedPersistenceEntry> =  readEntriesFromXml(inputStore)
    val gson = Gson()

    if (!Files.isDirectory(outputDir)) {
        Files.createDirectories(outputDir)
    }

    for (element in data) {
        //println("El: ${element.name}: ${element.value}")
        if (element.name != "tokenOrder") {
            val token = gson.fromJson<Token>(element.value, Token::class.java)
            println("URL: ${token.toString()}")
            toQrCodeImg(token, outputDir.resolve(element.name + ".png"))

        }
    }
}

private val qrGen = QRCodeWriter()
fun toQrCode(token: Token): BitMatrix {
    return qrGen.encode(token.toString(), BarcodeFormat.QR_CODE, 512, 512)
}

fun toQrCodeImg(token: Token, destPath: Path) {
    val code = toQrCode(token)
    MatrixToImageWriter.writeToPath(code, "png", destPath)
}

