package com.github.korniloval.flameviewer.converters.calltraces

import com.github.kornilova_l.flamegraph.proto.TreeProtos
import com.github.korniloval.flameviewer.FlameIndicator
import com.github.korniloval.flameviewer.converters.ConversionException
import com.github.korniloval.flameviewer.converters.Converter
import com.github.korniloval.flameviewer.converters.calltraces.StacksParser.getStacks
import java.io.File

/**
 * @author Liudmila Kornilova
 **/
@Deprecated("Use FlamegraphToCFlamegraphConverter instead")
class FlamegraphToCallTracesConverter(private val file: File) : Converter<TreeProtos.Tree> {
    @Throws(ConversionException::class)
    override fun convert(indicator: FlameIndicator?): TreeProtos.Tree {
        val stacks = getStacks(file, indicator) ?: throw ConversionException("Cannot get stacks from file $file")
        val tree: TreeProtos.Tree?
        tree = StacksToTreeBuilder(stacks).tree
        if (tree == null) {
            throw ConversionException("Cannot construct tree. File: $file")
        }
        return tree
    }

    companion object {
        const val EXTENSION = "flamegraph"
    }
}
