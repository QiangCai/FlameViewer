package com.github.korniloval.flameviewer.cli

import com.github.korniloval.flameviewer.server.handlers.FindFile
import java.io.File


val cliFindFile: FindFile = { name ->
    val file = File(name)
    if (!file.exists()) null
    else file
}
