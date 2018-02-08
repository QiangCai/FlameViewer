package com.github.kornilova_l.flamegraph.plugin.server.trees.converters.flamegraph_format_trees

import com.github.kornilova_l.flamegraph.plugin.server.trees.FileToCallTracesConverter.Companion.UniqueStringsKeeper
import com.github.kornilova_l.flamegraph.plugin.server.trees.TreeBuilder
import com.github.kornilova_l.flamegraph.plugin.server.trees.util.TreesUtil
import com.github.kornilova_l.flamegraph.plugin.server.trees.util.TreesUtil.setNodesCount
import com.github.kornilova_l.flamegraph.plugin.server.trees.util.TreesUtil.setNodesOffsetRecursively
import com.github.kornilova_l.flamegraph.plugin.server.trees.util.TreesUtil.updateNodeList
import com.github.kornilova_l.flamegraph.proto.TreeProtos.Tree

/**
 * Builds tree in which methods does not have
 * return value and parameters
 */
open class SimpleStacksOCTreeBuilder(stacks: Map<String, Int>) : TreeBuilder {
    private val treeBuilder: Tree.Builder = Tree.newBuilder()
    private val tree: Tree
    private var maxDepth = 0
    private val uniqueStrings = UniqueStringsKeeper()

    init {
        tree = buildTree(stacks)
    }

    private fun buildTree(stacks: Map<String, Int>): Tree {
        treeBuilder.setBaseNode(Tree.Node.newBuilder())
        processStacks(stacks)
        setNodesOffsetRecursively(treeBuilder.baseNodeBuilder, 0)
        TreesUtil.setTreeWidth(treeBuilder)
        setNodesCount(treeBuilder)
        treeBuilder.depth = maxDepth
        return treeBuilder.build()
    }

    private fun processStacks(stacks: Map<String, Int>) {
        for (stack in stacks.entries) {
            addStackToTree(stack)
        }
    }

    private fun addStackToTree(stack: Map.Entry<String, Int>) {
        val width = stack.value
        val calls = stack.key.split(";".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        if (calls.size > maxDepth) {
            maxDepth = calls.size
        }
        var nodeBuilder: Tree.Node.Builder = treeBuilder.baseNodeBuilder
        for (call in calls) {
            nodeBuilder = updateNodeList(nodeBuilder,
                    uniqueStrings.getUniqueString(getClassName(call)),
                    uniqueStrings.getUniqueString(getMethodName(call)),
                    uniqueStrings.getUniqueString(getDescription(call)), width.toLong())
        }
    }

    protected open fun getClassName(call: String): String = ""

    protected open fun getMethodName(call: String): String = call

    protected open fun getDescription(call: String): String = ""

    override fun getTree(): Tree? {
        return tree
    }
}